/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.types.MetalItem;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.minecraft.util.text.TextFormatting.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onInitGuiPre(GuiScreenEvent.InitGuiEvent.Pre event)
    {
        LOGGER.debug("Init Gui Pre Event");
        if (event.getGui() instanceof CreateWorldScreen)
        {
            // Only change if default is selected, because coming back from customisation, this will be set already.
            CreateWorldScreen gui = ((CreateWorldScreen) event.getGui());
            Integer selectedIndex = ObfuscationReflectionHelper.getPrivateValue(CreateWorldScreen.class, gui, "field_146331_K");
            if (selectedIndex != null && selectedIndex == WorldType.DEFAULT.getId())
            {
                LOGGER.debug("Setting Selected World Type to TFC Default");
                ObfuscationReflectionHelper.setPrivateValue(CreateWorldScreen.class, gui, TFCWorldType.INSTANCE.getId(), "field_146331_K");
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        Minecraft mc = Minecraft.getInstance();
        List<String> list = event.getRight();
        if (mc.world != null && mc.gameSettings.showDebugInfo) // todo: config
        {
            //noinspection ConstantConditions
            BlockPos pos = new BlockPos(mc.getRenderViewEntity().getPosX(), mc.getRenderViewEntity().getBoundingBox().minY, mc.getRenderViewEntity().getPosZ());
            if (mc.world.chunkExists(pos.getX() >> 4, pos.getZ() >> 4))
            {
                list.add("");
                list.add(AQUA + TerraFirmaCraft.MOD_NAME);

                // Always add calendar info
                list.add(I18n.format("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getFormattedText());
                list.add(I18n.format("tfc.tooltip.debug_times", Calendars.CLIENT.getTicks(), Calendars.CLIENT.getCalendarTicks(), mc.getRenderViewEntity().world.getDayTime() % ICalendar.TICKS_IN_DAY));

                ChunkData data = ChunkData.get(mc.world, pos);
                if (data.getStatus().isAtLeast(ChunkData.Status.CLIENT))
                {
                    list.add(GRAY + I18n.format("tfc.tooltip.f3_average_temperature", WHITE + String.format("%.1f", data.getAverageTemp(pos))));
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
            MetalItem.addTooltipInfo(stack, text);
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

            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 4, 20 + 3, 22, 96 + 20, 0, 1, 3, 0, 0, button -> {}).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 96, 0, 1, 3, 32, 0, button -> PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.CALENDAR))).setRecipeBookCallback(screen));
            event.addWidget(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 96, 0, 1, 3, 64, 0, button -> PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.NUTRITION))).setRecipeBookCallback(screen));
        }
    }
}
