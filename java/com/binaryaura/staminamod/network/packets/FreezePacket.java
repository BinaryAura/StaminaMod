package com.binaryaura.staminamod.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.binaryaura.staminamod.StaminaMod;
import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.entity.player.StaminaPlayer.StaminaType;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class FreezePacket implements IMessage, IMessageHandler<FreezePacket, IMessage>{

	public FreezePacket() {}
	
	public FreezePacket(StaminaType type, boolean freeze) {
		this.type = type;
		this.freeze = freeze;
		
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		freeze = buf.readBoolean();
		byte data = buf.readByte();
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
				assert false : "Invalid type recieved";
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(freeze);
		byte data = 0;
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
				assert false : "Attempted to send an Invalid type";
		}
		buf.writeByte(data);
	}
	
	@Override
	public IMessage onMessage(FreezePacket message, MessageContext ctx) {
		EntityPlayer player = StaminaMod.proxy.getPlayerFromMessageContext(ctx);
		if (ctx.side == Side.CLIENT) {
			System.out.println("F Packet Recieved on CLIENT");
			StaminaPlayer props = StaminaPlayer.get(player);
			System.out.println(type);
			props.setFrozen(message.type, message.freeze);			
		}
		return null;
	}
	
	private boolean freeze;
	private StaminaType type;
}
