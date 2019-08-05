/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui.overlay;

import java.awt.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.ConfigTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public final class PlayerDataOverlay
{
    private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/overlay/icons.png");
    private static final PlayerDataOverlay INSTANCE = new PlayerDataOverlay();

    public static PlayerDataOverlay getInstance() { return INSTANCE; }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Pre event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player.inventory.player;
        GuiIngameForge.renderFood = false;

        // We check for crosshairs just because it's always drawn and is before air bar
        if (event.getType() != ElementType.CROSSHAIRS)
        {
            return;
        }

        FoodStats foodStats = player.getFoodStats();
        float baseMaxHealth = 1000;
        float calcMaxHealth = 1000;
        float currentThirst = 100;
        if (foodStats instanceof IFoodStatsTFC)
        {
            IFoodStatsTFC foodStatsTFC = (IFoodStatsTFC) foodStats;
            baseMaxHealth = 20 * foodStatsTFC.getHealthModifier() * 50; //20 = default health
            calcMaxHealth = player.getMaxHealth() * foodStatsTFC.getHealthModifier() * 50;
            currentThirst = foodStatsTFC.getThirst();
        }

        // This is for air to be drawn above our bars
        GuiIngameForge.right_height += 10;

        ScaledResolution sr = event.getResolution();

        FontRenderer fontrenderer = mc.fontRenderer;

        int healthRowHeight = sr.getScaledHeight() - 40;
        int armorRowHeight = healthRowHeight - 10;
        int mid = sr.getScaledWidth() / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(ICONS);

        if (mc.playerController.gameIsSurvivalOrAdventure())
        {
            float curHealth = (player.getHealth() + player.getAbsorptionAmount()) * calcMaxHealth / player.getMaxHealth();
            float percentHealth = Math.min(curHealth / calcMaxHealth, 1);

            //Used for drawing "extra" health bars if enabled
            float surplusPercent = 0;
            if (ConfigTFC.CLIENT.showExtraHealthWithOverlays)
            {
                percentHealth = Math.min(curHealth / baseMaxHealth, 1);
                surplusPercent = Math.max((curHealth / baseMaxHealth) - 1, 0);
            }

            GL11.glEnable(GL11.GL_BLEND);

            //Draw Health Background Bar
            this.drawTexturedModalRect(mid - 91, healthRowHeight, 0, 0, 90, 10);

            //Draw Health Bar
            //Color of base health bar; red by default
            int healthColor = surplusPercent > 0 ? (int) Math.floor(surplusPercent) : 0;

            //Draw base bar color
            GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
            setHealthBarColor(healthColor);
            this.drawTexturedModalRect(mid - 91, healthRowHeight, 0, 10, (int) (90 * percentHealth), 10);
            //Draw additive shading texture
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBlendFunc(1, 1);
            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
            this.drawTexturedModalRect(mid - 91, healthRowHeight, 90, 10, (int) (90 * percentHealth), 10);
            GL11.glPopAttrib();

            //Add the "extra" health bar if enabled + needed
            if (surplusPercent > 0)
            {
                float percent = surplusPercent % 1;

                GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT);
                //Draw the "next" color
                setHealthBarColor(healthColor + 1);
                this.drawTexturedModalRect(mid - 91, healthRowHeight, 0, 10, (int) (90 * percent), 10);
                //Draw additive shading texture
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glBlendFunc(1, 1);
                GL14.glBlendEquation(GL14.GL_FUNC_ADD);
                this.drawTexturedModalRect(mid - 91, healthRowHeight, 90, 10, (int) (90 * percent), 10);
                GL11.glPopAttrib();
            }

            //Draw Food and Water
            float foodLevel = player.getFoodStats().getFoodLevel();
            float percentFood = foodLevel / 20f;
            float percentThirst = currentThirst / 100f;

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(mid + 1, healthRowHeight, 0, 20, 90, 5);
            this.drawTexturedModalRect(mid + 1, healthRowHeight, 0, 25, (int) (90 * percentFood), 5);

            this.drawTexturedModalRect(mid + 1, healthRowHeight + 5, 90, 20, 90, 5);
            this.drawTexturedModalRect(mid + 1, healthRowHeight + 5, 90, 25, (int) (90 * percentThirst), 5);

            //Draw Notifications
            String curHealthString = String.valueOf((int) curHealth);
            String maxHealthString = String.valueOf((int) calcMaxHealth);
            String healthString = curHealthString + "/" + maxHealthString;

            //Render health string border
            int healthStringBorder = new Color(0.0F, 0.0F, 0.0F, 0.3F).getRGB();
            fontrenderer.drawString(healthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2) - 1, healthRowHeight + 2, healthStringBorder);
            fontrenderer.drawString(healthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2) + 1, healthRowHeight + 2, healthStringBorder);
            fontrenderer.drawString(healthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2), healthRowHeight + 2 - 1, healthStringBorder);
            fontrenderer.drawString(healthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2), healthRowHeight + 2 + 1, healthStringBorder);

            //Render health string itself
            int healthStringColor = player.getAbsorptionAmount() > 0 ? 16777088 : Color.white.getRGB(); //#ffff80
            int maxHealthOffset = fontrenderer.getStringWidth(curHealthString);
            fontrenderer.drawString(curHealthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2), healthRowHeight + 2, healthStringColor);
            fontrenderer.drawString("/" + maxHealthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2) + maxHealthOffset, healthRowHeight + 2, Color.white.getRGB());

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(new ResourceLocation("minecraft:textures/gui/icons.png"));

            //Draw experience bar when not riding anything, riding a non-living entity such as a boat/minecart, or riding a pig.
            if (!(player.getRidingEntity() instanceof EntityLiving))
            {
                int cap = player.xpBarCap();
                int left = mid - 91;

                if (cap > 0)
                {
                    short barWidth = 182;
                    int filled = (int) (player.experience * (barWidth + 1));
                    int top = sr.getScaledHeight() - 29;
                    drawTexturedModalRect(left, top, 0, 64, barWidth, 5);
                    if (filled > 0)
                        drawTexturedModalRect(left, top, 0, 69, filled, 5);
                }

                if (player.experienceLevel > 0)
                {
                    int color = 8453920;
                    String text = Integer.toString(player.experienceLevel);
                    int x = (sr.getScaledWidth() - fontrenderer.getStringWidth(text)) / 2;
                    int y = sr.getScaledHeight() - 30;
                    fontrenderer.drawString(text, x + 1, y, 0);
                    fontrenderer.drawString(text, x - 1, y, 0);
                    fontrenderer.drawString(text, x, y + 1, 0);
                    fontrenderer.drawString(text, x, y - 1, 0);
                    fontrenderer.drawString(text, x, y, color);
                }

                // We have to reset the color back to white
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            // Draw mount's health bar
            if (player.getRidingEntity() instanceof EntityLivingBase)
            {
                GuiIngameForge.renderHealthMount = false;
                mc.renderEngine.bindTexture(ICONS);
                EntityLivingBase mount = ((EntityLivingBase) player.getRidingEntity());
                this.drawTexturedModalRect(mid + 1, armorRowHeight, 0, 0, 90, 10);
                double mountMaxHealth = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                double mountCurrentHealth = mount.getHealth();
                float mountPercentHealth = (float) Math.min(mountCurrentHealth / mountMaxHealth, 1.0f);
                this.drawTexturedModalRect(mid + 1, armorRowHeight, 90, 0, (int) (90 * mountPercentHealth), 10);

                String mountHealthString = (int) Math.min(mountCurrentHealth, mountMaxHealth) + "/" + (int) mountMaxHealth;
                fontrenderer.drawString(mountHealthString, mid + 47 - (fontrenderer.getStringWidth(mountHealthString) / 2), armorRowHeight + 2, Color.white.getRGB());
            }

            mc.renderEngine.bindTexture(new ResourceLocation("minecraft:textures/gui/icons.png"));
        }
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
        float textureScaleU = 0.00390625F;
        float textureScaleV = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(xCoord + 0.0F, yCoord + maxV, 0).tex((minU + 0) * textureScaleU, (minV + maxV) * textureScaleV).endVertex();
        vb.pos(xCoord + maxU, yCoord + maxV, 0).tex((minU + maxU) * textureScaleU, (minV + maxV) * textureScaleV).endVertex();
        vb.pos(xCoord + maxU, yCoord + 0.0F, 0).tex((minU + maxU) * textureScaleU, (minV + 0) * textureScaleV).endVertex();
        vb.pos(xCoord + 0.0F, yCoord + 0.0F, 0).tex((minU + 0) * textureScaleU, (minV + 0) * textureScaleV).endVertex();
        tessellator.draw();
    }

    private void setHealthBarColor(int col)
    {
        switch (col % 7)
        {
            case 0: //Red
                GL11.glColor4f(0.9F, 0.0F, 0.0F, 1.0F);
                break;
            case 1: //Orange
                GL11.glColor4f(0.8F, 0.3F, 0.0F, 1.0F);
                break;
            case 2: //Yellow
                GL11.glColor4f(0.65F, 0.5F, 0.0F, 1.0F);
                break;
            case 3: //Green
                GL11.glColor4f(0.0F, 0.5F, 0.0F, 1.0F);
                break;
            case 4: //Cyan
                GL11.glColor4f(0.0F, 0.5F, 0.6F, 1.0F);
                break;
            case 5: //Blue
                GL11.glColor4f(0.0F, 0.2F, 0.8F, 1.0F);
                break;
            case 6: //Purple
                GL11.glColor4f(0.4F, 0.0F, 0.6F, 1.0F);
                break;
        }
    }
}
