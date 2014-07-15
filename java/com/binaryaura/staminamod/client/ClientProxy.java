package com.binaryaura.staminamod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import com.binaryaura.staminamod.CommonProxy;
import com.binaryaura.staminamod.gui.GuiStamina;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy extends CommonProxy{

	public ClientProxy() {
		// This is for rendering entities and so forth later on.
	}
	
	@Override
	public void registerRenderers() {
		MinecraftForge.EVENT_BUS.register(new GuiStamina(Minecraft.getMinecraft()));
	}
	
	@Override
    public EntityPlayer getPlayerFromMessageContext(MessageContext ctx)
    {
        switch(ctx.side)
        {
            case CLIENT:
            {
                EntityPlayer entityClientPlayerMP = Minecraft.getMinecraft().thePlayer;
                return entityClientPlayerMP;
            }
            case SERVER:
            {
                EntityPlayer entityPlayerMP = ctx.getServerHandler().playerEntity;
                return entityPlayerMP;
            }
            default:
                assert false : "Invalid side in TestMsgHandler: " + ctx.side;
        }
        return null;
    }	
}
