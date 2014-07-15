package com.binaryaura.staminamod;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {

	// Client Stuff
	public void registerRenderers() {
		// Nothing here as the server doesn't render graphics or entities!
	}
	
	 public EntityPlayer getPlayerFromMessageContext(MessageContext ctx)
	    {
	        switch(ctx.side)
	        {
	            case CLIENT:
	            {
	                assert false : "Message for CLIENT received on dedicated server";
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
