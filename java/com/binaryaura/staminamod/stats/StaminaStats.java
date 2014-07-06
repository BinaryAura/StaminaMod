package com.binaryaura.staminamod.stats;

import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.entity.player.StaminaPlayer.StaminaType;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class StaminaStats {
	
	private static Minecraft mc;
	
	private static float difficultyModifier(EntityPlayer player) {
		float difficultyModifier = 1.0F;
		switch(player.worldObj.difficultySetting) {
			case PEACEFUL:
			case EASY:
				difficultyModifier = 0.5F;
				break;
			case NORMAL:
				difficultyModifier = 1.0F;
				break;
			case HARD:
				difficultyModifier = 1.5F;
				break;
		}
		return difficultyModifier;
	}
	
	private static float armorModifier(ItemStack armorStack, int slot) {
		float multiplier = 0.0F;
		
		/*
		 * 0 = Boots
		 * 1 = Leggings
		 * 2 = Chest-Plate
		 * 3 = Helmet
		 */
		switch(slot) {
			case 0:
				multiplier += 1.0F;
				break;
			case 1:
				multiplier += 3.0F;
				break;
			case 2:
				multiplier += 4.0F;
				break;
			case 3:
				multiplier += 2.0F;
				break;
		}
		
		if(armorStack == null)
			return multiplier / 10;
		ItemArmor armor = (ItemArmor)armorStack.getItem();
		ItemArmor.ArmorMaterial material = armor.getArmorMaterial();
		switch(material) {
			case CLOTH:
				multiplier /= 8;
				break;
			case DIAMOND:
				multiplier /= 6;
				break;
			case CHAIN:
				multiplier /= 5;
				break;
			case IRON:
				multiplier /= 3;
				break;
			case GOLD:
				multiplier /= 1;
				break;
		}
		return multiplier;
	}	
	
	private static int energyMultiplier(Item.ToolMaterial material) {
		int energyMultiplier = 0;
		switch(material) {
			case WOOD:
				energyMultiplier = 4;
				break;
			case EMERALD:
				energyMultiplier = 3;
				break;
			case STONE:
				energyMultiplier = 2;
				break;
			case IRON:
				energyMultiplier = 1;
				break;
			case GOLD:
				energyMultiplier = 0;
				break;				
		}
		return energyMultiplier;
	}
	
	private static int energyMultiplier(String material) {
		int energyMultiplier = 0;
		switch(material) {
		case "WOOD":
			energyMultiplier = 4;
			break;
		case "EMERALD":
			energyMultiplier = 3;
			break;
		case "STONE":
			energyMultiplier = 2;
			break;
		case "IRON":
			energyMultiplier = 1;
			break;
		case "GOLD":
			energyMultiplier = 0;
			break;				
		}
	return energyMultiplier;
	}
	
	public StaminaStats(Minecraft mc) {
		this.mc = mc;
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void handleFood(PlayerUseItemEvent.Finish event) {
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			StaminaPlayer props = StaminaPlayer.get(player);
			if(props != null && mc.playerController.isNotCreative()) {
				ItemStack itemStack = player.inventory.getCurrentItem();
				Item itemInUse = itemStack.getItem();
				if(itemInUse instanceof ItemFood) {
					ItemFood food = (ItemFood)itemInUse;
					float change = (200 * food.func_150906_h(itemStack) / difficultyModifier(player));
					props.addToQueue(StaminaType.CURRENT, change, 3.0F);
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void handlePlayerJump(LivingJumpEvent event) {
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			StaminaPlayer props = StaminaPlayer.get(player);
			if(props != null && mc.playerController.isNotCreative()) {
//				player.motionY = 0.20;
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void handlePlayerConstruction(EntityConstructing event) {
		if(event.entity instanceof EntityPlayer && StaminaPlayer.get((EntityPlayer)event.entity) == null)
			StaminaPlayer.register((EntityPlayer)event.entity);
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void handlePlayerUpdate(LivingUpdateEvent event) {
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			StaminaPlayer props = StaminaPlayer.get(player);
			if(props != null && mc.playerController.isNotCreative()) {
				float armorMulti = 0.0F;
				ItemStack[] armor = player.inventory.armorInventory;
				for(int i = 0; i < armor.length; i++) {
					armorMulti += armorModifier(armor[i], i);
				}
				
				if(player.isSprinting()) {
					props.addToQueue(StaminaType.CURRENT, -armorMulti * difficultyModifier(player));
				}
				if(player.isOnLadder() && player.motionY > 0) {
					props.addToQueue(StaminaType.CURRENT, -armorMulti * difficultyModifier(player));
				}
				if(!player.isSprinting()) {
					props.addToQueue(StaminaType.CURRENT, 2);
				}
				props.update();
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void handleToolUse(PlayerInteractEvent event) {
		if(event.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)event.entity;
			StaminaPlayer props = StaminaPlayer.get(player);
			if(props != null && mc.playerController.isNotCreative()) {
				if(event.action == Action.LEFT_CLICK_BLOCK) {
					float change = 0;
					try{
						Item itemInUse = player.inventory.getCurrentItem().getItem();
						if(itemInUse instanceof ItemTool || itemInUse instanceof ItemSword) {
							int usesToExhaust = 200;
							if(itemInUse instanceof ItemTool) {				
								final Item.ToolMaterial material = ((ItemTool)itemInUse).func_150913_i();
								if(itemInUse instanceof ItemSpade) {				
									usesToExhaust = 40 + 15*energyMultiplier(material);
								}
								if(itemInUse instanceof ItemAxe || itemInUse instanceof ItemPickaxe) {
									usesToExhaust = 20 + 15*energyMultiplier(material);
								}
							} else if(itemInUse instanceof ItemSword) {
								final String material = ((ItemSword)itemInUse).getToolMaterialName();
								usesToExhaust = 40 + 15 * energyMultiplier(material);
							}
							Block block = player.worldObj.getBlock(event.x, event.y, event.z);
							String type = null;
							if(itemInUse instanceof ItemAxe) {
								type = "axe";
							} else if(itemInUse instanceof ItemSpade) {
								type = "shovel";
							} else if(itemInUse instanceof ItemPickaxe) {
								type = "pickaxe";
							}
							if(block.getHarvestTool(0) != null && !block.getHarvestTool(0).equals(type)){
								usesToExhaust /= -2;
								usesToExhaust /= difficultyModifier(player);
							}							
							change = props.getDefaultStamina() / usesToExhaust;
						}else
							change = (props.getDefaultStamina() / (-400 / difficultyModifier(player)));
					}catch(NullPointerException e){
						change = (props.getDefaultStamina() / (-400 / difficultyModifier(player)));
					}finally{
						props.addToQueue(StaminaType.CURRENT, change, 0.25F);
					}
				}
			}
		}
	}
	
}
