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

    public static TFCWorldType WORLD_TYPE;

    /* Deprecated for now, because individual classes should use their own logger if possible / it makes sense */
    @Deprecated
    public static Logger getLog()
    {
        return LOGGER;
    }

    public TerraFirmaCraft()
    {
        LOGGER.info("TFC World Constructor");
        LOGGER.debug("Debug Logging Enabled");

        WORLD_TYPE = new TFCWorldType();
    }

    @SubscribeEvent
    public void setup(FMLCommonSetupEvent event)
    {
        ChunkDataCapability.setup();
    }
}
