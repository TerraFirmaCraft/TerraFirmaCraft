package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import net.dries007.tfc.types.TFCTypeManager;
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
}
