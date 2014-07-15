package com.binaryaura.staminamod.network.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.entity.player.StaminaPlayer.StaminaType;
import com.binaryaura.staminamod.network.packets.TotalStaminaPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class TotalStaminaPacketHandler implements IMessageHandler<TotalStaminaPacket, IMessage> {

	public TotalStaminaPacketHandler() {}

	@Override
	public IMessage onMessage(TotalStaminaPacket message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			EntityPlayer clientPlayer = Minecraft.getMinecraft().thePlayer;
			StaminaPlayer props = StaminaPlayer.get(clientPlayer);
			props.set(StaminaType.STAMINA, message.getTotalStamina());
		}
		return null;
	}
}
