/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.renderer.color.ColorCache;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.client.world.ClientWorldAccessor;
import net.dries007.tfc.mixin.client.world.DimensionRenderInfoAccessor;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.minecraft.util.text.TextFormatting.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler
{
    @SubscribeEvent
    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> list = event.getRight();
        if (mc.world != null && mc.gameSettings.glDebugVerbosity==1) // todo: config
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getRenderViewEntity().getPosX(), mc.getRenderViewEntity().getBoundingBox().minY, mc.getRenderViewEntity().getPosZ());
            if (mc.world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4))
            {
                list.add("");
                list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                // Always add calendar info
                list.add(I18n.format("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString());
                list.add(I18n.format("tfc.tooltip.debug_times", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getRenderViewEntity().world.getDayTime() % ICalendar.TICKS_IN_DAY));

                ChunkData data = ChunkData.get(mc.world, pos);
                if (data.getStatus().isAtLeast(ChunkData.Status.CLIENT))
                {
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", data.getAverageTemp(pos))));
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_temperature", WHITE + String.format("%.1f", Climate.calculateTemperature(pos, data.getAverageTemp(pos), Calendars.CLIENT))));
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_rainfall", WHITE + String.format("%.1f", data.getRainfall(pos))));
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_forest_type") + WHITE + I18n.format(Helpers.getEnumTranslationKey(data.getForestType())));
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_forest_properties",
                        WHITE + String.format("%.1f%%", 100 * data.getForestDensity()) + GRAY,
                        WHITE + String.format("%.1f%%", 100 * data.getForestWeirdness()) + GRAY));
                }
                else
                {
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_invalid_chunk_data"));
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
            ((DimensionRenderInfoAccessor) DimensionRenderInfoAccessor.accessor$Effects().get(DimensionType.OVERWORLD)).accessor$setCloudLevel(cloudHeight);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        World world = Minecraft.getInstance().world;
        if (event.phase == TickEvent.Phase.END && world != null && !Minecraft.getInstance().isGamePaused())
        {
            Calendars.CLIENT.onClientTick();
            ClimateRenderCache.INSTANCE.onClientTick();
        }
    }
}