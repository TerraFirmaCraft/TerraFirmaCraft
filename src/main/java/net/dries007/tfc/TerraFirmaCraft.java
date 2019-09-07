/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import net.dries007.tfc.world.TFCWorldType;
import net.dries007.tfc.world.chunkdata.ChunkDataCapability;

@Mod(TerraFirmaCraft.MOD_ID)
public class TerraFirmaCraft
{
    public static final String MOD_ID = "tfc";

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

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event)
    {
        ChunkDataCapability.setup();
    }

    public TerraFirmaCraft()
    {
        LOGGER.info("TFC Constructor");
        LOGGER.debug("Debug Logging Enabled");

        INSTANCE = this;
        worldType = new TFCWorldType();
    }
}
