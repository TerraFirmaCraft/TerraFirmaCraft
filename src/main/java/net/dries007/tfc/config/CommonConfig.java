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
    // Defaults
    public final ForgeConfigSpec.IntValue defaultMonthLength;
    // World Generation - Caves
    // todo: move to config for worley caves when we replace it with a json world carver
    public final ForgeConfigSpec.IntValue worleyCaveHeightFade;
    public final ForgeConfigSpec.DoubleValue worleyCaveBaseNoiseCutoff;
    public final ForgeConfigSpec.DoubleValue worleyCaveWorleyNoiseCutoff;
    // todo: move to decorator for "exclusive y range"
    public final ForgeConfigSpec.IntValue caveSpikeMaxY;

    // General
    public final ForgeConfigSpec.BooleanValue logDFUFUs;

    CommonConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.general." + name);

        innerBuilder.push("worldGeneration").push("general");

        defaultMonthLength = builder.apply("defaultMonthLength").defineInRange("defaultMonthLength", 8, 1, Integer.MAX_VALUE);

        innerBuilder.pop().push("caves");

        worleyCaveHeightFade = builder.apply("worleyCaveHeightFade").defineInRange("worleyCaveHeightFade", 94, 0, 256);
        worleyCaveBaseNoiseCutoff = builder.apply("worleyCaveBaseNoiseCutoff").defineInRange("worleyCaveBaseNoiseCutoff", 0.3, 0, 1);
        worleyCaveWorleyNoiseCutoff = builder.apply("worleyCaveWorleyNoiseCutoff").defineInRange("worleyCaveWorleyNoiseCutoff", 0.38, 0, 1);
        caveSpikeMaxY = builder.apply("caveSpikeMaxY").defineInRange("caveSpikeMaxY", 60, 0, 255);

        innerBuilder.pop().pop().push("general");

        logDFUFUs = builder.apply("logDFUFUs").comment("Should TFC try and identify potential issues with world generation data loading and log informational warnings? This does produce false positives!").define("logDFUFUs", true);
    }
}