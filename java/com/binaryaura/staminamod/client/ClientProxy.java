package com.binaryaura.staminamod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import com.binaryaura.staminamod.CommonProxy;
import com.binaryaura.staminamod.gui.GuiStamina;

public class ClientProxy extends CommonProxy{

	public ClientProxy() {
		// This is for rendering entities and so forth later on.
	}
	
	@Override
	public void registerRenderers() {
		MinecraftForge.EVENT_BUS.register(new GuiStamina(Minecraft.getMinecraft()));
	}
	
}
