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

public class TotalStaminaPacket implements IMessage, IMessageHandler<TotalStaminaPacket, IMessage> {

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

	@Override
	public IMessage onMessage(TotalStaminaPacket message, MessageContext ctx) {
		EntityPlayer player = StaminaMod.proxy.getPlayerFromMessageContext(ctx);
		if (ctx.side == Side.CLIENT) {
			System.out.println("TS Packet Recieved on CLIENT");
			StaminaPlayer props = StaminaPlayer.get(player);
			props.set(StaminaType.STAMINA, totalStamina);
		}
		return null;
	}

	private float totalStamina;
}
