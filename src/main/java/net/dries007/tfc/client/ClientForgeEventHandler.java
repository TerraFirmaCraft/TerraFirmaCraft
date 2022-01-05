/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.accessor.ClientLevelAccessor;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.PlaceBlockSpecialPacket;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.SurfaceManager;
import net.dries007.tfc.world.surface.builder.BadlandsSurfaceBuilder;
import net.dries007.tfc.world.surface.builder.SurfaceBuilder;

import static net.minecraft.ChatFormatting.*;

public class ClientForgeEventHandler
{
    private static final Field CAP_NBT_FIELD = Helpers.findUnobfField(ItemStack.class, "capNBT");

    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayText);
        bus.addListener(ClientForgeEventHandler::onRenderGameOverlayPost);
        bus.addListener(ClientForgeEventHandler::onItemTooltip);
        bus.addListener(ClientForgeEventHandler::onInitGuiPost);
        bus.addListener(ClientForgeEventHandler::onClientWorldLoad);
        bus.addListener(ClientForgeEventHandler::onClientTick);
        bus.addListener(ClientForgeEventHandler::onKeyEvent);
        bus.addListener(ClientForgeEventHandler::onHighlightBlockEvent);
        bus.addListener(ClientForgeEventHandler::onFogDensity);
    }

    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> list = event.getRight();
        if (mc.level != null && mc.options.renderDebug && TFCConfig.CLIENT.enableTFCF3Overlays.get())
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getCameraEntity().getX(), mc.getCameraEntity().getBoundingBox().minY, mc.getCameraEntity().getZ());
            if (mc.level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4))
            {
                list.add("");
                list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                // Always add calendar info
                list.add(I18n.get("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString());

                if (TFCConfig.CLIENT.enableDebug.get())
                {
                    list.add(String.format("[Debug] Ticks = %d, Calendar = %d, Daytime = %d", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getCameraEntity().level.getDayTime() % ICalendar.TICKS_IN_DAY));
                }

                // Always add climate data
                list.add(GRAY + I18n.get("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", ClimateRenderCache.INSTANCE.getAverageTemperature())));
                list.add(GRAY + I18n.get("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", ClimateRenderCache.INSTANCE.getTemperature())));
                list.add(GRAY + I18n.get("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", ClimateRenderCache.INSTANCE.getRainfall())));

                ChunkData data = ChunkData.get(mc.level, pos);
                if (data.getStatus() == ChunkData.Status.CLIENT)
                {
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

    /**
     * Render overlays for looking at particular block / item combinations
     */
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        final PoseStack stack = event.getMatrixStack();
        final Minecraft minecraft = Minecraft.getInstance();
        final Player player = minecraft.player;
        if (player != null)
        {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL && minecraft.screen == null && (TFCTags.Items.HOES.contains(player.getMainHandItem().getItem())) || TFCTags.Items.HOES.contains(player.getOffhandItem().getItem()) && (!TFCConfig.CLIENT.showHoeOverlaysOnlyWhenShifting.get() && player.isShiftKeyDown()))
            {
                HoeOverlays.render(minecraft, event.getWindow(), stack);
            }
        }
    }

    public static void onItemTooltip(ItemTooltipEvent event)
    {
        final ItemStack stack = event.getItemStack();
        final List<Component> text = event.getToolTip();
        if (!stack.isEmpty())
        {
            ItemSizeManager.addTooltipInfo(stack, text);
            stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.addTooltipInfo(stack, text));

            // Metal content, inferred from a matching heat recipe.
            ItemStackInventory wrapper = new ItemStackInventory(stack);
            HeatingRecipe recipe = HeatingRecipe.getRecipe(wrapper);
            if (recipe != null)
            {
                // Check what we would get if melted
                final FluidStack fluid = recipe.getOutputFluid(wrapper);
                if (!fluid.isEmpty())
                {
                    final Metal metal = Metal.get(fluid.getFluid());
                    if (metal != null)
                    {
                        final MutableComponent line = new TranslatableComponent("tfc.tooltip.item_melts_into", (fluid.getAmount() * stack.getCount()))
                            .append(new TranslatableComponent(metal.getTranslationKey()));
                        final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(recipe.getTemperature());
                        if (heat != null)
                        {
                            line.append(new TranslatableComponent("tfc.tooltip.item_melts_into_open"))
                                .append(heat)
                                .append(new TranslatableComponent("tfc.tooltip.item_melts_into_close"));
                        }
                        text.add(line);
                    }
                }
            }

            // Fuel information
            final Fuel fuel = Fuel.get(stack);
            if (fuel != null)
            {
                final MutableComponent heat = TFCConfig.CLIENT.heatTooltipStyle.get().formatColored(fuel.getTemperature());
                if (heat != null)
                {
                    text.add(new TranslatableComponent("tfc.tooltip.fuel_burns_at")
                        .append(heat)
                        .append(new TranslatableComponent("tfc.tooltip.fuel_burns_at_duration"))
                        .append(Calendars.CLIENT.getTimeDelta(fuel.getDuration())));
                }
            }

            if (TFCConfig.CLIENT.enableDebug.get())
            {
                final CompoundTag stackTag = stack.getTag();
                if (stackTag != null)
                {
                    text.add(new TextComponent(GRAY + "[Debug] NBT: " + DARK_GRAY + stackTag));
                }

                final CompoundTag capTag = Helpers.uncheck(() -> CAP_NBT_FIELD.get(stack));
                if (capTag != null)
                {
                    text.add(new TextComponent(GRAY + "[Debug] Cap NBT: " + DARK_GRAY + capTag));
                }

                final Set<ResourceLocation> tags = stack.getItem().getTags();
                if (!tags.isEmpty())
                {
                    text.add(new TextComponent(GRAY + "[Debug] Tags: " + DARK_GRAY + tags.stream().map(t -> "#" + t).collect(Collectors.joining(", "))));
                }
            }
        }
    }

    public static void onInitGuiPost(ScreenEvent.InitScreenEvent.Post event)
    {
        Player player = Minecraft.getInstance().player;
        if (event.getScreen() instanceof InventoryScreen screen && player != null && !player.isCreative())
        {
            int guiLeft = screen.getGuiLeft();
            int guiTop = screen.getGuiTop();

            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 128 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback(screen));
            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR).setRecipeBookCallback(screen));
            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION).setRecipeBookCallback(screen));
            event.addListener(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE).setRecipeBookCallback(screen));
        }
    }

    public static void onClientWorldLoad(WorldEvent.Load event)
    {
        if (event.getWorld() instanceof final ClientLevel level)
        {
            // Add our custom tints to the color resolver caches
            final Object2ObjectArrayMap<ColorResolver, BlockTintCache> colorCaches = ((ClientLevelAccessor) level).accessor$getTintCaches();

            colorCaches.putIfAbsent(TFCColors.FRESH_WATER, new BlockTintCache(TFCColors::getWaterColor));
            colorCaches.putIfAbsent(TFCColors.SALT_WATER, new BlockTintCache(TFCColors::getWaterColor));

        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Level world = Minecraft.getInstance().level;
        if (event.phase == TickEvent.Phase.END && world != null && !Minecraft.getInstance().isPaused())
        {
            Calendars.CLIENT.onClientTick();
            ClimateRenderCache.INSTANCE.onClientTick();
        }
    }

    public static void onKeyEvent(InputEvent.KeyInputEvent event)
    {
        if (TFCKeyBindings.PLACE_BLOCK.isDown())
        {
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new PlaceBlockSpecialPacket());
        }
    }

    /**
     * Handles custom bounding boxes drawing
     * eg: Chisel, Quern handle
     */
    public static void onHighlightBlockEvent(DrawSelectionEvent.HighlightBlock event)
    {
        final Camera camera = event.getCamera();
        final PoseStack poseStack = event.getPoseStack();
        final Entity entity = camera.getEntity();
        final Level level = entity.level;
        final BlockHitResult traceResult = event.getTarget();
        final BlockPos lookingAt = new BlockPos(traceResult.getLocation());

        //noinspection ConstantConditions
        if (lookingAt != null && entity instanceof Player player)
        {
            Block blockAt = level.getBlockState(lookingAt).getBlock();
            //todo: chisel
            if (blockAt instanceof IHighlightHandler handler)
            {
                // Pass on to custom implementations
                if (handler.drawHighlight(level, lookingAt, player, traceResult, poseStack, event.getMultiBufferSource(), camera.getPosition()))
                {
                    // Cancel drawing this block's bounding box
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onFogDensity(EntityViewRenderEvent.RenderFogEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && (event.getMode() == FogRenderer.FogMode.FOG_TERRAIN))
        {
            final float fog = Climate.getFogginess(mc.level, event.getCamera().getBlockPosition());
            if (fog == 0) return;
            final float renderDistance = mc.gameRenderer.getRenderDistance();
            final float density = renderDistance * (1 - fog);

            // let's just do this the same way MC does because the FogDensityEvent is crap
            RenderSystem.setShaderFogStart(density - Mth.clamp(renderDistance / 10.0F, 4.0F, 64.0F));
            RenderSystem.setShaderFogEnd(density);
        }
    }
}