package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.network.ChunkDataPacket;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.types.TFCTypeManager;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

@Mod.EventBusSubscriber(modid = TerraFirmaCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeEventHandler
{
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void beforeServerStart(FMLServerAboutToStartEvent event)
    {
        LOGGER.debug("Before Server Start");

        // Initializes json data listeners
        TFCTypeManager.init(event.getServer().getResourceManager());
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("Common Setup");

        ChunkDataCapability.setup();
    }

    @SubscribeEvent
    public static void onChunkWatchWatch(ChunkWatchEvent.Watch event)
    {
        ChunkPos pos = event.getPos();
        if (pos != null)
        {
            ChunkData.get(event.getWorld().getChunk(pos.asBlockPos())).ifPresent(data -> {
                // Update server side climate cache
                ClimateTFC.update(pos, data.getRegionalTemp(), data.getRainfall());

                // Update client side data
                PacketHandler.get().send(PacketDistributor.PLAYER.with(event::getPlayer), new ChunkDataPacket(pos.x, pos.z, data));
            });
        }
    }
}
