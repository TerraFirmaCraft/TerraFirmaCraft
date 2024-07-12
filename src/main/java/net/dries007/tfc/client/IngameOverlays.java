/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.awt.Color;
import java.util.Locale;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforge.neoforged.client.event.RegisterGuiOverlaysEvent;
import net.neoforge.neoforged.client.event.RenderGuiOverlayEvent;
import net.neoforge.neoforged.client.gui.overlay.ForgeGui;
import net.neoforge.neoforged.client.gui.overlay.GuiOverlayManager;
import net.neoforge.neoforged.client.gui.overlay.IGuiOverlay;
import net.neoforge.neoforged.client.gui.overlay.NamedGuiOverlay;
import net.neoforge.neoforged.client.gui.overlay.VanillaGuiOverlay;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.misc.TFCFishingHook;
import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.config.DisabledExperienceBarStyle;
import net.dries007.tfc.config.HealthDisplayStyle;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public enum IngameOverlays
{
    HEALTH(IngameOverlays::renderHealth),
    MOUNT_HEALTH(IngameOverlays::renderMountHealth),
    FOOD(IngameOverlays::renderFood),
    THIRST(IngameOverlays::renderThirst),
    INK(IngameOverlays::renderInk),
    CHISEL(IngameOverlays::renderChiselMode),
    EXPERIENCE(IngameOverlays::renderExperience),
    JUMP_BAR(IngameOverlays::renderJumpBar),
    HUD_MOVER(IngameOverlays::moveLeftAndRightHeights),
    FAMILIARITY(IngameOverlays::renderFamiliarity),
    ;

    private final String id;
    final IGuiOverlay overlay;

    IngameOverlays(IGuiOverlay overlay)
    {
        this.id = name().toLowerCase(Locale.ROOT);
        this.overlay = overlay;
    }

    public ResourceLocation id()
    {
        return Helpers.identifier(id);
    }

    public static final ResourceLocation TEXTURE = Helpers.identifier("textures/gui/icons/overlay.png");
    public static final ResourceLocation INK_TEXTURE = Helpers.identifier("textures/misc/ink_splatter.png");
    public static final ResourceLocation GLOW_INK_TEXTURE = Helpers.identifier("textures/misc/glow_ink_splatter.png");

    private static final ResourceLocation VANILLA_HEALTH = VanillaGuiOverlay.PLAYER_HEALTH.id();
    private static final ResourceLocation VANILLA_MOUNT_HEALTH = VanillaGuiOverlay.MOUNT_HEALTH.id();
    private static final ResourceLocation VANILLA_FOOD = VanillaGuiOverlay.FOOD_LEVEL.id();
    private static final ResourceLocation VANILLA_EXP = VanillaGuiOverlay.EXPERIENCE_BAR.id();
    private static final ResourceLocation VANILLA_JUMP = VanillaGuiOverlay.JUMP_BAR.id();

    public static void registerOverlays(RegisterGuiOverlaysEvent event)
    {
        above(event, VanillaGuiOverlay.PLAYER_HEALTH, HEALTH);
        above(event, VanillaGuiOverlay.MOUNT_HEALTH, MOUNT_HEALTH);
        above(event, VanillaGuiOverlay.FOOD_LEVEL, FOOD);
        above(event, VanillaGuiOverlay.FOOD_LEVEL, THIRST);
        above(event, VanillaGuiOverlay.EXPERIENCE_BAR, EXPERIENCE);
        above(event, VanillaGuiOverlay.JUMP_BAR, JUMP_BAR);
        above(event, VanillaGuiOverlay.PLAYER_HEALTH, HUD_MOVER);
        above(event, VanillaGuiOverlay.CROSSHAIR, FAMILIARITY);

        top(event, INK);
        top(event, CHISEL);
    }

    private static void above(RegisterGuiOverlaysEvent event, VanillaGuiOverlay vanilla, IngameOverlays overlay)
    {
        event.registerAbove(vanilla.id(), overlay.id, overlay.overlay);
    }

    private static void top(RegisterGuiOverlaysEvent event, IngameOverlays overlay)
    {
        event.registerAboveAll(overlay.id, overlay.overlay);
    }

    public static void checkGuiOverlays(RenderGuiOverlayEvent.Pre event)
    {
        final ResourceLocation id = event.getOverlay().id();
        if (id.equals(VANILLA_EXP) || id.equals(VANILLA_JUMP))
        {
            event.setCanceled(true);
        }
        else if (enableThisOrThat(id, TFCConfig.CLIENT.enableHungerBar.get(), FOOD.id(), VANILLA_FOOD) || enableThisOrThat(id, TFCConfig.CLIENT.enableHealthBar.get(), HEALTH.id(), VANILLA_HEALTH) || enableThisOrThat(id, TFCConfig.CLIENT.enableHealthBar.get(), MOUNT_HEALTH.id(), VANILLA_MOUNT_HEALTH))
        {
            event.setCanceled(true);
        }
        else if (disableIfFalse(id, TFCConfig.CLIENT.enableThirstBar.get(), THIRST.id()) || disableIfFalse(id, TFCConfig.CLIENT.enableInkSplatter.get(), INK.id()))
        {
            event.setCanceled(true);
        }
    }

    private static boolean enableThisOrThat(ResourceLocation id, boolean config, ResourceLocation myOverlay, ResourceLocation vanillaOverlay)
    {
        return (config && id.equals(vanillaOverlay)) || (!config && id.equals(myOverlay));
    }

    private static boolean disableIfFalse(ResourceLocation id, boolean config, ResourceLocation myOverlay)
    {
        return id.equals(myOverlay) && !config;
    }

    public static void renderHealth(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            final Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            renderHealthBar(player, gui, graphics, width, height);
        }
    }

    public static void renderMountHealth(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            final Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            if (player.getVehicle() instanceof final LivingEntity entity)
            {
                renderHealthBar(entity, gui, graphics, width, height);
            }
        }
    }

    public static void renderFood(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final PoseStack stack = graphics.pose();
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            int x = width / 2;
            int y = height - gui.rightHeight;
            float percentFood = (float) player.getFoodData().getFoodLevel() / PlayerInfo.MAX_HUNGER;

            stack.pushPose();
            stack.translate(x + 1, y + 4, 0);
            graphics.blit(TEXTURE, 0, 0, 0, 20, 90, 5);
            graphics.blit(TEXTURE, 0, 0, 0, 25, (int) (90 * percentFood), 5);
            stack.popPose();

            gui.rightHeight += 6;
        }
    }

    public static void renderThirst(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final PoseStack stack = graphics.pose();
        final Minecraft minecraft = Minecraft.getInstance();
        if (setupForSurvival(gui, minecraft))
        {
            Player player = (Player) minecraft.getCameraEntity();
            assert player != null;

            final IPlayerInfo info = IPlayerInfo.get(player);

            int x = width / 2;
            int y = height - gui.rightHeight;
            float percentThirst = info.getThirst() / PlayerInfo.MAX_THIRST;
            float overheat = info.getThirstContributionFromTemperature(player);

            stack.pushPose();
            stack.translate(x + 1, y + 4, 0);
            graphics.blit(TEXTURE, 0, 0, 90, 20, 90, 5);
            graphics.blit(TEXTURE, 0, 0, 90, 25, (int) (90 * percentThirst), 5);
            if (overheat > 0)
            {
                RenderSystem.setShaderColor(1f, 1f, 1f, overheat / PlayerInfo.MAX_TEMPERATURE_THIRST_DECAY);
                graphics.blit(TEXTURE, 0, 0, 90, 30, 90, 5);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            }
            stack.popPose();

            gui.rightHeight += 6;
        }
    }

    private static void renderChiselMode(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final PoseStack stack = graphics.pose();
        final Minecraft mc = Minecraft.getInstance();
        if (setup(gui, mc))
        {
            final Player player = ClientHelpers.getPlayer();
            if (player != null && Helpers.isItem(player.getItemInHand(InteractionHand.MAIN_HAND), TFCTags.Items.CHISELS))
            {
                int u = 60;
                if (Helpers.isItem(player.getItemInHand(InteractionHand.OFF_HAND), TFCTags.Items.HAMMERS))
                {
                    u = IPlayerInfo.get(player).chiselMode().ordinal() * 20;
                }
                stack.pushPose();
                graphics.blit(TEXTURE, width / 2 + 100, height - 21, u, 58, 20, 20);
                stack.popPose();
            }
        }
    }

    private static void renderExperience(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final PoseStack stack = graphics.pose();
        final Minecraft mc = Minecraft.getInstance();
        final @Nullable LocalPlayer localPlayer = mc.player;
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
                if (localPlayer.getVehicle() instanceof LivingEntity && TFCConfig.CLIENT.enableHealthBar.get()) // Increase the bar's height if a second health bar is present
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
                graphics.blit(TEXTURE, x, y, uOffset, 36, 5, barHeight);
                if (amount > 0)
                {
                    graphics.blit(TEXTURE, x, height - amount, uOffset + 5, texturePos - amount, 5, amount);
                }
            }
            else
            {
                final int x = width / 2 - 91;
                final int y = height - 29;
                final int amount = Mth.ceil(Mth.clampedMap(hook.pullExhaustion, 0, 100, 0, 183));
                graphics.blit(TEXTURE, x, y, 0, 111, 182, 5);
                if (amount > 0)
                {
                    graphics.blit(TEXTURE, x, y, 0, 116, amount, 5);
                }
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else if (isShowingExperience)
        {
            final NamedGuiOverlay overlay = GuiOverlayManager.findOverlay(VANILLA_EXP);
            if (overlay != null)
            {
                overlay.overlay().render(gui, graphics, partialTicks, width, height);
            };
        }
    }

    private static void renderJumpBar(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        final PoseStack stack = graphics.pose();
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer localPlayer = mc.player;
        final boolean isShowingExperience = TFCConfig.CLIENT.enableExperienceBar.get();
        final boolean isStyleLeftHotbar = (TFCConfig.CLIENT.disabledExperienceBarStyle.get() == DisabledExperienceBarStyle.LEFT_HOTBAR);
        if (isShowingExperience || !isStyleLeftHotbar)
        {
            final NamedGuiOverlay overlay = GuiOverlayManager.findOverlay(VANILLA_JUMP);
            if (overlay != null)
            {
                overlay.overlay().render(gui, graphics, partialTicks, width, height);
            };
        }
        else if (localPlayer != null && localPlayer.jumpableVehicle() != null && setup(gui, mc))
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
            graphics.blit(TEXTURE, x, y, uOffset, 36, 5, barHeight);
            if (charge > 0)
            {
                graphics.blit(TEXTURE, x, height - charge, uOffset + 5, texturePos - charge, 5, charge);
            }

            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static void renderInk(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width, int height)
    {
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson())
        {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null)
            {
                if (player.hasEffect(TFCEffects.INK.get()))
                {
                    renderTextureOverlay(graphics, INK_TEXTURE, 1F);
                }
                else if (player.hasEffect(TFCEffects.GLOW_INK.get()))
                {
                    renderTextureOverlay(graphics, GLOW_INK_TEXTURE, 1F);
                }
            }
        }
    }

    private static void renderHealthBar(LivingEntity entity, ForgeGui gui, GuiGraphics graphics, int width, int height)
    {
        final PoseStack stack = graphics.pose();
        HealthDisplayStyle style = TFCConfig.CLIENT.healthDisplayStyle.get();
        float maxHealth = entity.getMaxHealth();

        int centerX = width / 2;
        int y = height - gui.leftHeight;

        stack.pushPose();
        stack.translate(centerX - 91, y, 0);
        graphics.blit(TEXTURE, 0, 0, 0, 0, 90, 10);

        float absorption = entity.getAbsorptionAmount();
        absorption = Float.isNaN(absorption) ? 0 : absorption;
        float percentHealth = (entity.getHealth() + absorption) / entity.getMaxHealth();
        float currentHealth = percentHealth * maxHealth;
        percentHealth = Mth.clamp(percentHealth, 0, 1);

        graphics.blit(TEXTURE, 0, 0, 0, 10, (int) (90 * percentHealth), 10);

        boolean isHurt = entity.getHealth() > 0.0F && entity.getHealth() < entity.getMaxHealth();
        boolean playerHasSaturation = entity instanceof Player player && player.getFoodData().getSaturationLevel() > 0;
        if ((playerHasSaturation && isHurt) || entity.hurtTime > 0 || entity.hasEffect(MobEffects.REGENERATION))
        {
            graphics.blit(TEXTURE, 0, 1, 0, 30, 90, 8);
        }

        float surplusPercent = Mth.clamp(percentHealth + (absorption / 20f) - 1, 0, 1);
        if (surplusPercent > 0)
        {
            // fill up the yellow bar until you get a second full bar, then just fill it up
            float percent = Math.min(surplusPercent, 1);
            graphics.blit(TEXTURE, 0, 0, 90, 10, (int) (90 * percent), 10);
        }
        stack.popPose();

        // Health modifier affects both max and current health equally. All we do is draw different numbers as a result.
        final float healthModifier = entity instanceof Player player ? IPlayerInfo.get(player).getHealthModifier() : 1f;

        String text = style.format(currentHealth * healthModifier, maxHealth * healthModifier);
        stack.pushPose();
        stack.translate(centerX - 45, y + 2.5, 0);
        stack.scale(0.8f, 0.8f, 1.0f);
        graphics.drawString(gui.getFont(), text, -1 * gui.getFont().width(text) / 2, 0, surplusPercent < 0.6 ? Color.WHITE.getRGB() : Color.BLACK.getRGB(), false);
        stack.popPose();

        gui.leftHeight += 10;
    }

    private static void renderFamiliarity(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        Player player = mc.player;

        if (player.isShiftKeyDown() && IngameOverlays.setup(gui, mc))
        {
            Entity entity = mc.crosshairPickEntity;
            if (entity instanceof TFCAnimalProperties animal && animal.getAdultFamiliarityCap() > 0)
            {
                if (player.closerThan(entity, 5.0F))
                {
                    PoseStack stack = graphics.pose();
                    stack.pushPose();

                    stack.translate(width / 2f, height / 2f - 45, 0);
                    stack.scale(1.5f, 1.5f, 1.5f);

                    float familiarity = Math.max(0.0F, Math.min(1.0F, animal.getFamiliarity()));
                    int u;
                    int fontColor;
                    if (familiarity >= animal.getAdultFamiliarityCap() && animal.getAgeType() != TFCAnimalProperties.Age.CHILD)
                    {
                        u = 132; // Render a red-ish outline for adults that cannot be familiarized more
                        fontColor = Color.RED.getRGB();
                    }
                    else if (familiarity >= 0.3F)
                    {
                        u = 112; // Render a white outline for the when the familiarity stopped decaying
                        fontColor = Color.WHITE.getRGB();
                    }
                    else
                    {
                        u = 92;
                        fontColor = Color.GRAY.getRGB();
                    }

                    if (TFCConfig.CLIENT.displayFamiliarityAsPercent.get())
                    {
                        String string = String.format("%.2f", familiarity * 100);

                        graphics.drawString(mc.font, string,-mc.font.width(string) / 2, 0, fontColor, false);

                    }
                    else
                    {
                        graphics.blit(IngameOverlays.TEXTURE, -8, 0, u, 40, 16, 16);

                        stack.translate(0F, 0F,-0.001F);
                        graphics.blit(IngameOverlays.TEXTURE, -6, 14 - (int) (12 * familiarity), familiarity == 1.0F ? 114 : 94, 74 - (int) (12 * familiarity), 12, (int) (12 * familiarity));
                    }
                    if (animal instanceof MammalProperties mammal && mammal.getPregnantTime() > 0 && mammal.isFertilized())
                    {
                        stack.translate(0, -15F, 0F);
                        String string = Component.translatable("tfc.tooltip.animal.pregnant", entity.getName().getString()).getString();
                        graphics.drawString(mc.font, string,-mc.font.width(string) / 2, 0, Color.WHITE.getRGB(), false);
                    }

                    stack.popPose();
                }
            }
        }
    }


    private static void renderTextureOverlay(GuiGraphics graphics, ResourceLocation location, float alpha)
    {
        final Minecraft mc = Minecraft.getInstance();
        final int screenWidth = mc.getWindow().getGuiScaledWidth();
        final int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        graphics.setColor(1.0F, 1.0F, 1.0F, alpha);
        graphics.blit(location, 0, 0, -90, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void moveLeftAndRightHeights(ForgeGui gui, GuiGraphics stack, float partialTicks, int width, int height)
    {
        gui.rightHeight -= considerExperienceConfigs();
        gui.leftHeight -= considerExperienceConfigs();
    }

    private static boolean setupForSurvival(ForgeGui gui, Minecraft minecraft)
    {
        return gui.shouldDrawSurvivalElements() && setup(gui, minecraft);
    }

    public static boolean setup(ForgeGui gui, Minecraft minecraft)
    {
        if (!minecraft.options.hideGui && minecraft.getCameraEntity() instanceof Player)
        {
            gui.setupOverlayRenderState(true, false);
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
            case BUMP -> player != null && (player.fishing instanceof TFCFishingHook || player.jumpableVehicle() != null) ? 0 : 6;
            default -> 0;
        };
    }
}
