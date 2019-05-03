/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import java.util.ArrayList;
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
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.client.render.RenderFallingBlockTFC;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;
import net.dries007.tfc.world.classic.ClimateRenderHelper;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
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
        Minecraft mc = Minecraft.getMinecraft();
        List<String> list = event.getRight();
        if (ConfigTFC.GENERAL.debug && mc.gameSettings.showDebugInfo)
        {
            //noinspection ConstantConditions
            BlockPos blockpos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
            Chunk chunk = mc.world.getChunk(blockpos);
            if (mc.world.isBlockLoaded(blockpos) && !chunk.isEmpty())
            {
                final int x = blockpos.getX() & 15, z = blockpos.getZ() & 15;
                ChunkDataTFC data = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);

                list.add("");
                list.add(AQUA + "TerraFirmaCraft");

                if (data == null || !data.isInitialized()) list.add("No data ?!");
                else
                {
                    list.add(String.format("%sTemp: Base: %s%.1f\u00b0C%s Biome Avg: %s%.1f\u00b0C%s Month: %s%.1f\u00b0C%s Daily: %s%.1f\u00b0C",
                        GRAY, WHITE, data.getBaseTemp(), GRAY,
                        WHITE, data.getAverageTemp(), GRAY,
                        WHITE, ClimateRenderHelper.get(blockpos).getTemperature(), GRAY,
                        WHITE, ClimateTFC.getHeightAdjustedTemp(mc.world, blockpos)));
                    String monthName = I18n.format(Helpers.getEnumName(CalendarTFC.getMonthOfYear()));
                    list.add(String.format("Year %04d, %s %02d %02d:%02d", CalendarTFC.getTotalYears(), monthName, CalendarTFC.getDayOfMonth(), CalendarTFC.getHourOfDay(), CalendarTFC.getMinuteOfHour()));

                    list.add(GRAY + "Biome: " + WHITE + mc.world.getBiome(blockpos).getBiomeName());

                    list.add(GRAY + "Rainfall: " + WHITE + data.getRainfall());
                    list.add(GRAY + "Flora Density: " + WHITE + data.getFloraDensity());
                    list.add(GRAY + "Flora Diversity: " + WHITE + data.getFloraDiversity());

                    list.add(GRAY + "Valid Trees: ");
                    data.getValidTrees().forEach(t -> list.add(String.format("%s %s (%.1f)", WHITE, t.getRegistryName(), t.getDominance())));

                    //list.add(GRAY + "Rocks: " + WHITE + data.getRockLayer1(x, z).name + ", " + data.getRockLayer2(x, z).name + ", " + data.getRockLayer3(x, z).name);
                    //list.add(GRAY + "Stability: " + WHITE + data.getStabilityLayer(x, z).name);
                    //list.add(GRAY + "Drainage: " + WHITE + data.getDrainageLayer(x, z).name);
                    list.add(GRAY + "Sea level offset: " + WHITE + data.getSeaLevelOffset(x, z));
                    //list.add(GRAY + "Fish population: " + WHITE + data.getFishPopulation());

                    //list.add("");
                    //list.add(GRAY + "Rock at feet: " + WHITE + data.getRockLayerHeight(x, blockpos.getY(), z).name);

                    // list.add("");
                    //data.getOresSpawned().stream().map(String::valueOf).forEach(list::add);
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty())
        {
            TerraFirmaCraft.getLog().warn("ItemTooltipEvent with empty stack??", new Exception());
            return;
        }
        Item item = stack.getItem();
        List<String> tt = event.getToolTip();

        // Stuff that should always be shown as part of the tooltip
        IItemSize size = CapabilityItemSize.getIItemSize(stack);
        if (size != null)
        {
            size.addSizeInfo(stack, tt);
        }
        IItemHeat heat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
        if (heat != null)
        {
            heat.addHeatInfo(stack, tt);
        }

        if (event.getFlags().isAdvanced()) // Only added with advanced tooltip mode
        {
            if (item instanceof IMetalObject)
            {
                ((IMetalObject) item).addMetalInfo(stack, tt);
            }
            else if (item instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof IMetalObject)
                {
                    ((IMetalObject) block).addMetalInfo(stack, tt);
                }
            }
            if (item instanceof IRockObject)
            {
                ((IRockObject) item).addRockInfo(stack, tt);
            }
            else if (item instanceof ItemBlock)
            {
                Block block = ((ItemBlock) item).getBlock();
                if (block instanceof IRockObject)
                {
                    ((IRockObject) block).addRockInfo(stack, tt);
                }
            }

            // todo: remove this debug tooltip
            if (stack.hasCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null))
            {
                IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
                assert cap != null;
                tt.add("Forge Stuff: " + cap.serializeNBT());
            }

            if (ConfigTFC.CLIENT.showToolClassTooltip)
            {
                Set<String> toolClasses = item.getToolClasses(stack);
                if (toolClasses.size() == 1)
                {
                    tt.add(I18n.format("tfc.tooltip.toolclass", toolClasses.iterator().next()));
                }
                else if (toolClasses.size() > 1)
                {
                    tt.add(I18n.format("tfc.tooltip.toolclasses"));
                    for (String toolClass : toolClasses)
                    {
                        tt.add("+ " + toolClass);
                    }
                }
            }
            if (ConfigTFC.CLIENT.showOreDictionaryTooltip)
            {
                int[] ids = OreDictionary.getOreIDs(stack);
                if (ids.length == 1)
                {
                    tt.add(I18n.format("tfc.tooltip.oredictionaryentry", OreDictionary.getOreName(ids[0])));
                }
                else if (ids.length > 1)
                {
                    tt.add(I18n.format("tfc.tooltip.oredictionaryentries"));
                    ArrayList<String> names = new ArrayList<>(ids.length);
                    for (int id : ids)
                    {
                        names.add("+ " + OreDictionary.getOreName(id));
                    }
                    names.sort(null); // Natural order (String.compare)
                    tt.addAll(names);
                }
            }
            if (ConfigTFC.CLIENT.showNBTTooltip)
            {
                if (stack.hasTagCompound())
                {
                    tt.add("NBT: " + stack.getTagCompound());
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void textureStitched(TextureStitchEvent.Post event)
    {
        FluidSpriteCache.clear();
    }
}
