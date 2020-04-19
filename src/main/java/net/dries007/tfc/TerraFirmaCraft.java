/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.items.TFCItems;
import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.carver.TFCWorldCarvers;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;
import net.dries007.tfc.world.feature.TFCFeatures;

@Mod(TerraFirmaCraft.MOD_ID)
public final class TerraFirmaCraft
{
    public static final String MOD_ID = "tfc";
    public static final String MOD_NAME = "TerraFirmaCraft";

    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static TerraFirmaCraft INSTANCE;

    public static TFCWorldType getWorldType()
    {
        return INSTANCE.worldType;
    }

    /* Deprecated for now, because individual classes should use their own logger if possible / it makes sense */
    @Deprecated
    public static Logger getLog()
    {
        return LOGGER;
    }

    private final TFCWorldType worldType;

    public TerraFirmaCraft()
    {
        LOGGER.info("TFC Constructor");
        LOGGER.debug("Debug Logging Enabled");

        INSTANCE = this;

        worldType = new TFCWorldType();

        TFCConfig.register();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);

        TFCBlocks.BLOCKS.register(modEventBus);
        TFCItems.ITEMS.register(modEventBus);

        TFCWorldType.BIOME_PROVIDERS.register(modEventBus);
        TFCWorldType.CHUNK_GENERATORS.register(modEventBus);
        TFCBiomes.BIOMES.register(modEventBus);
        TFCFeatures.FEATURES.register(modEventBus);
        TFCWorldCarvers.CARVERS.register(modEventBus);

        PacketHandler.setup();
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event)
    {
        LOGGER.info("TFC Common Setup");

        ChunkDataCapability.setup();
        TFCBiomes.setup();
    }
}
