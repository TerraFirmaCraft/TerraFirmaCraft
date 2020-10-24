/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.config;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Common Config
 * - not synced, saved per instance
 * - use for things that are only important server side (i.e. world gen), or make less sense to have per-world.
 */
public class CommonConfig
{
    // General
    public final ForgeConfigSpec.IntValue defaultMonthLength;
    public final ForgeConfigSpec.BooleanValue logDFUFUs;

    CommonConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.general." + name);

        innerBuilder.push("general");

        defaultMonthLength = builder.apply("defaultMonthLength").defineInRange("defaultMonthLength", 8, 1, Integer.MAX_VALUE);
        logDFUFUs = builder.apply("logDFUFUs").comment("Should TFC try and identify potential issues with world generation data loading and log informational warnings? This does produce false positives!").define("logDFUFUs", true);

        innerBuilder.pop();
    }
}