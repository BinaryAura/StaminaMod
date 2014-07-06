package com.binaryaura.staminamod.gui;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ARMOR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;

import org.lwjgl.opengl.GL11;

import com.binaryaura.staminamod.StaminaMod;
import com.binaryaura.staminamod.entity.player.StaminaPlayer;
import com.binaryaura.staminamod.entity.player.StaminaPlayer.StaminaType;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

/**
 * GuiStamina extends GuiIngameForge to gain access to the protected Objects:
 *     Random() rand of GuiInGameForge and int udateCounter of GuiIngame for renderHealth()
 * 
 * @author BinaryAura
 *
 */

public class GuiStamina extends GuiIngameForge {
	
	// boolean values for easy deactivation for mod compatibility
	public static boolean renderArmor = true;
	public static boolean renderHealth = true;
	public static boolean renderStamina = true;
	
	// Top of the first bar on the left side of the HUD, excluding the XP bar, This would be my Stamina Bar.
	public static int left_height = 36;
	
	private static Minecraft mc = Minecraft.getMinecraft();
	private static final ResourceLocation staminaBar = new ResourceLocation(StaminaMod.MODID, "textures/gui/stamina_bar.png");
	
	/**
	 * 
	 * @param mc
	 */
	public GuiStamina(Minecraft mc) {
		super(mc);
		
		// Disable Vanilla rendering for Health and Armor (Left Side Bars)
		GuiIngameForge.renderHealth = false;
		GuiIngameForge.renderArmor = false;
	}
	
	/**
	 * renderHealth() copy-pasted from GuiIngameForge with a few changes:
	 * 
	 * 		- post(HEALTH); and if(pre(HEALTH)) return; statements removed.
	 * 				Forge didn't like me calling events from events from events.
	 *      - if(pre(HEALTH)) return; replaced with if(event.isCancelable()) return; in onRenderHUD()
	 * 
	 * @param width is the width of the window for location calculation
	 * @param height is the height of the window for location calculation
	 */
	@Override
	public void renderHealth(int width, int height) {
		 	bind(icons);
	        mc.mcProfiler.startSection("health");
	        GL11.glEnable(GL11.GL_BLEND);

	        boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
	        
	        if (mc.thePlayer.hurtResistantTime < 10) {
	            highlight = false;
	        }

	        IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
	        int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
	        int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
	        float healthMax = (float)attrMaxHealth.getAttributeValue();
	        float absorb = this.mc.thePlayer.getAbsorptionAmount();

	        int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
	        int rowHeight = Math.max(10 - (healthRows - 2), 3);
	        
	        super.rand.setSeed((long)(super.updateCounter * 312871));

	        int left = width / 2 - 91;
	        int top = height - left_height;
	        left_height += (healthRows * rowHeight);
	        if (rowHeight != 10) left_height += 10 - rowHeight;

	        int regen = -1;
	        if (mc.thePlayer.isPotionActive(Potion.regeneration)) {
	            regen = super.updateCounter % 25;
	        }

	        final int TOP =  9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
	        final int BACKGROUND = (highlight ? 25 : 16);
	        int MARGIN = 16;
	        if (mc.thePlayer.isPotionActive(Potion.poison))      MARGIN += 36;
	        else if (mc.thePlayer.isPotionActive(Potion.wither)) MARGIN += 72;
	        float absorbRemaining = absorb;	        
	        
	        for (int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
	            int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
	            int x = left + i % 10 * 8;
	            int y = top - row * rowHeight;

	            if (health <= 4) y += rand.nextInt(2);
	            if (i == regen) y -= 2;
	            drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
	            
	            if (highlight) {
	                if (i * 2 + 1 < healthLast) {
	                    drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9); //6
	                } else if (i * 2 + 1 == healthLast) {
	                    drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9); //7
	                }
	            }

