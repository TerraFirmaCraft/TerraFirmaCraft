/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.util.Cache;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Client Config
 * - not synced, only loaded client side
 * - only use for PURELY AESTHETIC options
 */
public class ClientConfig extends CachingConfig
{
    public final Cache.Boolean ignoreExperimentalWorldGenWarning;
    public final Cache.Boolean assumeTFCWorld;

    public final Cache.Boolean enableHungerBar;
    public final Cache.Boolean enableHealthBar;
    public final Cache.Boolean enableThirstBar;
    public final Cache.Object<HealthDisplayFormat> healthDisplayFormat;


    ClientConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("general");

        ignoreExperimentalWorldGenWarning = wrap(builder.apply("ignoreExperimentalWorldGenWarning").comment("Should TFC forcefully skip the 'Experimental World Generation' warning screen when creating or loading a world?").define("ignoreExperimentalWorldGenWarning", true));

        assumeTFCWorld = wrap(builder.apply("assumeTFCWorld").comment(
            "This will assume in several places, that the world is a TFC world, and modify rendering appropriately. This affects the following changes:",
            "1. (Requires a world restart) Cloud height is moved from 160 -> 210",
            "2. The 'horizon height' (where the fog changes from sky to black) is moved from 63 -> 96"
        ).define("assumeTFCWorld", true));

        innerBuilder.pop().push("display");

        enableHungerBar = wrap(builder.apply("enableHungerBar").comment("Replace the vanilla hunger bar with a TFC one.").define("enableHungerBar", true));
        enableHealthBar = wrap(builder.apply("enableHealthBar").comment("Replaces the vanilla health bar with a TFC one.").define("enableHealthBar", true));
        enableThirstBar = wrap(builder.apply("enableThirstBar").comment("Adds a TFC thirst bar over the hotbar.").define("enableThirstBar", true));
        healthDisplayFormat = wrap(builder.apply("healthDisplayFormat").comment(
            "Health display format. This affects what number is displayed on top of the tfc-style health bar",
            "TFC - e.g. 750 / 1000",
            "VANILLA - e.g. 15.0 / 20.0",
            "TFC_CURRENT - e.g. 750",
            "VANILLA_CURRENT - e.g. 15.0"
        ).defineEnum("healthDisplayFormat", HealthDisplayFormat.TFC));

        innerBuilder.pop();
    }
}