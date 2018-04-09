package net.dries007.tfc.client;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.render.RenderFallingBlockTFC;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.minecraft.util.text.TextFormatting.*;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = MOD_ID)
public class ClientEvents
{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void openGUI(GuiScreenEvent.InitGuiEvent.Pre event)
    {
        if (ConfigTFC.CLIENT.makeWorldTypeClassicDefault && event.getGui() instanceof GuiCreateWorld)
        {
            GuiCreateWorld gui = ((GuiCreateWorld) event.getGui());
            // Only change if default is selected, because coming back from customisation, this will be set already.
            if (gui.selectedIndex == WorldType.DEFAULT.getId()) gui.selectedIndex = TerraFirmaCraft.getWorldTypeTFC().getId();
        }
    }

    @SubscribeEvent
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
                    list.add(GRAY + "Rock at feet: " + WHITE + data.getRockHeight(x, blockpos.getY(), z));

                    list.add("");
                    data.getOresSpawned().stream().map(String::valueOf).forEach(list::add);
                }
            }
        }
    }

    public static void preInit()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityFallingBlockTFC.class, RenderFallingBlockTFC::new);
    }
}
