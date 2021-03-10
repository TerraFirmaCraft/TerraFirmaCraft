package net.dries007.tfc.client.screen.overlay;

import java.awt.*;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MOD_ID, value = Dist.CLIENT)
public final class PlayerDataOverlay
{
    private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/icons/overlay.png");

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void render(RenderGameOverlayEvent.Pre event)
    {
        Minecraft mc = Minecraft.getInstance();
        IngameGui gui = mc.gui;
        ClientPlayerEntity clientPlayer = mc.player;
        if (clientPlayer == null) return;
        PlayerEntity player = clientPlayer.inventory.player;

        ForgeIngameGui.renderFood = false;
        ForgeIngameGui.renderHealth = false;
        ForgeIngameGui.renderArmor = true;
        ForgeIngameGui.renderExperiance = false;

        if (event.getType() != ElementType.CROSSHAIRS) return;

        MatrixStack matrixStack = event.getMatrixStack();

        float displayModifier = 50f;
        float maxHealth = player.getMaxHealth() * displayModifier; // 20 * 50 = 1000
        float currentThirst = 100f;

        MainWindow window = event.getWindow();
        int guiScaledHeight = window.getGuiScaledHeight();
        int guiScaledWidth = window.getGuiScaledWidth();

        int healthRowHeight = guiScaledHeight - 40;
        int armorRowHeight = healthRowHeight - 10;
        int mid = guiScaledWidth / 2;

        FontRenderer fontRenderer = mc.font;
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bind(ICONS);

        if (!clientPlayer.abilities.instabuild)
        {
            float foodLevel = player.getFoodData().getFoodLevel();
            float percentFood = foodLevel / 20f;
            float percentThirst = currentThirst / 100f; //todo

            //hunger
            matrixStack.pushPose();
            matrixStack.translate(mid + 1, healthRowHeight, 0);
            gui.blit(matrixStack, 0, 0, 0, 20, 90, 5);
            gui.blit(matrixStack, 0, 0, 0, 25, (int) (90 * percentFood), 5);
            matrixStack.popPose();

            //thirst
            matrixStack.pushPose();
            matrixStack.translate(mid + 1, healthRowHeight + 5, 0);
            gui.blit(matrixStack, 0, 0, 90, 20, 90, 5);
            gui.blit(matrixStack, 0, 0, 90, 25, (int) (90 * percentThirst), 5);
            matrixStack.popPose();

            //health
            ForgeIngameGui.left_height += 10;
            matrixStack.pushPose();
            matrixStack.translate(mid - 91, healthRowHeight, 0);
            gui.blit(matrixStack, 0, 0, 0, 0, 90, 10);

            float absorption = player.getAbsorptionAmount();
            float percentHealth = (player.getHealth() + absorption) / 20f;
            float currentHealth = percentHealth * maxHealth;
            int uSurplus = 90;
            if (percentHealth > 1) percentHealth = 1;

            gui.blit(matrixStack, 0, 0, 0, 10, (int) (90 * percentHealth), 10);

            if ((player.getFoodData().getSaturationLevel() > 0.0f && player.isHurt()) || player.hurtTime > 0 || player.hasEffect(Effects.REGENERATION))
                gui.blit(matrixStack, 0, 1, 0, 30, 90, 8);
            float surplusPercent = MathHelper.clamp(percentHealth + (absorption / 20f) - 1, 0, 1);
            if (surplusPercent > 0)
            {
                // fill up the yellow bar until you get a second full bar, then just fill it up
                float percent = Math.min(surplusPercent, 1);
                gui.blit(matrixStack, 0, 0, uSurplus, 10, (int) (90 * percent), 10);
            }
            matrixStack.popPose();

            String healthString = String.format("%.0f / %.0f", currentHealth, maxHealth);
            matrixStack.pushPose();
            matrixStack.translate(mid - 45, healthRowHeight + 2.5, 0);
            matrixStack.scale(0.8f, 0.8f, 1.0f);
            fontRenderer.draw(matrixStack, healthString, -1 * fontRenderer.width(healthString) / 2f, 0, surplusPercent < 0.6 ? Color.white.getRGB() : Color.black.getRGB());
            matrixStack.popPose();

            RenderSystem.enableBlend();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

            textureManager.bind(AbstractGui.GUI_ICONS_LOCATION);
            Entity mount = player.getVehicle();
            if (!(mount instanceof LivingEntity) && mc.gameMode != null && mc.gameMode.hasExperience() && !ForgeIngameGui.renderExperiance)
            {
                int left = mid - 91;
                int xpNeeded = mc.player.getXpNeededForNextLevel();
                if (xpNeeded > 0)
                {
                    int filled = (int) (player.experienceProgress * 183.0F);
                    int top = guiScaledHeight - 29;
                    matrixStack.pushPose();
                    matrixStack.translate(left, top, 0);
                    gui.blit(matrixStack, 0, 0, 0, 64, 182, 5);
                    if (filled > 0)
                        gui.blit(matrixStack, 0, 0, 0, 69, filled, 5);
                    matrixStack.popPose();
                }

                if (player.experienceLevel > 0)
                {
                    String level = "" + player.experienceLevel;
                    int x = (guiScaledWidth - fontRenderer.width(level)) / 2;
                    int y = guiScaledHeight - 35;
                    fontRenderer.draw(matrixStack, level, x + 1, y, 0);
                    fontRenderer.draw(matrixStack, level, x - 1, y, 0);
                    fontRenderer.draw(matrixStack, level, x, y + 1, 0);
                    fontRenderer.draw(matrixStack, level, x, y - 1, 0);
                    fontRenderer.draw(matrixStack, level, x, y, 8453920);
                }
                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            if (mount instanceof LivingEntity)
            {
                textureManager.bind(ICONS);
                ForgeIngameGui.renderHealthMount = false;
                LivingEntity mountEntity = (LivingEntity) mount;
                matrixStack.pushPose();
                matrixStack.translate(mid + 1, armorRowHeight, 0);

                float mountMaxHealth = mountEntity.getMaxHealth() * displayModifier;
                float mountCurrentHealth = mountEntity.getHealth() * displayModifier;
                float mountPercentHealth = Math.min(mountCurrentHealth / mountMaxHealth, 1.0f);
                gui.blit(matrixStack, 0, 0, 90, 0, 90, 10);
                gui.blit(matrixStack, 0, 0, 90, 10, (int) (90 * mountPercentHealth), 10);
                if (mountEntity.hurtTime > 0)
                    gui.blit(matrixStack, 0, 1, 0, 30, 90, 8);
                matrixStack.popPose();

                String mountHealthString = String.format("%.0f / %.0f", mountCurrentHealth, mountMaxHealth);
                matrixStack.pushPose();
                matrixStack.translate(mid + 47, armorRowHeight + 2.5, 0);
                matrixStack.scale(0.8f, 0.8f, 1.0f);
                fontRenderer.draw(matrixStack, mountHealthString, -1 * (fontRenderer.width(mountHealthString) / 2f), 0, mountPercentHealth < 0.6 ? Color.white.getRGB() : Color.black.getRGB());
                matrixStack.popPose();

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }
}
