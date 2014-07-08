package com.binaryaura.staminamod;

import com.binaryaura.staminamod.command.CommandStamina;
import com.binaryaura.staminamod.gui.GuiStamina;
import com.binaryaura.staminamod.stats.StaminaStats;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = StaminaMod.MODID, name = StaminaMod.NAME, version = StaminaMod.VERSION, canBeDeactivated = true)
public class StaminaMod {

	public static final String MODID = "staminamod";
	public static final String NAME = "Stamina Mod";
	public static final String VERSION = "pre-Alpha";
	public static final String CLIENTPROXY = "com.binaryaura.staminamod.client.";
	public static final String COMMONPROXY = "com.binaryaura.staminamod.";
	
	// The instance of your mod that Forge uses.
	@Instance(MODID)
	public static StaminaMod instance;
	
	// Says where the client and server 'proxy' are located.
	@SidedProxy(clientSide = CLIENTPROXY + "ClientProxy", serverSide = COMMONPROXY + "CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new StaminaStats(mc));
		MinecraftForge.EVENT_BUS.register(new GuiStamina(mc));		
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandStamina());
	}
	
	private static Minecraft mc = Minecraft.getMinecraft();
}
