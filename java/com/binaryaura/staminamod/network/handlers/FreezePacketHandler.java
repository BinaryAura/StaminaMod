package com.binaryaura.staminamod.network.handlers;

import java.awt.TrayIcon.MessageType;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.network.packets.FreezePacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class FreezePacketHandler implements IMessageHandler<FreezePacket, IMessage> {

	public FreezePacketHandler() {}

	@Override
	public IMessage onMessage(FreezePacket message, MessageContext ctx) {
		if (ctx.side == Side.CLIENT) {
			EntityPlayer clientPlayer = Minecraft.getMinecraft().thePlayer;
			StaminaPlayer props = StaminaPlayer.get(clientPlayer);
			props.setFrozen(message.getType(), message.getFreeze());			
		}
		return null;
	}
}
