/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Client Config
 * - not synced, only loaded client side
 * - only use for PURELY AESTHETIC options
 */
public class ClientConfig
{
    public final ForgeConfigSpec.BooleanValue ignoreExperimentalWorldGenWarning;
    public final ForgeConfigSpec.BooleanValue assumeTFCWorld;

    ClientConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        ignoreExperimentalWorldGenWarning = builder.apply("ignoreExperimentalWorldGenWarning").comment("Should TFC forcefully skip the 'Experimental World Generation' warning screen when creating or loading a world?").define("ignoreExperimentalWorldGenWarning", true);

        assumeTFCWorld = builder.apply("assumeTFCWorld").comment(
            "This will assume in several places, that the world is a TFC world, and modify rendering appropriately. This affects the following changes:",
            "1. (Requires a world restart) Cloud height is moved from 160 -> 210",
            "2. The 'horizon height' (where the fog changes from sky to black) is moved from 63 -> 96"
        ).define("assumeTFCWorld", true);
    }
}