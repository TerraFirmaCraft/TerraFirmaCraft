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
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiButtonRecipe;
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
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.client.button.GuiButtonPlayerInventoryTab;
import net.dries007.tfc.client.render.RenderFallingBlockTFC;
import net.dries007.tfc.client.render.animal.*;
import net.dries007.tfc.client.render.projectile.RenderThrownJavelin;
import net.dries007.tfc.network.PacketSwitchPlayerInventoryTab;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.objects.entity.animal.*;
import net.dries007.tfc.objects.entity.projectile.EntityThrownJavelin;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateHelper;
import net.dries007.tfc.util.climate.ClimateTFC;
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
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownJavelin.class, RenderThrownJavelin::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySheepTFC.class, RenderSheepTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCowTFC.class, RenderCowTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBearTFC.class, RenderBearTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityChickenTFC.class, RenderChickenTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPheasantTFC.class, RenderPheasantTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDeerTFC.class, RenderDeerTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityPigTFC.class, RenderPigTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityWolfTFC.class, RenderWolfTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityRabbitTFC.class, RenderRabbitTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityHorseTFC.class, RenderHorseTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDonkeyTFC.class, RenderAbstractHorseTFC::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMuleTFC.class, RenderAbstractHorseTFC::new);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onInitGuiPre(GuiScreenEvent.InitGuiEvent.Pre event)
    {
        if (ConfigTFC.CLIENT.makeWorldTypeClassicDefault && event.getGui() instanceof GuiCreateWorld)
        {
            GuiCreateWorld gui = ((GuiCreateWorld) event.getGui());
            // Only change if default is selected, because coming back from customisation, this will be set already.
            if (gui.selectedIndex == WorldType.DEFAULT.getId())
            {
                gui.selectedIndex = TerraFirmaCraft.getWorldTypeTFC().getId();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory)
        {
            int buttonId = event.getButtonList().size();
            int guiLeft = ((GuiInventory) event.getGui()).getGuiLeft();
            int guiTop = ((GuiInventory) event.getGui()).getGuiTop();

            event.getButtonList().add(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.INVENTORY, guiLeft, guiTop, ++buttonId, false));
            event.getButtonList().add(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.SKILLS, guiLeft, guiTop, ++buttonId, true));
            event.getButtonList().add(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.CALENDAR, guiLeft, guiTop, ++buttonId, true));
            event.getButtonList().add(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.NUTRITION, guiLeft, guiTop, ++buttonId, true));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onGuiButtonPress(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        if (event.getGui() instanceof GuiInventory && event.getButton() instanceof GuiButtonPlayerInventoryTab)
        {
            // This should generally be true, but check just in case something has disabled it
            GuiButtonPlayerInventoryTab button = (GuiButtonPlayerInventoryTab) event.getButton();
            if (button.isActive())
            {
                TerraFirmaCraft.getNetwork().sendToServer(new PacketSwitchPlayerInventoryTab(button.getGuiType()));
            }
        }
        else if (event.getGui() instanceof GuiInventory && event.getButton() instanceof GuiButtonRecipe)
        {
            // This is necessary to catch the resizing of the inventory gui when you open the recipe book
            for (GuiButton button : event.getButtonList())
            {
                if (button instanceof GuiButtonPlayerInventoryTab)
                {
                    ((GuiButtonPlayerInventoryTab) button).updateGuiLeft(((GuiInventory) event.getGui()).getGuiLeft());
                }
            }
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
                    list.add(String.format("%sRegion: %s%.1f\u00b0C%s Avg: %s%.1f\u00b0C%s Min: %s%.1f\u00b0C%s Max: %s%.1f\u00b0C",
                        GRAY, WHITE, data.getRegionalTemp(), GRAY,
                        WHITE, data.getAverageTemp(), GRAY,
                        WHITE, ClimateHelper.monthFactor(data.getRegionalTemp(), Month.JANUARY.getTemperatureModifier(), blockpos.getZ()), GRAY,
                        WHITE, ClimateHelper.monthFactor(data.getRegionalTemp(), Month.JULY.getTemperatureModifier(), blockpos.getZ())));
                    list.add(String.format("%sTemperature: %s%.1f\u00b0C Daily: %s%.1f\u00b0C",
                        GRAY, WHITE, ClimateTFC.getMonthlyTemp(blockpos),
                        WHITE, ClimateTFC.getActualTemp(blockpos)));

                    list.add(I18n.format("tfc.tooltip.date", CalendarTFC.CALENDAR_TIME.getTimeAndDate()));
                    list.add(I18n.format("tfc.tooltip.debug_times", CalendarTFC.TOTAL_TIME.getTicks(), CalendarTFC.PLAYER_TIME.getTicks(), CalendarTFC.CALENDAR_TIME.getTicks()));

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
        Item item = stack.getItem();
        List<String> tt = event.getToolTip();
        if (!stack.isEmpty())
        {
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
            IFood nutrients = stack.getCapability(CapabilityFood.CAPABILITY, null);
            if (nutrients != null)
            {
                nutrients.addNutrientInfo(stack, tt);
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
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void textureStitched(TextureStitchEvent.Post event)
    {
        FluidSpriteCache.clear();
    }
}
