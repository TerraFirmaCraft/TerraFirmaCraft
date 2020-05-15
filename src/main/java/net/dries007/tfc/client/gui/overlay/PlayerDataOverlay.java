/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui.overlay;

import java.awt.*;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
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
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.items.metal.ItemMetalChisel;
import net.dries007.tfc.util.config.HealthDisplayFormat;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public final class PlayerDataOverlay
{
    private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/icons/overlay.png");
    private static final ResourceLocation MC_ICONS = new ResourceLocation("minecraft:textures/gui/icons.png");
    private static final PlayerDataOverlay INSTANCE = new PlayerDataOverlay();

    public static PlayerDataOverlay getInstance() { return INSTANCE; }

    private static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
    {
        float textureScaleU = 0.00390625F;
        float textureScaleV = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(xCoord + 0.0F, yCoord + maxV, 0).tex((minU) * textureScaleU, (minV + maxV) * textureScaleV).endVertex();
        vb.pos(xCoord + maxU, yCoord + maxV, 0).tex((minU + maxU) * textureScaleU, (minV + maxV) * textureScaleV).endVertex();
        vb.pos(xCoord + maxU, yCoord + 0.0F, 0).tex((minU + maxU) * textureScaleU, (minV) * textureScaleV).endVertex();
        vb.pos(xCoord + 0.0F, yCoord + 0.0F, 0).tex((minU) * textureScaleU, (minV) * textureScaleV).endVertex();
        tessellator.draw();
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Pre event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player.inventory.player;
        GuiIngameForge.renderFood = ConfigTFC.Client.DISPLAY.useVanillaHunger;
        GuiIngameForge.renderHealth = ConfigTFC.Client.DISPLAY.useVanillaHealth;
        GuiIngameForge.renderArmor = ConfigTFC.Client.DISPLAY.useVanillaHealth; // Draws on top of health
        GuiIngameForge.renderExperiance = ConfigTFC.Client.DISPLAY.useVanillaHealth && ConfigTFC.Client.DISPLAY.hideThirstBar; // Since it's below both, makes sense needing both enabled

        // We check for crosshairs just because it's always drawn and is before air bar
        if (event.getType() != ElementType.CROSSHAIRS)
        {
            return;
        }

        FoodStats foodStats = player.getFoodStats();
        float displayModifier = 1;
        if (ConfigTFC.Client.DISPLAY.healthDisplayFormat == HealthDisplayFormat.TFC || ConfigTFC.Client.DISPLAY.healthDisplayFormat == HealthDisplayFormat.TFC_CURRENT_HEALTH)
        {
            displayModifier = 50;
        }
        float baseMaxHealth = 20 * displayModifier;
        float currentThirst = 100;
        if (foodStats instanceof IFoodStatsTFC)
        {
            IFoodStatsTFC foodStatsTFC = (IFoodStatsTFC) foodStats;
            baseMaxHealth = 20 * foodStatsTFC.getHealthModifier() * displayModifier;
            currentThirst = foodStatsTFC.getThirst();
        }
        // This is for air to be drawn above our bars
        if (!ConfigTFC.Client.DISPLAY.hideThirstBar || !ConfigTFC.Client.DISPLAY.useVanillaHunger)
        {
            GuiIngameForge.right_height += ConfigTFC.Client.DISPLAY.useVanillaHunger ? 6 : 10;
        }

        ScaledResolution sr = event.getResolution();

        FontRenderer fontrenderer = mc.fontRenderer;

        int healthRowHeight = sr.getScaledHeight() - 40;
        int armorRowHeight = healthRowHeight - 10;
        int mid = sr.getScaledWidth() / 2;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(ICONS);

        if (mc.playerController.gameIsSurvivalOrAdventure())
        {
            GL11.glEnable(GL11.GL_BLEND);

            //Draw Food and Water
            float foodLevel = player.getFoodStats().getFoodLevel();
            float percentFood = foodLevel / 20f;
            float percentThirst = currentThirst / 100f;


            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            // Food
            if (!ConfigTFC.Client.DISPLAY.useVanillaHunger)
            {
                drawTexturedModalRect(mid + 1, healthRowHeight, 0, 20, 90, 5);
                drawTexturedModalRect(mid + 1, healthRowHeight, 0, 25, (int) (90 * percentFood), 5);
            }

            // Water
            if (!ConfigTFC.Client.DISPLAY.hideThirstBar)
            {
                drawTexturedModalRect(mid + 1, healthRowHeight + 5, 90, 20, 90, 5);
                drawTexturedModalRect(mid + 1, healthRowHeight + 5, 90, 25, (int) (90 * percentThirst), 5);
            }

            if (!ConfigTFC.Client.DISPLAY.useVanillaHealth)
            {
                //Draw Health
                drawTexturedModalRect(mid - 91, healthRowHeight, 0, 0, 90, 10);
                float curHealth = player.getHealth() * baseMaxHealth / (float) 20;
                float percentHealth = curHealth / baseMaxHealth;
                float surplusPercent = Math.max(percentHealth - 1, 0);
                int uSurplus = 90;
                if (percentHealth > 1) percentHealth = 1;

                drawTexturedModalRect(mid - 91, healthRowHeight, 0, 10, (int) (90 * percentHealth), 10);
                while (surplusPercent > 0)
                {
                    //Draw beyond max health bar(if other mods adds more health)
                    float percent = Math.min(surplusPercent, 1);
                    drawTexturedModalRect(mid - 91, healthRowHeight, uSurplus, 10, (int) (90 * percent), 10);
                    surplusPercent -= 1.0f;
                    uSurplus = uSurplus == 90 ? 0 : 90; //To alternate between red and yellow bars (if mods adds that much surplus health)
                    //To anyone seeing this: feel free to make a colorize(Hue tweaking?) function to get other color bars
                    //Or just add more color bars to overlay icons.
                }
                //Draw Health value
                String healthString = ConfigTFC.Client.DISPLAY.healthDisplayFormat.format(curHealth, baseMaxHealth);
                fontrenderer.drawString(healthString, mid - 45 - (fontrenderer.getStringWidth(healthString) / 2), healthRowHeight + 2, Color.white.getRGB());
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(MC_ICONS);

            //Draw experience bar when not riding anything, riding a non-living entity such as a boat/minecart, or riding a pig.
            if (!(player.getRidingEntity() instanceof EntityLiving) && !GuiIngameForge.renderExperiance)
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
            if (player.getRidingEntity() instanceof EntityLivingBase && !ConfigTFC.Client.DISPLAY.useVanillaHealth)
            {
                GuiIngameForge.renderHealthMount = false;
                mc.renderEngine.bindTexture(ICONS);
                EntityLivingBase mount = ((EntityLivingBase) player.getRidingEntity());
                drawTexturedModalRect(mid + 1, armorRowHeight, 90, 0, 90, 10);
                double mountMaxHealth = mount.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue();
                double mountCurrentHealth = mount.getHealth();
                float mountPercentHealth = (float) Math.min(mountCurrentHealth / mountMaxHealth, 1.0f);
                drawTexturedModalRect(mid + 1, armorRowHeight, 90, 10, (int) (90 * mountPercentHealth), 10);

                String mountHealthString = (int) Math.min(mountCurrentHealth, mountMaxHealth) + "/" + (int) mountMaxHealth;
                fontrenderer.drawString(mountHealthString, mid + 47 - (fontrenderer.getStringWidth(mountHealthString) / 2), armorRowHeight + 2, Color.white.getRGB());
            }

            mc.renderEngine.bindTexture(MC_ICONS);
        }

        int itemModeY = sr.getScaledHeight() - 21;
        int itemModeX = mid + 100;

        // draw chisel mode if main hand item is tfc chisel
        if (player.getHeldItemMainhand().getItem() instanceof ItemMetalChisel)
        {
            int iconU = 0;

            if (ItemMetalChisel.hasHammerForChisel(player))
            {
                IPlayerData capability = player.getCapability(CapabilityPlayerData.CAPABILITY, null);
                if (capability != null)
                {
                    switch (capability.getChiselMode())
                    {
                        case SMOOTH:
                            iconU = 0;
                            break;
                        case STAIR:
                            iconU = 20;
                            break;
                        case SLAB:
                            iconU = 40;
                            break;
                    }
                }
            }
            else
            {
                // todo: display missing hammer art
                iconU = 60;
            }

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.renderEngine.bindTexture(ICONS);
            drawTexturedModalRect(itemModeX, itemModeY, iconU, 58, 20, 20);
            mc.renderEngine.bindTexture(MC_ICONS);
        }
    }

    @SubscribeEvent
    public void renderAnimalFamiliarity(RenderLivingEvent.Post<EntityLiving> event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player.inventory.player;

        if (player.isSneaking())
        {
            EntityLivingBase entity = event.getEntity();
            if (entity instanceof IAnimalTFC && ((IAnimalTFC) entity).getAdultFamiliarityCap() > 0 && entity == mc.pointedEntity)
            {
                double x, y, z;
                x = event.getX();
                y = event.getY();
                z = event.getZ();

                float f = 1.6F;
                float f1 = 0.016666668F * f;
                double d3 = entity.getDistance(player);
                float f2 = 5.0F;

                if (d3 < f2)
                {
                    IAnimalTFC animal = (IAnimalTFC) entity;
                    RenderManager rendermanager = mc.getRenderManager();

                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) x + 0.0F, (float) y + entity.height + 0.75F, (float) z);
                    GL11.glRotatef(-rendermanager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(rendermanager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f1, -f1, f1);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.renderEngine.bindTexture(ICONS);
                    GL11.glScalef(0.33F, 0.33F, 0.33F);

                    float familiarity = Math.max(0.0F, Math.min(1.0F, animal.getFamiliarity()));
                    if (familiarity >= animal.getAdultFamiliarityCap() && animal.getAge() != IAnimalTFC.Age.CHILD)
                    {
                        // Render a red-ish outline for adults that cannot be familiarized more
                        drawTexturedModalRect(-8, 0, 132, 40, 16, 16);
                    }
                    else if (familiarity >= 0.3F)
                    {
                        // Render a white outline for the when the familiarity stopped decaying
                        drawTexturedModalRect(-8, 0, 112, 40, 16, 16);
                    }
                    else
                    {
                        drawTexturedModalRect(-8, 0, 92, 40, 16, 16);
                    }

                    GL11.glTranslatef(0, 0, -0.001F);

                    if (familiarity == 1.0F)
                    {
                        drawTexturedModalRect(-6, 14 - (int) (12 * familiarity), 114, 74 - (int) (12 * familiarity), 12, (int) (12 * familiarity));
                    }
                    else
                    {
                        drawTexturedModalRect(-6, 14 - (int) (12 * familiarity), 94, 74 - (int) (12 * familiarity), 12, (int) (12 * familiarity));
                    }

                    GL11.glDepthMask(true);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    }
}
