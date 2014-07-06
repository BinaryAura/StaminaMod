package com.binaryaura.staminamod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandStamina extends CommandBase {

	public CommandStamina() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCommandName() {
		return "stamina";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return null;
	}

	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		// TODO Auto-generated method stub

	}

}