	            if (absorbRemaining > 0.0F) {
	                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
	                    drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9); //17
	                } else {
	                    drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); //16
	                }
	                absorbRemaining -= 2.0F;
	            } else {
	                if (i * 2 + 1 < health) {
	                    drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); //4
	                } else if (i * 2 + 1 == health) {
	                    drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9); //5
	                }
	            }
	        }

	        GL11.glDisable(GL11.GL_BLEND);
	        mc.mcProfiler.endSection();
	        return;
	}
	
	/**
	 * 
	 * @param event RenderGameOverlayEvent to invoke the method
	 */
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderHUD(RenderGameOverlayEvent event) {
		
		// Checks to see if it is after (event.isCancelable() is false if the event is Post) the rendering of BOSSHEALTH
		if(event.type != ElementType.BOSSHEALTH || event.isCancelable()) return;
		
		res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
		int width = res.getScaledWidth();
		int height = res.getScaledHeight();
		
		// Reset left_height back to default
		left_height = 39;
		
		mc.entityRenderer.setupOverlayRendering();
		GL11.glEnable(GL11.GL_BLEND);
		if(!mc.playerController.enableEverythingIsScrewedUpMode()) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			if(mc.playerController.shouldDrawHUD()) {
				
				// Ask if the game should render each, if so, do so
				if(renderStamina) renderStamina(width, height);
				if(renderHealth) renderHealth(width, height);
				if(renderArmor) renderArmor(width, height);
			}
		}
	}
	
	/**
	 * renderArmor() copy-pasted from GuiIngameForge with a few changes:
	 * 
	 * 		- post(ARMOR); and if(pre(ARMOR)) return; statements removed
	 * 			Forge didn't like me calling events from events from events.
	 *      - if(pre(ARMOR)) return; replaced with if(event.isCancelable()) return; in onRenderHUD()
	 * 
	 * @param width is the width of the window for location calculation
	 * @param height is the height of the window for location calculation
	 */
	@Override
	protected void renderArmor(int width, int height) {
        mc.mcProfiler.startSection("armor");        

        GL11.glEnable(GL11.GL_BLEND);
        int left = width / 2 - 91;
        int top = height - left_height;

        int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
        for (int i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
                drawTexturedModalRect(left, top, 34, 9, 9, 9);
            } else if (i == level) {
                drawTexturedModalRect(left, top, 25, 9, 9, 9);
            } else if (i > level) {
                drawTexturedModalRect(left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        left_height += 10;

        GL11.glDisable(GL11.GL_BLEND);
        mc.mcProfiler.endSection();
        return;
	}
	
	/**
	* Renders a Three-in-one bar for stamina
	*
	*  @param width is the width of the window for location calculation
	*  @param height is the height of the window for location calculation
	*/
	protected void renderStamina(int width, int height) {
		
		props = StaminaPlayer.get(mc.thePlayer);	// Extended Properties
		if(props == null || props.getStamina(StaminaType.STAMINA) == 0) return;
		
		mc.mcProfiler.startSection("stamina");
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bind(staminaBar);
					
		int left = width / 2 - 91;
		int top  = height - left_height;
		int barWidth = 82;
		
//		int maximumFill = (int)(props.getStamina(StaminaType.MAXIMUM) / props.getStamina(StaminaType.STAMINA) * barWidth); // M
//		int currentFill = (int)(props.getStamina(StaminaType.CURRENT) / props.getStamina(StaminaType.STAMINA) * barWidth); // C
//		int adrenalineFill = (int)(props.getStamina(StaminaType.ADRENALINE) / props.getStamina(StaminaType.STAMINA) * barWidth);  // A
		
		int maximumFill = (int)(1000.0F / 2000.0F * barWidth);
		int currentFill = (int)(500.0F / 2000.0F * barWidth);
		int adrenalineFill = (int)(1500.0F / 2000.0F * barWidth);
		
//		System.out.printf("%.1f : %.1f : %.1f : %.1f %n", props.getStamina(StaminaType.CURRENT), props.getStamina(StaminaType.MAXIMUM), props.getStamina(StaminaType.ADRENALINE), props.getStamina(StaminaType.STAMINA));
		
		drawTexturedModalRect(left, top, 0, 0, barWidth, 5);
		
		// C M A
		if(adrenalineFill >= maximumFill) {
			drawTexturedModalRect(left, top, 0, 15, adrenalineFill, 5);
			if(maximumFill > 0) {
				drawTexturedModalRect(left, top, 0, 20, maximumFill, 5);
				if(currentFill > 0) {
					drawTexturedModalRect(left, top, 0, 24, currentFill, 5);
				}
			}
		// C A M
		} else if(adrenalineFill >= currentFill) {
			if(maximumFill > 0) {
				drawTexturedModalRect(left, top, 0, 5, maximumFill, 5);
				drawTexturedModalRect(left, top, 0, 20, adrenalineFill, 5);
				if(currentFill > 0) {
					drawTexturedModalRect(left, top, 0, 25, currentFill, 5);
				}
			}
		// A C M
		} else if(maximumFill > 0) {
			drawTexturedModalRect(left, top, 0, 5, maximumFill, 5);
			if(currentFill > 0) {
				drawTexturedModalRect(left, top, 0, 10, currentFill, 5);
				if(adrenalineFill > 0) {
					drawTexturedModalRect(left, top, 0, 25, adrenalineFill, 5);
				}
			}
		}
		left_height += 13;
		GL11.glDisable(GL11.GL_BLEND);
		mc.mcProfiler.endSection();
	}
	
	/**
	 * Calls the bindTexture() to bind the .png method to the rect drawn by the tesselator.
	 * 
	 * @param location the ResourceLocation of the Texture
	 */
    private void bind(ResourceLocation location)
    {
        mc.getTextureManager().bindTexture(location);
    }
	
	private ScaledResolution res = null;
    private StaminaPlayer props = null;		// Extended Properties
}
