package com.binaryaura.staminamod.gui;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ALL;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HELMET;

import org.lwjgl.opengl.GL11;

import com.binaryaura.staminamod.entity.player.StaminaPlayer;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;

@Deprecated
@SideOnly(Side.CLIENT)
public class OldGuiStamina extends GuiIngameForge{

	public OldGuiStamina(Minecraft mc) {
		super(mc);
	}
	
	public int left_height = 45; 
	
	@Override
	public void renderGameOverlay(float partialTicks, boolean hasScreen, int mouseX, int mouseY){

        res = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        eventParent = new RenderGameOverlayEvent(partialTicks, res, mouseX, mouseY);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        left_height = 45;
        if(!this.mc.playerController.enableEverythingIsScrewedUpMode() && this.mc.playerController.shouldDrawHUD())
        	renderStamina(width, height);
        super.renderGameOverlay(partialTicks, hasScreen, mouseX, mouseY);
	}
	
	@Override
	protected void renderArmor(int width, int height){
		if(pre(ElementType.ARMOR))
			return;
		mc.mcProfiler.startSection("armor");
		
		GL11.glEnable(GL11.GL_BLEND);
		int left = width / 2 - 91;
		int top = height - left_height;
		
		int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
		for(int i = 1; level > 0 && i < 20; i += 2){
			if(i < level)
				drawTexturedModalRect(left, top, 34, 9, 9, 9);
			else if(i == level)
				drawTexturedModalRect(left, top, 25, 9, 9, 9);
			else if(i > level)
				drawTexturedModalRect(left, top, 16, 9, 9, 9);
			left += 8;
		}
		left_height += 10;
		
		GL11.glDisable(GL11.GL_BLEND);
		mc.mcProfiler.endSection();
		post(ElementType.ARMOR);
	}
	
	@Override
	public void renderHealth(int width, int height){
		bind(icons);
		if(pre(HEALTH)) 
			return;
		mc.mcProfiler.startSection("health");
		GL11.glEnable(GL11.GL_BLEND);
		
		boolean highlight = mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
		
		if(mc.thePlayer.hurtResistantTime < 10)
			highlight = false;
		
		IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
		int health = MathHelper.ceiling_float_int(mc.thePlayer.getHealth());
		int healthLast = MathHelper.ceiling_float_int(mc.thePlayer.prevHealth);
		float healthMax = (float)attrMaxHealth.getAttributeValue();
		float absorb = this.mc.thePlayer.getAbsorptionAmount();
		
		int healthRows = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F / 10.0F);
		int rowHeight = Math.max(10 - (healthRows - 2), 3);
		
		this.rand.setSeed((long)updateCounter * 312871);
		
		int left = width / 2 - 91;
		int top = height - left_height - 6;
		left_height += (healthRows * rowHeight);
		if(rowHeight != 10)
			left_height += 10 - rowHeight;
		
		int regen = -1;
		if(mc.thePlayer.isPotionActive(Potion.regeneration))
			regen = updateCounter % 25;
		
		final int TOP = 9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
		final int BACKGROUND = (highlight ? 25 : 0);
		int MARGIN = 16;
		if(mc.thePlayer.isPotionActive(Potion.poison))
			MARGIN += 36;
		else if(mc.thePlayer.isPotionActive(Potion.wither))
			MARGIN += 72;
		float absorbRemaining = absorb;
		
		for(int i = MathHelper.ceiling_float_int((healthMax + absorb) / 2.0F) - 1; i >= 0; --i){
			int row = MathHelper.ceiling_float_int((float)(i + 1) / 10.0F) - 1;
			int x = left + i % 10 * 8;
			int y = top - row * rowHeight;
			
			if(health <= 4)
				y += rand.nextInt(2);
			if(i == regen)
				y -= 2;
			
			drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
			
			if(highlight){
				if(i * 2 + 1 < healthLast)
					drawTexturedModalRect(x, y, MARGIN + 54, TOP, 9, 9);
				else if(i * 2 + 1 == healthLast)
					drawTexturedModalRect(x, y, MARGIN + 63, TOP, 9, 9);
			}
			
			if(absorbRemaining > 0.0F){
				if(i * 2 + 1 < health)
					drawTexturedModalRect(x, y, MARGIN + 153, TOP, 9, 9);
				else
					drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9);
				absorbRemaining -= 2.0F;
			}else{
				if(i * 2 + 1 < health)
					drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9);
				else if(i * 2 + 1 == health)
					drawTexturedModalRect(x, y, MARGIN + 45, TOP, 9, 9);
			}
		}
		
		GL11.glDisable(GL11.GL_BLEND);
		mc.mcProfiler.endSection();	
		post(HEALTH);
	}

	public void renderStamina(int width, int height){
		bind(new ResourceLocation("staminamod", "textures/gui/stamina_bar"));
		if(eventParent.isCancelable() || props.getStamina() == 0){
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			if(this.mc.playerController.gameIsSurvivalOrAdventure()){
				mc.mcProfiler.startSection("stamina");
				int left = width / 2 - 91;
				int top  = height - 41;
				int barWidth = 82;
				int maximumFill = (int)(props.getMaximumStamina() / props.getStamina() * barWidth);
				int currentFill = (int)(props.getCurrentStamina() / props.getStamina() * barWidth);
				int adrenalineFill = (int)(props.getAdrenaline() / props.getStamina() * barWidth);
				drawTexturedModalRect(left, top, 0, 0, barWidth, 5);
				
				if(adrenalineFill >= maximumFill){
					drawTexturedModalRect(left, top, 0, 15, adrenalineFill, 5);
					if(maximumFill > 0)
						drawTexturedModalRect(left, top, 0, 20, maximumFill, 5);
						if(currentFill > 0)
							drawTexturedModalRect(left, top, 0, 25, currentFill, 5);
				}else if(adrenalineFill >= currentFill){
					if(maximumFill > 0){
						drawTexturedModalRect(left, top, 0, 5, maximumFill, 5);
						drawTexturedModalRect(left, top, 0, 20, adrenalineFill, 5);
						if(currentFill > 0)
							drawTexturedModalRect(left, top, 0, 25, currentFill, 5);
					}
				}else if(maximumFill > 0){
					drawTexturedModalRect(left, top, 0, 5, maximumFill, 5);
					if(currentFill > 0){
						drawTexturedModalRect(left, top, 0, 10, currentFill, 5);
						if(adrenalineFill > 0)
							drawTexturedModalRect(left, top, 0, 25, adrenalineFill, 5);
					}
				}
				mc.mcProfiler.endSection();
			}
			
			
		}
	}
	
    //Helper macros
	
    private boolean pre(ElementType type)
    {
        return MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(eventParent, type));
    }
    private void post(ElementType type)
    {
        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(eventParent, type));
    }
    private void bind(ResourceLocation res)
    {
        mc.getTextureManager().bindTexture(res);
    }
    
    private StaminaPlayer props = StaminaPlayer.get(this.mc.thePlayer);
    private ScaledResolution res = null;
    private RenderGameOverlayEvent eventParent;
    
}
