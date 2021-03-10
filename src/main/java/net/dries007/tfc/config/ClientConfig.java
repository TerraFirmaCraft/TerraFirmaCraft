/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.util.Cache;
import net.dries007.tfc.util.config.HealthDisplayFormat;

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

    public final Cache.Boolean useVanillaHunger;
    public final Cache.Boolean useVanillaHealth;
    public final Cache.Boolean hideThirstBar;
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

        useVanillaHunger = wrap(builder.apply("useVanillaHunger").comment("Render vanilla hunger over the hotbar?").define("useVanillaHunger", false));
        useVanillaHealth = wrap(builder.apply("useVanillaHunger").comment("Render vanilla health bar over the hotbar?").define("useVanillaHealth", false));
        hideThirstBar = wrap(builder.apply("hideThirstBar").comment("Hide the TFC thirst bar?").define("hideThirstBar", false));
        healthDisplayFormat = wrap(builder.apply("healthDisplayFormat").comment("Health display format. Options: TFC, VANILLA, TFC_CURRENT_HEALTH, VANILLA_CURRENT_HEALTH").define("healthDisplayFormat", HealthDisplayFormat.VANILLA));

        innerBuilder.pop();
    }
}