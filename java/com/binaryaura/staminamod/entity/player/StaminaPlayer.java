package com.binaryaura.staminamod.entity.player;

import java.util.HashMap;
import java.util.Map;

import com.binaryaura.staminamod.util.Queue;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import net.minecraftforge.common.IExtendedEntityProperties;

public class StaminaPlayer implements IExtendedEntityProperties {
	
	public static enum StaminaType {
		STAMINA(20),
		CURRENT(21),
		MAXIMUM(22),
		ADRENALINE(23);
		
		private StaminaType(int meta) {
			this.meta = meta;
		}
		
		public int getMeta() {
			return this.meta;
		}
		
		private int meta;
	}
	
	public final static String NAME = "StaminaPlayer";
	private final static float DEFAULT_STAMINA = 2000.0F;

	public StaminaPlayer(EntityPlayer player) {
		this.player = player;
		this.dw = this.player.getDataWatcher();
		this.dw.addObject(StaminaType.STAMINA.getMeta(), DEFAULT_STAMINA);
		this.dw.addObject(StaminaType.CURRENT.getMeta(), DEFAULT_STAMINA);
		this.dw.addObject(StaminaType.MAXIMUM.getMeta(), DEFAULT_STAMINA);
		this.dw.addObject(StaminaType.ADRENALINE.getMeta(), 0.0F);
	}
	
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(NAME, new StaminaPlayer(player));
	}
	
	public static final StaminaPlayer get(EntityPlayer player) {
		return (StaminaPlayer)player.getExtendedProperties(NAME);
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		properties.setFloat("Stamina", this.dw.getWatchableObjectFloat(StaminaType.STAMINA.getMeta()));
		properties.setFloat("CurrentStamina", this.dw.getWatchableObjectFloat(StaminaType.CURRENT.getMeta()));
		properties.setFloat("MaximumStamina", this.dw.getWatchableObjectFloat(StaminaType.MAXIMUM.getMeta()));
		properties.setFloat("Adrenaline", this.dw.getWatchableObjectFloat(StaminaType.ADRENALINE.getMeta()));
		compound.setTag(NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound)compound.getTag(NAME);
		this.dw.updateObject(StaminaType.STAMINA.getMeta(), properties.getFloat("Stamina"));
		this.dw.updateObject(StaminaType.CURRENT.getMeta(), properties.getFloat("CurrentStamina"));
		this.dw.updateObject(StaminaType.MAXIMUM.getMeta(), properties.getFloat("MaximumStamina"));
		this.dw.updateObject(StaminaType.ADRENALINE.getMeta(), properties.getFloat("Adrenaline"));
	}

	@Override
	public void init(Entity entity, World world) { }
	
	public void addToQueue(StaminaType type, float amount) {
		if(type == null) throw new IllegalArgumentException("Type must not be null.");
		if(type.equals(StaminaType.CURRENT)) {
			currentStaminaQueue.add(amount);
		} else if(type.equals(StaminaType.MAXIMUM)) {
			maximumStaminaQueue.add(amount);
		}
	}
	
	public void addToQueue(StaminaType type, float amount, float time) {
		if(type == null) throw new IllegalArgumentException("Type must not be null.");
		if(time < 0) throw new IllegalArgumentException("Time must not be negative.");
		if(time == 0) addToQueue(type, amount);
		float delta = amount / (time * 40);
		if(type.equals(StaminaType.CURRENT)) {
			currentStaminaQueue.add(amount, delta);
		} else if(type.equals(StaminaType.MAXIMUM)) {
			maximumStaminaQueue.add(amount, delta);
		} else if(type.equals(StaminaType.ADRENALINE)) {
			adrenQueue.add(amount);
		}
	}
	
	public float getDefaultStamina() {
		return this.DEFAULT_STAMINA;
	}
	
	public float getStamina(StaminaType type) {
		switch(type) {
			case STAMINA:
				 return this.dw.getWatchableObjectFloat(StaminaType.STAMINA.getMeta());
			case CURRENT:
				return this.dw.getWatchableObjectFloat(StaminaType.CURRENT.getMeta());
			case MAXIMUM:
				return this.dw.getWatchableObjectFloat(StaminaType.MAXIMUM.getMeta());
			case ADRENALINE:
				return this.dw.getWatchableObjectFloat(StaminaType.ADRENALINE.getMeta());
			default:
				throw new IllegalArgumentException("Type must not be null.");
		}
	}
	
	public void invokeAdrenaline() {
		if(canInvokeAdrenaline) {
			isAdrenalineActive = true;
			canInvokeAdrenaline = false;
			adrenQueue.add(this.dw.getWatchableObjectFloat(StaminaType.STAMINA.getMeta()), 10.0F);
		}
	}
	
	public void update() {
		float stamina = this.dw.getWatchableObjectFloat(StaminaType.STAMINA.getMeta());
		float current = this.dw.getWatchableObjectFloat(StaminaType.CURRENT.getMeta());
		float maximum = this.dw.getWatchableObjectFloat(StaminaType.MAXIMUM.getMeta());
		float adrenaline = this.dw.getWatchableObjectFloat(StaminaType.ADRENALINE.getMeta());
		
		current += currentStaminaQueue.getNetChange();
		maximum += maximumStaminaQueue.getNetChange();
		adrenaline += adrenQueue.getNetChange();
		
		if(maximum >= stamina) maximum = stamina;
		if(maximum <= 0.0F) maximum = 0.0F;
		
		if(isAdrenalineActive) {
			if(adrenaline >= stamina) {
				adrenaline = stamina;
				adrenHasPeaked = true;
				adrenQueue.add(stamina, 5.0F / 3.0F);
			}
			if(adrenaline <= 0) {
				adrenaline = 0.0F;
				isAdrenalineActive = false;
				adrenCooldownTimer = 12000;
			}
			if(adrenHasPeaked && current >= adrenaline) current = adrenaline;
			this.dw.updateObject(StaminaType.ADRENALINE.getMeta(), adrenaline);
		} else if(canInvokeAdrenaline == false && --adrenCooldownTimer <= 0) {
			canInvokeAdrenaline = true;
			adrenHasPeaked = false;
		}
		
		if(current >= maximum) current = maximum;
		if(current <= 0.0F) current = 0.0F;
		
		this.dw.updateObject(StaminaType.CURRENT.getMeta(), current);
		this.dw.updateObject(StaminaType.MAXIMUM.getMeta(), maximum);
//		System.out.printf("%.1f : %.1f : %.1f %n", current, maximum, stamina);
	}
	
	public void setStamina(float amount) {
		this.dw.updateObject(StaminaType.STAMINA.getMeta(), amount);
	}
	
	private boolean canInvokeAdrenaline = true;
	private boolean adrenHasPeaked = false;
	private boolean isAdrenalineActive = false;
	private int adrenCooldownTimer = 0;
	
	private final DataWatcher dw;
	private final EntityPlayer player;
	private final Queue currentStaminaQueue = new Queue();
	private final Queue maximumStaminaQueue = new Queue();
	private final Queue adrenQueue = new Queue();
}
