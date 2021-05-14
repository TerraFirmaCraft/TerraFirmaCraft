/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.awt.*;
import java.util.List;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.world.ClientWorldAccessor;
import net.dries007.tfc.mixin.client.world.DimensionRenderInfoAccessor;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PlaceBlockSpecialPacket;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.config.HealthDisplayFormat;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.minecraft.util.text.TextFormatting.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler
{
    private static final ResourceLocation ICONS = Helpers.identifier("textures/gui/icons/overlay.png");

    @SubscribeEvent
    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> list = event.getRight();
        if (mc.level != null && mc.options.renderDebug) // todo: config
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getCameraEntity().getX(), mc.getCameraEntity().getBoundingBox().minY, mc.getCameraEntity().getZ());
            if (mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4))
            {
                list.add("");
                list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                // Always add calendar info
                list.add(I18n.get("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString());
                list.add(I18n.get("tfc.tooltip.debug_times", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getCameraEntity().level.getDayTime() % ICalendar.TICKS_IN_DAY));

                ChunkData data = ChunkData.get(mc.level, pos);
                if (data.getStatus().isAtLeast(ChunkData.Status.CLIENT))
                {
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", data.getAverageTemp(pos))));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", Climate.calculateTemperature(pos, data.getAverageTemp(pos), Calendars.CLIENT))));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", data.getRainfall(pos))));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_forest_type") + WHITE + I18n.get(Helpers.getEnumTranslationKey(data.getForestType())));
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_forest_properties",
                        WHITE + String.format("%.1f%%", 100 * data.getForestDensity()) + GRAY,
                        WHITE + String.format("%.1f%%", 100 * data.getForestWeirdness()) + GRAY));
                }
                else
                {
                    list.add(GRAY + I18n.get("tfc.tooltip.f3_invalid_chunk_data"));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        PlayerEntity player = event.getPlayer();
        List<ITextComponent> text = event.getToolTip();
        if (!stack.isEmpty() && player != null)
        {
            MetalItemManager.addTooltipInfo(stack, text);
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addHeatInfo(stack, text));
            if (event.getFlags().isAdvanced())
            {
                FuelManager.addTooltipInfo(stack, text);
            }
        }
    }

    @SubscribeEvent
    public static void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if (event.getGui() instanceof InventoryScreen && player != null && !player.isCreative())
        {
            InventoryScreen screen = (InventoryScreen) event.getGui();
            int guiLeft = ((InventoryScreen) event.getGui()).getGuiLeft();
            int guiTop = ((InventoryScreen) event.getGui()).getGuiTop();

            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 128 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE).setRecipeBookCallback(screen));
        }
    }

    @SubscribeEvent
    public static void onClientWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof ClientWorld)
        {
            final ClientWorld world = (ClientWorld) event.getWorld();

            // Add our custom tints to the color resolver caches
            final Object2ObjectArrayMap<ColorResolver, ColorCache> colorCaches = ((ClientWorldAccessor) world).getTintCaches();

            colorCaches.putIfAbsent(TFCColors.FRESH_WATER, new ColorCache());
            colorCaches.putIfAbsent(TFCColors.SALT_WATER, new ColorCache());

            // Update cloud height
            final float cloudHeight = TFCConfig.CLIENT.assumeTFCWorld.get() ? 210 : 160;
            ((DimensionRenderInfoAccessor) DimensionRenderInfoAccessor.accessor$Effects().get(DimensionType.OVERWORLD_EFFECTS)).accessor$setCloudLevel(cloudHeight);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        World world = Minecraft.getInstance().level;
        if (event.phase == TickEvent.Phase.END && world != null && !Minecraft.getInstance().isPaused())
        {
            Calendars.CLIENT.onClientTick();
            ClimateRenderCache.INSTANCE.onClientTick();
        }
    }

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if (TFCKeyBindings.PLACE_BLOCK.isDown())
        {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new PlaceBlockSpecialPacket());
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void render(RenderGameOverlayEvent.Pre event)
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
        {
            return;
        }
        final IngameGui gui = mc.gui;
        final PlayerEntity player = mc.player;

        ForgeIngameGui.renderFood = !TFCConfig.CLIENT.enableHungerBar.get();
        ForgeIngameGui.renderHealth = !TFCConfig.CLIENT.enableHealthBar.get();
        ForgeIngameGui.renderExperiance = !TFCConfig.CLIENT.enableHealthBar.get() && !TFCConfig.CLIENT.enableThirstBar.get(); // only allow vanilla exp in this case
        HealthDisplayFormat healthDisplayFormat = TFCConfig.CLIENT.healthDisplayFormat.get();

        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            return;
        }

        if (TFCConfig.CLIENT.enableThirstBar.get() || TFCConfig.CLIENT.enableHungerBar.get())
        {
            ForgeIngameGui.right_height += TFCConfig.CLIENT.enableHungerBar.get() ? 10 : 6;
        }

        MatrixStack matrixStack = event.getMatrixStack();

        float displayModifier = (healthDisplayFormat == HealthDisplayFormat.TFC || healthDisplayFormat == HealthDisplayFormat.TFC_CURRENT) ? 50f : 1f;
        float maxHealth = player.getMaxHealth() * displayModifier; // 20 * 50 = 1000
        float currentThirst = 100f; // todo: update to fetch from thirst once implemented

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

        if (!player.abilities.instabuild && !player.isSpectator())
        {
            float foodLevel = player.getFoodData().getFoodLevel();
            float percentFood = foodLevel / 20f;
            float percentThirst = currentThirst / 100f;

            if (TFCConfig.CLIENT.enableHungerBar.get())
            {
                matrixStack.pushPose();
                matrixStack.translate(mid + 1, healthRowHeight, 0);
                gui.blit(matrixStack, 0, 0, 0, 20, 90, 5);
                gui.blit(matrixStack, 0, 0, 0, 25, (int) (90 * percentFood), 5);
                matrixStack.popPose();
            }

            if (TFCConfig.CLIENT.enableThirstBar.get())
            {
                matrixStack.pushPose();
                matrixStack.translate(mid + 1, healthRowHeight + 5, 0);
                gui.blit(matrixStack, 0, 0, 90, 20, 90, 5);
                gui.blit(matrixStack, 0, 0, 90, 25, (int) (90 * percentThirst), 5);
                matrixStack.popPose();
            }

            if (TFCConfig.CLIENT.enableHealthBar.get())
            {
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
                {
                    gui.blit(matrixStack, 0, 1, 0, 30, 90, 8);
                }
                float surplusPercent = MathHelper.clamp(percentHealth + (absorption / 20f) - 1, 0, 1);
                if (surplusPercent > 0)
                {
                    // fill up the yellow bar until you get a second full bar, then just fill it up
                    float percent = Math.min(surplusPercent, 1);
                    gui.blit(matrixStack, 0, 0, uSurplus, 10, (int) (90 * percent), 10);
                }
                matrixStack.popPose();

                String healthString = healthDisplayFormat.format(currentHealth, maxHealth);
                matrixStack.pushPose();
                matrixStack.translate(mid - 45, healthRowHeight + 2.5, 0);
                matrixStack.scale(0.8f, 0.8f, 1.0f);
                fontRenderer.draw(matrixStack, healthString, -1 * fontRenderer.width(healthString) / 2f, 0, surplusPercent < 0.6 ? Color.white.getRGB() : Color.black.getRGB());
                matrixStack.popPose();

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }

            textureManager.bind(AbstractGui.GUI_ICONS_LOCATION);
            Entity mount = player.getVehicle();
            if (!(mount instanceof LivingEntity) && mc.gameMode != null && mc.gameMode.hasExperience() && !ForgeIngameGui.renderExperiance)
            {
                int left = mid - 91;
                int xpNeeded = player.getXpNeededForNextLevel();
                if (xpNeeded > 0)
                {
                    int filled = (int) (player.experienceProgress * 183.0F);
                    int top = guiScaledHeight - 29;
                    matrixStack.pushPose();
                    matrixStack.translate(left, top, 0);
                    gui.blit(matrixStack, 0, 0, 0, 64, 182, 5);
                    if (filled > 0)
                    {
                        gui.blit(matrixStack, 0, 0, 0, 69, filled, 5);
                    }
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
            if (mount instanceof LivingEntity && TFCConfig.CLIENT.enableHealthBar.get())
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
                {
                    gui.blit(matrixStack, 0, 1, 0, 30, 90, 8);
                }
                matrixStack.popPose();

                String mountHealthString = healthDisplayFormat.format(mountCurrentHealth, mountMaxHealth);
                matrixStack.pushPose();
                matrixStack.translate(mid + 47, armorRowHeight + 2.5, 0);
                matrixStack.scale(0.8f, 0.8f, 1.0f);
                fontRenderer.draw(matrixStack, mountHealthString, -1 * (fontRenderer.width(mountHealthString) / 2f), 0, mountPercentHealth < 0.6 ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
                matrixStack.popPose();

                RenderSystem.enableBlend();
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }
}