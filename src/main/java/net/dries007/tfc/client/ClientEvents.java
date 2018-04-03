package net.dries007.tfc.client;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.classic.capabilities.ChunkDataProvider;
import net.dries007.tfc.world.classic.capabilities.ChunkDataTFC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

import static net.dries007.tfc.Constants.MOD_ID;

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
        // todo: sync data
        // todo: check if this is allowed to be displayed, debug only maybe?
        Minecraft mc = Minecraft.getMinecraft();
        List<String> list = event.getRight();
        if (ConfigTFC.GENERAL.debug && mc.gameSettings.showDebugInfo)
        {
            list.add("");
            list.add("TerraFirmaCraft World Data:");

            BlockPos blockpos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
            Chunk chunk = mc.world.getChunkFromBlockCoords(blockpos);
            if (mc.world.isBlockLoaded(blockpos) && !chunk.isEmpty())
            {
                list.add(mc.world.getBiome(blockpos).getBiomeName());

                final int x = blockpos.getX() & 15, z = blockpos.getZ() & 15;
                ChunkDataTFC data = chunk.getCapability(ChunkDataProvider.CHUNK_DATA_CAPABILITY, null);

                if (data == null || !data.isInitialized()) list.add("No data ?!");
                else
                {
                    list.add("Rock 1: " + data.getRockLayer1(x, z).name);
                    list.add("Rock 2: " + data.getRockLayer2(x, z).name);
                    list.add("Rock 3: " + data.getRockLayer3(x, z).name);
                    list.add("EVT: " + data.getEvtLayer(x, z).name);
                    list.add("Rainfall: " + data.getRainfallLayer(x, z).name);
                    list.add("Stability: " + data.getStabilityLayer(x, z).name);
                    list.add("Drainage: " + data.getDrainageLayer(x, z).name);
                    list.add("Sea level offset: " + data.getSeaLevelOffset(x, z));
                }
            }
        }
    }
}
