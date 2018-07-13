/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.render.RenderFallingBlockTFC;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.util.CapabilityItemSize;
import net.dries007.tfc.util.IItemSize;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.minecraft.util.text.TextFormatting.*;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public class ClientEvents
{
    public static void preInit()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingBlockTFC.class, RenderFallingBlockTFC::new);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void openGUI(GuiScreenEvent.InitGuiEvent.Pre event)
    {
        if (ConfigTFC.CLIENT.makeWorldTypeClassicDefault && event.getGui() instanceof GuiCreateWorld)
        {
            GuiCreateWorld gui = ((GuiCreateWorld) event.getGui());
            // Only change if default is selected, because coming back from customisation, this will be set already.
            if (gui.selectedIndex == WorldType.DEFAULT.getId())
                gui.selectedIndex = TerraFirmaCraft.getWorldTypeTFC().getId();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onRenderGameOverlayText(RenderGameOverlayEvent.Text event)
    {
        // todo: check if this is allowed to be displayed, debug/op only maybe?
        Minecraft mc = Minecraft.getMinecraft();
        List<String> list = event.getRight();
        if (ConfigTFC.GENERAL.debug && mc.gameSettings.showDebugInfo)
        {
            BlockPos blockpos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
            Chunk chunk = mc.world.getChunkFromBlockCoords(blockpos);
            if (mc.world.isBlockLoaded(blockpos) && !chunk.isEmpty())
            {
                final int x = blockpos.getX() & 15, z = blockpos.getZ() & 15;
                ChunkDataTFC data = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);

                list.add("");
                list.add(AQUA + "TerraFirmaCraft");

                if (data == null || !data.isInitialized()) list.add("No data ?!");
                else
                {
                    list.add(String.format("%sTemps: Base: %s%.0f°%s Bio: %s%.0f°%s Height adjusted: %s%.0f°",
                            GRAY, WHITE, ClimateTFC.getTemp(mc.world, blockpos), GRAY,
                            WHITE, ClimateTFC.getBioTemperatureHeight(mc.world, blockpos), GRAY,
                            WHITE, ClimateTFC.getHeightAdjustedTemp(mc.world, blockpos)
                    ));
                    list.add(String.format("%sTime: %s%02d:%02d %04d/%02d/%02d",
                            GRAY, WHITE,
                            CalenderTFC.getHourOfDay(),
                            CalenderTFC.getMinuteOfHour(),
                            CalenderTFC.getTotalYears(),
                            CalenderTFC.getMonthOfYear(),
                            CalenderTFC.getDayOfMonth()
                            )
                    );

                    list.add(GRAY + "Biome: " + WHITE + mc.world.getBiome(blockpos).getBiomeName());

                    list.add(GRAY + "Rocks: " + WHITE + data.getRockLayer1(x, z).name + ", " + data.getRockLayer2(x, z).name + ", " + data.getRockLayer3(x, z).name);
                    list.add(GRAY + "EVT: " + WHITE + data.getEvtLayer(x, z).name);
                    list.add(GRAY + "Rainfall: " + WHITE + data.getRainfallLayer(x, z).name);
                    list.add(GRAY + "Stability: " + WHITE + data.getStabilityLayer(x, z).name);
                    list.add(GRAY + "Drainage: " + WHITE + data.getDrainageLayer(x, z).name);
                    list.add(GRAY + "Sea level offset: " + WHITE + data.getSeaLevelOffset(x, z));
                    list.add(GRAY + "Fish population: " + WHITE + data.getFishPopulation());

                    list.add("");
                    list.add(GRAY + "Rock at feet: " + WHITE + data.getRockLayerHeight(x, blockpos.getY(), z).name);

                    list.add("");
                    data.getOresSpawned().stream().map(String::valueOf).forEach(list::add);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();
        List<String> tt = event.getToolTip();

        IItemSize size = CapabilityItemSize.getIItemSize(stack);
        if (size != null)
        {
            size.addSizeInfo(stack, tt);
        }

        if (event.getFlags().isAdvanced())
        {
            Set<String> toolClasses = item.getToolClasses(stack);
            if (!toolClasses.isEmpty())
            {
                tt.add("");
                for (String toolClass : toolClasses)
                {
                    tt.add(I18n.format("tfc.tooltip.toolclass", toolClass));
                }
            }
            if (item instanceof IMetalObject)
                ((IMetalObject) item).addMetalInfo(stack, tt);
            if (item instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof IMetalObject)
                {
                    ((IMetalObject) block).addMetalInfo(stack, tt);
                }
            }

            int[] ids = OreDictionary.getOreIDs(stack);
            if (ids != null && ids.length != 0)
            {
                tt.add("");
                tt.add(TextFormatting.AQUA + "Ore Dictionary:");
                Arrays.stream(ids).mapToObj(OreDictionary::getOreName).sorted().forEachOrdered(tt::add);
            }
        }
    }
}
