package com.binaryaura.staminamod;

import com.binaryaura.staminamod.command.CommandStamina;
import com.binaryaura.staminamod.network.handlers.FreezePacketHandler;
import com.binaryaura.staminamod.network.handlers.TotalStaminaPacketHandler;
import com.binaryaura.staminamod.network.packets.FreezePacket;
import com.binaryaura.staminamod.network.packets.TotalStaminaPacket;
import com.binaryaura.staminamod.stats.StaminaStats;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = StaminaMod.MODID, name = StaminaMod.NAME, version = StaminaMod.VERSION, canBeDeactivated = true)
public class StaminaMod {

	public static final String MODID = "staminamod";
	public static final String NAME = "Stamina Mod";
	public static final String VERSION = "pre-Alpha";
	public static final String CLIENTPROXY = "com.binaryaura.staminamod.client.";
	public static final String COMMONPROXY = "com.binaryaura.staminamod.";
	public static SimpleNetworkWrapper channel;
	
	// The instance of your mod that Forge uses.
	@Instance(MODID)
	public static StaminaMod instance;
	
	// Says where the client and server 'proxy' are located.
	@SidedProxy(clientSide = CLIENTPROXY + "ClientProxy", serverSide = COMMONPROXY + "CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
			channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
			channel.registerMessage(TotalStaminaPacketHandler.class, TotalStaminaPacket.class, 0, Side.CLIENT);
			channel.registerMessage(FreezePacketHandler.class, FreezePacket.class, 1, Side.CLIENT);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new StaminaStats());		
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandStamina());
	}
}
