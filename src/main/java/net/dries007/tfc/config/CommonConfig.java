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
    public final ForgeConfigSpec.BooleanValue enableDevTweaks;

    CommonConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        // Standardization for translation keys
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.general." + name);

        innerBuilder.push("general");

        defaultMonthLength = builder.apply("defaultMonthLength").defineInRange("defaultMonthLength", 8, 1, Integer.MAX_VALUE);
        enableDevTweaks = builder.apply("enableDevTweaks").comment(
            "This enables a series of quality of life logging improvements aimed at mod or pack development. It has no end user or gameplay effect.",
            "This currently enables the following tweaks:",
            " - Enables a [Possible DFU FU] log message, which attempts to catch errors due to incorrect world generation data packs. This does produce false errors!",
            " - Improves and shortens the error message for invalid loot tables.",
            " - Improves and shortens the error message for invalid recipes.",
            " - Fixes MC-190122 (Makes the 'Loaded Recipes' log message accurate)"
        ).define("enableDevTweaks", true);

        innerBuilder.pop();
    }
}