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
		
		private final int meta;
	}
	
	public final static String NAME = "StaminaPlayer";
	public final static float DEFAULT_STAMINA = 2000.0F;
	
	public static final void register(EntityPlayer player) {
		player.registerExtendedProperties(NAME, new StaminaPlayer(player));
	}
	
	public static final StaminaPlayer get(EntityPlayer player) {
		return (StaminaPlayer)player.getExtendedProperties(NAME);
	}

	public StaminaPlayer(EntityPlayer player) {
		this.player = player;
		this.dw = this.player.getDataWatcher();
		this.dw.addObject(StaminaType.STAMINA.getMeta(), DEFAULT_STAMINA);
		this.dw.addObject(StaminaType.CURRENT.getMeta(), DEFAULT_STAMINA);
		this.dw.addObject(StaminaType.MAXIMUM.getMeta(), DEFAULT_STAMINA);
		this.dw.addObject(StaminaType.ADRENALINE.getMeta(), 0.0F);
	}
	
	public void addToQueue(StaminaType type, float amount) {
		if(type == null) throw new NullPointerException("Type must not be null.");
		switch(type) {
			case CURRENT:
				currentStaminaQueue.add(amount);
				break;
			case MAXIMUM:
				maximumStaminaQueue.add(amount);
				break;
			case ADRENALINE:
				adrenQueue.add(amount);
				break;
			default:
				throw new IllegalArgumentException("Type must be current, maximum, or adrenaline.");
		}
	}
	
	public void addToQueue(StaminaType type, float amount, float time) {
		if(type == null) throw new NullPointerException("Type must not be null.");
		if(time < 0) throw new IllegalArgumentException("Time must not be negative.");
		if(time == 0) addToQueue(type, amount);
		float delta = amount / (time * 40);
		switch(type) {
			case CURRENT:
				currentStaminaQueue.add(amount, delta);
				break;
			case MAXIMUM:
				maximumStaminaQueue.add(amount, delta);
				break;
			case ADRENALINE:
				adrenQueue.add(amount, delta);
				break;
			default:
				throw new IllegalArgumentException("Type must be current, maximum, or adrenaline.");
		}
	}
	
	public void exhaust(StaminaType type) {
		set(type, 0.0F);
	}

	@Override
	public void init(Entity entity, World world) { }
	
	public void invokeAdrenaline() {
		invokeAdrenaline(false);
	}
	
	public void invokeAdrenaline(boolean override) {
		if(canInvokeAdrenaline || override) {
			adrenQueue.add(getStaminaValue(StaminaType.STAMINA) - getStaminaValue(StaminaType.ADRENALINE), DEFAULT_STAMINA / (5.0F * 40));
		}
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound)compound.getTag(NAME);
		this.dw.updateObject(StaminaType.STAMINA.getMeta(), properties.getFloat("Stamina"));
		this.dw.updateObject(StaminaType.CURRENT.getMeta(), properties.getFloat("CurrentStamina"));
		this.dw.updateObject(StaminaType.MAXIMUM.getMeta(), properties.getFloat("MaximumStamina"));
		this.dw.updateObject(StaminaType.ADRENALINE.getMeta(), properties.getFloat("Adrenaline"));
	}
	
	public void replenish(StaminaType type) {
		set(type, getStaminaValue(StaminaType.STAMINA));
	}
	
	public void reset(StaminaType type) {
		if (type == null) throw new NullPointerException("Type must not be null.");
		switch(type) {
			case CURRENT:
				currentStaminaQueue.reset();;
				break;
			case MAXIMUM:
				maximumStaminaQueue.reset();;
				break;
			case ADRENALINE:
				adrenQueue.reset();
			default:
				throw new IllegalArgumentException("Type must be current, maximum, or adrenaline.");
		}
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
	
	public void set(StaminaType type, float amount) {
		this.dw.updateObject(type.getMeta(), amount);
	}
	
	public void setFrozen(StaminaType type, boolean freeze) {
		switch (type) {
			case CURRENT:
				updateCurrent = !freeze;
				break;
			case MAXIMUM:
				updateMaximum = !freeze;
				break;
			case ADRENALINE:
				updateAdren = !freeze;
				break;
			case STAMINA:
				updateMaximum = !freeze;
				updateMaximum = !freeze;
				updateAdren = !freeze;
				break;
			default:
				throw new NullPointerException("Type must not be null");	
		}
	}
	
	public void setStamina(float amount) {
		this.dw.updateObject(StaminaType.STAMINA.getMeta(), amount);
	}
	
	public void update() {
		float stamina = getStaminaValue(StaminaType.STAMINA);
		float current = getStaminaValue(StaminaType.CURRENT);
		float maximum = getStaminaValue(StaminaType.MAXIMUM);
		float adrenaline = getStaminaValue(StaminaType.ADRENALINE);
		
		System.out.println(updateCurrent);
		
		if (updateCurrent) current += currentStaminaQueue.getNetChange();
		if (updateMaximum) maximum += maximumStaminaQueue.getNetChange();
		if (updateAdren) adrenaline += adrenQueue.getNetChange();
		
		if(maximum >= stamina) maximum = stamina;
		if(maximum <= 0.0F) maximum = 0.0F;
		
		if (adrenaline > 0) {
			isAdrenalineActive = true;
			if (adrenaline >= stamina) {
				adrenaline = stamina;
				adrenHasPeaked = true;
				adrenQueue.reset();
				adrenQueue.add(-stamina, DEFAULT_STAMINA / (30.0F * 40));
			}
			if (updateAdren && adrenHasPeaked && current >= adrenaline) current = adrenaline;
		} else if (isAdrenalineActive) {
			adrenaline = 0.0F;
			isAdrenalineActive = false;
			adrenHasPeaked = false;
			adrenQueue.reset();
			adrenCooldownTimer = 12000;
		} else if (!canInvokeAdrenaline && --adrenCooldownTimer <= 0) {
			canInvokeAdrenaline = true;
		}
		
		if(current >= maximum) current = maximum;
		if(current <= 0.0F) current = 0.0F;
		
		this.dw.updateObject(StaminaType.CURRENT.getMeta(), current);
		this.dw.updateObject(StaminaType.MAXIMUM.getMeta(), maximum);
		this.dw.updateObject(StaminaType.ADRENALINE.getMeta(), adrenaline);
	}	
	
	public float getStaminaValue(StaminaType type) {
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
				throw new NullPointerException("Type must not be null.");
		}
	}
	
	private boolean adrenHasPeaked = false;
	private boolean canInvokeAdrenaline = true;
	private boolean isAdrenalineActive = false;
	
	private boolean updateCurrent = true;
	private boolean updateMaximum = true;
	private boolean updateAdren = true;
	
	private int adrenCooldownTimer = 0;
	
	private final DataWatcher dw;
	private final EntityPlayer player;
	private final Queue currentStaminaQueue = new Queue();
	private final Queue maximumStaminaQueue = new Queue();
	private final Queue adrenQueue = new Queue();
}
