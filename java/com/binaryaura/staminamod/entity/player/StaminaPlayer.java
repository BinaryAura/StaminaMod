package com.binaryaura.staminamod.entity.player;

import java.util.HashMap;
import java.util.Map;

import com.binaryaura.staminamod.util.Queue;
import com.binaryaura.staminamod.util.StaminaQueue;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class StaminaPlayer implements IExtendedEntityProperties {
	
	public static enum StaminaType {
		CURRENT,
		MAXIMUM,
		ADRENALINE
	}
	
	public final static String NAME = "StaminaPlayer";
	public final static int STAMINA = 20;
	public final static int CURRENT_STAMINA = 21;
	public final static int MAXIMUM_STAMINA = 22;
	public final static int ADRENALINE = 23;
	private final static float DEFAULT_STAMINA = 2000.0F;

	public StaminaPlayer(EntityPlayer player) {
		this.player = player;
		this.dw = this.player.getDataWatcher();
		this.dw.addObject(STAMINA, DEFAULT_STAMINA);
		this.dw.addObject(CURRENT_STAMINA, DEFAULT_STAMINA);
		this.dw.addObject(MAXIMUM_STAMINA, DEFAULT_STAMINA);
		this.dw.addObject(ADRENALINE, 0.0F);
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
		properties.setFloat("Stamina", this.dw.getWatchableObjectFloat(STAMINA));
		properties.setFloat("CurrentStamina", this.dw.getWatchableObjectFloat(CURRENT_STAMINA));
		properties.setFloat("MaximumStamina", this.dw.getWatchableObjectFloat(MAXIMUM_STAMINA));
		properties.setFloat("Adrenaline", this.dw.getWatchableObjectFloat(ADRENALINE));
		compound.setTag(NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound)compound.getTag(NAME);
		this.dw.updateObject(STAMINA, properties.getFloat("Stamina"));
		this.dw.updateObject(CURRENT_STAMINA, properties.getFloat("CurrentStamina"));
		this.dw.updateObject(MAXIMUM_STAMINA, properties.getFloat("MaximumStamina"));
		this.dw.updateObject(ADRENALINE, properties.getFloat("Adrenaline"));
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
		if(time < 0) throw new IllegalArgumentException("Time must not be negative");
		if(time == 0) addToQueue(type, amount);
		float delta = amount / (time * 40);
		if(type.equals(StaminaType.CURRENT)) {
			currentStaminaQueue.add(amount, delta);
		} else if(type.equals(StaminaType.MAXIMUM)) {
			maximumStaminaQueue.add(amount, delta);
		}
	}
	
	public void update() {
		int current = this.dw.getWatchableObjectInt(CURRENT_STAMINA);
		int maximum = this.dw.getWatchableObjectInt(MAXIMUM_STAMINA);
		int adrenaline = this.dw.getWatchableObjectInt(ADRENALINE);
		current += currentStaminaQueue.getNetChange();
		maximum += maximumStaminaQueue.getNetChange();
		this.dw.updateObject(CURRENT_STAMINA, current);
		this.dw.updateObject(MAXIMUM_STAMINA, maximum);
	}
	
	public float getStamina() {
		return this.dw.getWatchableObjectFloat(STAMINA);
	}
	
	public float getCurrentStamina() {
		return this.dw.getWatchableObjectFloat(CURRENT_STAMINA);
	}
	
	public float getMaximumStamina() {
		return this.dw.getWatchableObjectFloat(MAXIMUM_STAMINA);
	}
	
	public float getAdrenaline() {
		return this.dw.getWatchableObjectFloat(ADRENALINE);
	}
	
	public float getDefaultStamina() {
		return this.DEFAULT_STAMINA;
	}
	
	public void setStamina(float amount) {
		this.dw.updateObject(STAMINA, amount);
	}
	
	private final EntityPlayer player;
	
	private DataWatcher dw;
	private Queue currentStaminaQueue = new Queue();
	private Queue maximumStaminaQueue = new Queue();
}
