/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.dries007.tfc.common.TFCEffects;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.common.capabilities.player.PlayerDataCapability;
import net.dries007.tfc.common.entities.TFCFishingHook;
import net.dries007.tfc.config.HealthDisplayStyle;
import net.dries007.tfc.config.DisabledExperienceBarStyle;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_NAME;

public class IngameOverlays
{
    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/icons/overlay.png");
    public static final ResourceLocation INK_TEXTURE = Helpers.identifier("textures/misc/ink_splatter.png");
    public static final ResourceLocation GLOW_INK_TEXTURE = Helpers.identifier("textures/misc/glow_ink_splatter.png");

    public static final IIngameOverlay HEALTH = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, MOD_NAME + " Health", IngameOverlays::renderHealth);

    public static final IIngameOverlay MOUNT_HEALTH = OverlayRegistry.registerOverlayAbove(HEALTH, MOD_NAME + " Mount Health", IngameOverlays::renderMountHealth);

    public static final IIngameOverlay FOOD = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.FOOD_LEVEL_ELEMENT, MOD_NAME + " Food Bar", IngameOverlays::renderFood);
    public static final IIngameOverlay THIRST = OverlayRegistry.registerOverlayAbove(FOOD, MOD_NAME + " Thirst Bar", IngameOverlays::renderThirst);

    public static final IIngameOverlay INK = OverlayRegistry.registerOverlayTop(MOD_NAME + " Ink", IngameOverlays::renderInk);

    public static final IIngameOverlay CHISEL = OverlayRegistry.registerOverlayTop(MOD_NAME + " Chisel Mode", IngameOverlays::renderChiselMode);
    public static final IIngameOverlay EXPERIENCE = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, MOD_NAME + " EXPERIENCE", IngameOverlays::renderExperience);
    public static final IIngameOverlay JUMP_METER = OverlayRegistry.registerOverlayAbove(ForgeIngameGui.JUMP_BAR_ELEMENT, MOD_NAME + " JUMP METER", IngameOverlays::renderJumpMeter);

    public static final IIngameOverlay HUD_MOVER = OverlayRegistry.registerOverlayBelow(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, MOD_NAME + " HUD MOVER", IngameOverlays::moveLeftAndRightHeights);

    public static void reloadOverlays()
    {
        // Player and mount health, to use TFC variants or not
        final boolean enableHealth = TFCConfig.CLIENT.enableHealthBar.get();
        final boolean enableFood = TFCConfig.CLIENT.enableHungerBar.get();
        final boolean enableThirst = TFCConfig.CLIENT.enableThirstBar.get();

        OverlayRegistry.enableOverlay(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, !enableHealth);
        OverlayRegistry.enableOverlay(ForgeIngameGui.MOUNT_HEALTH_ELEMENT, !enableHealth);
        OverlayRegistry.enableOverlay(ForgeIngameGui.FOOD_LEVEL_ELEMENT, !enableFood);
        OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, false);
        OverlayRegistry.enableOverlay(ForgeIngameGui.JUMP_BAR_ELEMENT, false);

        OverlayRegistry.enableOverlay(HEALTH, enableHealth);
        OverlayRegistry.enableOverlay(MOUNT_HEALTH, enableHealth);
        OverlayRegistry.enableOverlay(FOOD, enableFood);
        OverlayRegistry.enableOverlay(THIRST, enableThirst);
        OverlayRegistry.enableOverlay(INK, TFCConfig.CLIENT.enableInkSplatter.get());
        OverlayRegistry.enableOverlay(CHISEL, true);
        OverlayRegistry.enableOverlay(EXPERIENCE, true);
        OverlayRegistry.enableOverlay(JUMP_METER, true);
        OverlayRegistry.enableOverlay(HUD_MOVER, !TFCConfig.CLIENT.enableExperienceBar.get());
    }

    public static void renderHealth(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            final Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            renderHealthBar(player, gui, stack, width, height);
        }
    }

    public static void renderMountHealth(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            final Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            if (player.getVehicle() instanceof final LivingEntity entity)
            {
                renderHealthBar(entity, gui, stack, width, height);
            }
        }
    }

    public static void renderFood(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            int x = width / 2;
            int y = height - gui.right_height;
            float percentFood = (float) player.getFoodData().getFoodLevel() / TFCFoodData.MAX_HUNGER;

            stack.pushPose();
            stack.translate(x + 1, y + 4, 0);
            gui.blit(stack, 0, 0, 0, 20, 90, 5);
            gui.blit(stack, 0, 0, 0, 25, (int) (90 * percentFood), 5);
            stack.popPose();

            gui.right_height += 6;
        }
    }

    public static void renderThirst(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            int x = width / 2;
            int y = height - gui.right_height;
            float percentThirst = 0;
            float overheat = 0;
            if (player.getFoodData() instanceof TFCFoodData data)
            {
                percentThirst = data.getThirst() / TFCFoodData.MAX_THIRST;
                overheat = data.getThirstContributionFromTemperature(player);
            }

            stack.pushPose();
            stack.translate(x + 1, y + 4, 0);
            gui.blit(stack, 0, 0, 90, 20, 90, 5);
            gui.blit(stack, 0, 0, 90, 25, (int) (90 * percentThirst), 5);
            if (overheat > 0)
            {
                RenderSystem.setShaderColor(1f, 1f, 1f, overheat / TFCFoodData.MAX_TEMPERATURE_THIRST_DECAY);
                gui.blit(stack, 0, 0, 90, 30, 90, 5);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
            stack.popPose();

            gui.right_height += 6;
        }
    }

    private static void renderChiselMode(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (setup(gui, mc))
        {
            final Player player = ClientHelpers.getPlayer();
            if (player != null && Helpers.isItem(player.getItemInHand(InteractionHand.MAIN_HAND), TFCTags.Items.CHISELS))
            {
                int u = 60;
                if (Helpers.isItem(player.getItemInHand(InteractionHand.OFF_HAND), TFCTags.Items.HAMMERS))
                {
                    u = player.getCapability(PlayerDataCapability.CAPABILITY).map(cap -> cap.getChiselMode().ordinal() * 20).orElse(0);
                }
                stack.pushPose();
                gui.blit(stack, width / 2 + 100, height - 21, u, 58, 20, 20);
                stack.popPose();
            }
        }
    }

    private static void renderExperience(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer localPlayer = mc.player;
        final Player player = (Player) mc.getCameraEntity();
        final boolean isShowingExperience = TFCConfig.CLIENT.enableExperienceBar.get();
        final boolean isStyleLeftHotbar = (TFCConfig.CLIENT.disabledExperienceBarStyle.get() == DisabledExperienceBarStyle.LEFT_HOTBAR);
        if (localPlayer != null && localPlayer.fishing instanceof TFCFishingHook hook && setup(gui, mc))
        {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            if (!isShowingExperience && isStyleLeftHotbar)
            {
                int barHeight;
                int uOffset;
                if (player != null && player.getVehicle() instanceof LivingEntity && TFCConfig.CLIENT.enableHealthBar.get()) // Increase the bar's height if a second health bar is present
                {
                    barHeight = 42;
                    uOffset = 164;
                }
                else
                {
                    barHeight = 32;
                    uOffset = 153;
                }
                final int x = width / 2 - 97;
                final int y = height - barHeight;
                final int texturePos = 36 + barHeight;
                final int amount = Mth.ceil(Mth.clampedMap(hook.pullExhaustion, 0, 100, 0, barHeight + 1));
                gui.blit(stack, x, y, uOffset, 36, 5, barHeight);
                if (amount > 0)
                {
                    gui.blit(stack, x, height - amount, uOffset + 5, texturePos - amount, 5, amount);
                }
            }
            else
            {
                final int x = width / 2 - 91;
                final int y = height - 29;
                final int amount = Mth.ceil(Mth.clampedMap(hook.pullExhaustion, 0, 100, 0, 183));
                gui.blit(stack, x, y, 0, 111, 182, 5);
                if (amount > 0)
                {
                    gui.blit(stack, x, y, 0, 116, amount, 5);
                }
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else if (isShowingExperience)
        {
            ForgeIngameGui.EXPERIENCE_BAR_ELEMENT.render(gui, stack, partialTicks, width, height);
        }
    }

    private static void renderJumpMeter(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer localPlayer = mc.player;
        final boolean isShowingExperience = TFCConfig.CLIENT.enableExperienceBar.get();
        final boolean isStyleLeftHotbar = (TFCConfig.CLIENT.disabledExperienceBarStyle.get() == DisabledExperienceBarStyle.LEFT_HOTBAR);
        if (isShowingExperience || (!isShowingExperience && !isStyleLeftHotbar))
        {
            ForgeIngameGui.JUMP_BAR_ELEMENT.render(gui, stack, partialTicks, width, height);
        }
        else if (localPlayer != null && localPlayer.isRidingJumpable() && setup(gui, mc) && !isShowingExperience && isStyleLeftHotbar)
        {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            int barHeight;
            int uOffset;
            if (TFCConfig.CLIENT.enableHealthBar.get()) // Use the taller bar if using TFC's health display
            {
                barHeight = 42;
                uOffset = 186;
            }
            else
            {
                barHeight = 32;
                uOffset = 175;
            }
            final int x = width / 2 - 97;
            final int y = height - barHeight;
            final int texturePos = 36 + barHeight;
            final int charge = (int) (localPlayer.getJumpRidingScale() *  (float) (barHeight + 1));
            gui.blit(stack, x, y, uOffset, 36, 5, barHeight);
            if (charge > 0)
            {
                gui.blit(stack, x, height - charge, uOffset + 5, texturePos - charge, 5, charge);
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void renderInk(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson())
        {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null)
            {
                if (player.hasEffect(TFCEffects.INK.get()))
                {
                    renderTextureOverlay(INK_TEXTURE, 1F);
                }
                else if (player.hasEffect(TFCEffects.GLOW_INK.get()))
                {
                    renderTextureOverlay(GLOW_INK_TEXTURE, 1F);
                }
            }
        }
    }

    private static void renderHealthBar(LivingEntity entity, ForgeIngameGui gui, PoseStack stack, int width, int height)
    {
        HealthDisplayStyle style = TFCConfig.CLIENT.healthDisplayStyle.get();
        float maxHealth = entity.getMaxHealth();

        int centerX = width / 2;
        int y = height - gui.left_height;

        stack.pushPose();
        stack.translate(centerX - 91, y, 0);
        gui.blit(stack, 0, 0, 0, 0, 90, 10);

        float absorption = entity.getAbsorptionAmount();
        absorption = Float.isNaN(absorption) ? 0 : absorption;
        float percentHealth = (entity.getHealth() + absorption) / entity.getMaxHealth();
        float currentHealth = percentHealth * maxHealth;
        percentHealth = Mth.clamp(percentHealth, 0, 1);

        gui.blit(stack, 0, 0, 0, 10, (int) (90 * percentHealth), 10);

        boolean isHurt = entity.getHealth() > 0.0F && entity.getHealth() < entity.getMaxHealth();
        boolean playerHasSaturation = entity instanceof Player player && player.getFoodData().getSaturationLevel() > 0;
        if ((playerHasSaturation && isHurt) || entity.hurtTime > 0 || entity.hasEffect(MobEffects.REGENERATION))
        {
            gui.blit(stack, 0, 1, 0, 30, 90, 8);
        }

        float surplusPercent = Mth.clamp(percentHealth + (absorption / 20f) - 1, 0, 1);
        if (surplusPercent > 0)
        {
            // fill up the yellow bar until you get a second full bar, then just fill it up
            float percent = Math.min(surplusPercent, 1);
            gui.blit(stack, 0, 0, 90, 10, (int) (90 * percent), 10);
        }
        stack.popPose();

        // Health modifier affects both max and current health equally. All we do is draw different numbers as a result.
        final float healthModifier = entity instanceof Player player && player.getFoodData() instanceof TFCFoodData data ? data.getHealthModifier() : 1f;

        String text = style.format(currentHealth * healthModifier, maxHealth * healthModifier);
        stack.pushPose();
        stack.translate(centerX - 45, y + 2.5, 0);
        stack.scale(0.8f, 0.8f, 1.0f);
        gui.getFont().draw(stack, text, -1 * gui.getFont().width(text) / 2f, 0, surplusPercent < 0.6 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
        stack.popPose();

        gui.left_height += 10;
    }

    private static void renderTextureOverlay(ResourceLocation location, float alpha)
    {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.setShaderTexture(0, location);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0.0D, screenHeight, -90.0D).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(screenWidth, screenHeight, -90.0D).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(screenWidth, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void moveLeftAndRightHeights(ForgeIngameGui gui, PoseStack stack, float partialTicks, int width, int height)
    {
        gui.right_height -= considerExperienceConfigs();
        gui.left_height -= considerExperienceConfigs();
    }

    private static boolean setupForSurvival(ForgeIngameGui gui, Minecraft minecraft)
    {
        return gui.shouldDrawSurvivalElements() && setup(gui, minecraft);
    }

    public static boolean setup(ForgeIngameGui gui, Minecraft minecraft)
    {
        if (!minecraft.options.hideGui && minecraft.getCameraEntity() instanceof Player)
        {
            gui.setupOverlayRenderState(true, false, TEXTURE);
            return true;
        }
        return false;
    }

    private static int considerExperienceConfigs()
    {
        final LocalPlayer player = Minecraft.getInstance().player;
        return switch (TFCConfig.CLIENT.disabledExperienceBarStyle.get())
        {
            case LEFT_HOTBAR -> 6;
            case BUMP -> player != null && (player.fishing instanceof TFCFishingHook || player.isRidingJumpable()) ? 0 : 6;
            default -> 0;
        };
    }
}
