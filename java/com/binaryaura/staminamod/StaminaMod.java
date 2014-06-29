package com.binaryaura.staminamod;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = StaminaMod.MODID, name = StaminaMod.NAME, version = StaminaMod.VERSION)
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
	public void preInit(FMLPreInitializationEvent event){
		System.out.println("PreInitialization: " + NAME);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		System.out.println("Initialization: " + NAME);
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		System.out.println("PostInitialization: " + NAME);
	}
}