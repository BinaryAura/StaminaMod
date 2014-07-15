package com.binaryaura.staminamod.network.packets;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class TotalStaminaPacket implements IMessage {

	public TotalStaminaPacket() {}
	
	public TotalStaminaPacket(float totalStamina) {
		this.totalStamina = totalStamina;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		totalStamina = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(totalStamina);
	}
	
	public float getTotalStamina() {
		return totalStamina;
	}

	private float totalStamina;
}
