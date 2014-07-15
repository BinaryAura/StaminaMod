package com.binaryaura.staminamod.network.packets;

import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.entity.player.StaminaPlayer.StaminaType;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class FreezePacket implements IMessage{

	public FreezePacket() {}
	
	public FreezePacket(boolean freeze, StaminaType type) {
		this.freeze = freeze;
		this.type = type;
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		freeze = buf.readBoolean();
		int data = buf.readInt();
		switch (data) {
			case 0:
				type = StaminaType.STAMINA;
				break;
			case 1:
				type = StaminaType.CURRENT;
				break;
			case 2:
				type = StaminaType.MAXIMUM;
				break;
			case 3:
				type = StaminaType.ADRENALINE;
				break;
			default:
				return;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(freeze);
		byte data;
		switch (type) {
			case STAMINA:
				data = 0;
				break;
			case CURRENT:
				data = 1;
				break;
			case MAXIMUM:
				data = 2;
				break;
			case ADRENALINE:
				data = 3;
				break;
			default:
				return;
		}
		buf.writeInt(data);
	}
	
	public boolean getFreeze() {
		return freeze;
	}
	
	public StaminaType getType() {
		return type;
	}
	
	private boolean freeze;
	private StaminaType type;
}
