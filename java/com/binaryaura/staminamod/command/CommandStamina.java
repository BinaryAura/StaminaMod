package com.binaryaura.staminamod.command;

import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.entity.player.StaminaPlayer.StaminaType;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandStamina extends CommandBase {

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
		return "commands.stamina.usage";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		if (commandSender instanceof EntityPlayer) {
			if (args.length < 1) {
				throw new WrongUsageException("commands.stamina.usage", new Object[0]);
			}
			
			StaminaType type = null;
			
			if (args[0].equals("exhaust") || args[0].equalsIgnoreCase("empty")) {
				if(args.length == 1) {
					throw new WrongUsageException("commands.stamina.exhaust.usage", new Object[0]);
				}
				
				if (args.length == 2) {
					player = getCommandSenderAsPlayer(commandSender);
					type = parseType(args[1]);
					
				} else if (args.length == 3) {
					player = getPlayer(commandSender, args[1]);
					type = parseType(args[2]);
					
				} else {
					throw new WrongUsageException("commands.stamina.replenish.usage", new Object[0]);
				}
				props = getProps();
				props.exhaust(type);
				notifyAdmins(commandSender, "commands.stamina.exhaust.success", new Object[] {type, player.getCommandSenderName()});
				
			} else if (args[0].equals("replenish") || args[0].equalsIgnoreCase("fill")) {
				if (args.length == 1) {
					throw new WrongUsageException("commands.stamina.replenish.usage", new Object[0]);
				}
				
				if (args.length == 2) {
					player = getCommandSenderAsPlayer(commandSender);
					type = parseType(args[1]);
					
				} else if (args.length == 3) {
					player = getPlayer(commandSender, args[1]);
					type = parseType(args[2]);
					
				} else {
					throw new WrongUsageException("commands.stamina.replenish.usage", new Object[0]);
				}
				props = getProps();	
				props.replenish(type);
				notifyAdmins(commandSender, "commands.stamina.replenish.success", new Object[] {type, player.getCommandSenderName()});			
				
			} else if (args[0].equals("set")) {
				if (args.length < 3) {
					throw new WrongUsageException("commands.stamina.set.usage", new Object[0]);
				}
				
				float value = 0;
				
				if (args.length == 3) {
					player = getCommandSenderAsPlayer(commandSender);
					type = parseType(args[1]);			
					value = (float) parseDoubleBounded(commandSender, args[2], 0.0F, props.getStaminaValue(StaminaType.STAMINA));
					
				} else if (args.length == 4) {
					player = getPlayer(commandSender, args[1]);
					type = parseType(args[2]);				
					value = (float) parseDoubleBounded(commandSender, args[3], 0.0F, props.getStaminaValue(StaminaType.STAMINA));
					
				} else {
					throw new WrongUsageException("commands.stamina.set.usage", new Object[0]);
				}
				props = getProps();	
				props.set(type, value);
				notifyAdmins(commandSender, "commands.stamina.set.success", new Object[] {value, type, player.getCommandSenderName()});
				
				
			} else if (args[0].equals("add")) {
				if (args.length < 3) {
					throw new WrongUsageException("commands.stamina.add.usage", new Object[0]);
				}
				
				float value = 0, time = 0;
				
				// Add Immediately
				if (isType(args[2])) {
					if (args.length == 3) {
						player = getCommandSenderAsPlayer(commandSender);
						type = parseType(args[1]);		
						value = (float) parseDouble(commandSender, args[2]);
						
					} else if (args.length == 4) {
						player = getPlayer(commandSender, args[1]);
						type = parseType(args[2]);			
						value = (float) parseDouble(commandSender, args[3]);
						
					} else {
						throw new WrongUsageException("commands.stamina.add.usage", new Object[0]);
					}
					props = getProps();		
					props.addToQueue(type, value);
					notifyAdmins(commandSender, "commands.stamina.add.success.immediate", new Object[] {value, type, props.getStaminaValue(StaminaType.STAMINA), player.getCommandSenderName()});
					
				// Add Over Time
				} else if (isType(args[1])) {
					if (args.length == 4) {
						player = getCommandSenderAsPlayer(commandSender);
						type = parseType(args[1]);				
						value = (float) parseDouble(commandSender, args[2]);
						time = (float) parseDouble(commandSender, args[3]);
						
					} else if (args.length == 5) {					
						player = getPlayer(commandSender, args[1]);
						type = parseType(args[2]);			
						value = (float) parseDouble(commandSender, args[3]);
						time = (float) parseDouble(commandSender, args[4]);
						
					} else {
						throw new WrongUsageException("commands.stamina.add.usage", new Object[0]);
					}
					props = getProps();
					if (time < 0) throw new CommandException("commands.stamina.add.failure.negativeTime", new Object[] {time, type, player.getCommandSenderName()});
					props.addToQueue(type, value, time);
					notifyAdmins(commandSender, "commands.stamina.add.success.overTime", new Object[] {value, type, time, player.getCommandSenderName()});
					
				} else {
					throw new WrongUsageException("commands.stamina.noSuchType", new Object[0]);
				}
				
			} else if (args[0].equals("invoke")) {
				if(args.length == 1) {
					throw new WrongUsageException("commands.stamina.invoke.usage", new Object[0]);
				}
				
				if (args.length == 2) {
					player = getCommandSenderAsPlayer(commandSender);
					type = parseType(args[1]);
					
				} else if (args.length == 3) {
					player = getPlayer(commandSender, args[1]);
					type = parseType(args[2]);
					
				} else {
					throw new WrongUsageException("commands.stamina.invoke.usage", new Object[0]);
				}
				props = getProps();
				invoke(type);
				notifyAdmins(commandSender, "commands.stamina.invoke.success", new Object[] {player.getCommandSenderName()});
				
			} else if (args[0].equals("reset")) {
				if (args.length == 1) {
					throw new WrongUsageException("commands.stamina.reset.usage", new Object[0]);
				}
				
				if (args.length == 2) {
					player = getCommandSenderAsPlayer(commandSender);
					type = parseType(args[1]);
					
				} else if (args.length == 3) {
					player = getPlayer(commandSender, args[1]);
					type = parseType(args[2]);
					
				} else {
					throw new WrongUsageException("commands.stamina.reset.usage", new Object[0]);
				}
				props = getProps();
				props.reset(type);
				notifyAdmins(commandSender, "commands.stamina.reset.success", new Object[] {type, player.getCommandSenderName()});
				
			} else if (args[0].equals("freeze")) {
				if (args.length == 1) {
					throw new WrongUsageException("commands.stamina.freeze.usage", new Object[0]);
				}
				
				boolean value;
				
				if (args.length == 2) {
					player = getCommandSenderAsPlayer(commandSender);
					value = parseBoolean(commandSender, args[1]);
					type = StaminaType.STAMINA;
					
				} else if (args.length == 3) {
					if(!isType(args[1])) {
						player = getPlayer(commandSender, args[1]);
						value = parseBoolean(commandSender, args[2]);
						type = StaminaType.STAMINA;
						
					} else {
						player = getCommandSenderAsPlayer(commandSender);
						type = parseType(args[1]);
						value = parseBoolean(commandSender, args[2]);				
					}
					
				} else if (args.length == 4) {
					player = getPlayer(commandSender, args[1]);
					type = parseType(args[2]);
					value = parseBoolean(commandSender, args[3]);
					
				} else {
					throw new WrongUsageException("commands.stamina.freeze.usage", new Object[0]);
				}
				props = getProps();
				props.setFrozen(type, value);
				if (value) {
					if (type == StaminaType.STAMINA) {
						notifyAdmins(commandSender, "commands.stamina.freeze.success.freezeAll", new Object[] {player.getCommandSenderName()});
					} else {
						notifyAdmins(commandSender, "commands.stamina.freeze.success.freezeOne", new Object[] {type, player.getCommandSenderName()});
					}
					
				} else {
					if (type == StaminaType.STAMINA) {
						notifyAdmins(commandSender, "commands.stamina.freeze.success.unfreezeAll", new Object[] {player.getCommandSenderName()});
					} else {
						notifyAdmins(commandSender, "commands.stamina.freeze.success.unfreezeOne", new Object[] {type, player.getCommandSenderName()});
					}
				}
			} else {
				throw new WrongUsageException("commands.stamina.usage", new Object[0]);
			}
		}
	}
	
	@Deprecated
	protected void add(String type, float value) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				props.addToQueue(StaminaType.CURRENT, value);
				break;
			case "maximum": case "max":
				props.addToQueue(StaminaType.MAXIMUM, value);
				break;
			case "adrenaline": case "adren":
				props.addToQueue(StaminaType.ADRENALINE, value);
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}		
	}
	
	@Deprecated
	protected void add(String type, float value, float time) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				props.addToQueue(StaminaType.CURRENT, value, time);
				break;
			case "maximum": case "max":
				props.addToQueue(StaminaType.MAXIMUM, value, time);
				break;
			case "adrenaline": case "adren":
				props.addToQueue(StaminaType.ADRENALINE, value, time);
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	@Deprecated
	protected void exhaust(String type) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				props.exhaust(StaminaType.CURRENT);
				break;
			case "maximum": case "max":
				props.exhaust(StaminaType.MAXIMUM);
				break;
			case "adrenaline": case "adren":
				props.exhaust(StaminaType.ADRENALINE);
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});	
		}
	}
	
	@Deprecated
	protected void freeze(String type, boolean freeze) {
		if(type == null) type = "";
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				props.setFrozen(StaminaType.CURRENT, freeze);
				break;
			case "maximum": case "max":
				props.setFrozen(StaminaType.MAXIMUM, freeze);
				break;
			case "adrenaline": case "adren":
				props.setFrozen(StaminaType.ADRENALINE, freeze);
				break;
			case "":
				props.setFrozen(StaminaType.STAMINA, freeze);
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	@Deprecated
	protected void invoke(String type) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
			case "maximum": case "max":
				throw new CommandException("commands.stamina.invoke.wrongType", new Object[] {type});
			case "adrenaline": case "adren":
				props.invokeAdrenaline();
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	protected void invoke(StaminaType type) {
		switch (type) {
			case CURRENT:
			case MAXIMUM:
				throw new CommandException("commands.stamina.invoke.wrongType", new Object[] {type});
			case ADRENALINE:
				props.invokeAdrenaline();
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	protected StaminaType parseType(String type) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				return StaminaType.CURRENT;
			case "maximum": case "max":
				return StaminaType.MAXIMUM;
			case "adrenaline": case "adren":
				return StaminaType.ADRENALINE;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	@Deprecated
	protected void replenish(String type) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				props.replenish(StaminaType.CURRENT);
				break;
			case "maximum": case "max":
				props.replenish(StaminaType.MAXIMUM);
				break;
			case "adrenaline": case "adren":
				props.replenish(StaminaType.ADRENALINE);
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	@Deprecated
	protected void reset(String type) {
		switch (type.toLowerCase()) {
			case "current": case "realtime":
				props.reset(StaminaType.CURRENT);
				break;
			case "maximum": case "max":
				props.reset(StaminaType.MAXIMUM);
				break;
			case "adrenaline": case "adern":
				props.reset(StaminaType.ADRENALINE);
				break;
			default:
				throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	@Deprecated
	protected void set(String type, float value) {
		switch (type.toLowerCase()) {
		case "current": case "realtime":
			props.set(StaminaType.CURRENT, value);
			break;
		case "maximum": case "max":
			props.set(StaminaType.MAXIMUM, value);
			break;
		case "adrenaline": case "adren":
			props.set(StaminaType.ADRENALINE, value);
			break;
		default:
			throw new CommandException("commands.stamina.noSuchType", new Object[] {type});
		}
	}
	
	protected boolean isType(String type) {
		switch (type.toLowerCase()) { 
			case "current": case "realtime":
			case "maximum": case "max":
			case "adrenaline": case "adren":
				return true;
			default:
				return false;
		}
	}
	
	protected StaminaPlayer getProps() {
		props = StaminaPlayer.get(player);
		if (props == null) throw new CommandException("commands.stamina.playerHasNoProps", new Object[] {player.getCommandSenderName()});
		return props;
	}

	private EntityPlayerMP player;
	private StaminaPlayer props;
		
}
