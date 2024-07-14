/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

/**
 * Common Config
 * - not synced, saved per instance
 * - use for things that are only important server side (i.e. world gen), or make less sense to have per-world.
 */
public class CommonConfig extends BaseConfig
{
    // General
    public final ConfigValue<String> defaultWorldPreset;

    // Calendar
    public final IntValue defaultMonthLength;
    public final IntValue defaultCalendarStartDay;

    // Debug
    public final BooleanValue enableDatapackTests;

    CommonConfig(ConfigBuilder builder)
    {
        builder.push("general");

        defaultWorldPreset = builder.comment(
            "If the TFC world preset 'tfc:overworld' should be set as the default world generation when creating a new world."
        ).define("defaultWorldPreset", "tfc:overworld");

        builder.swap("calendar");

        defaultMonthLength = builder.comment(
            "The number of days in a month, for newly created worlds.",
            "This can be modified in existing worlds using the /time command"
        ).define("defaultMonthLength", 8, 1, Integer.MAX_VALUE);
        defaultCalendarStartDay = builder.comment(
            "The start date for newly created worlds, in a number of ticks, for newly created worlds",
            "This represents a number of days offset from January 1, 1000",
            "The default is (5 * daysInMonth) = 40, which starts at June 1, 1000 (with the default daysInMonth = 8)"
        ).define("defaultCalendarStartDay", (5 * 8), -1, Integer.MAX_VALUE);

        builder.swap("debug");

        enableDatapackTests = builder.comment("If enabled, TFC will validate that certain pieces of reloadable data fit the conditions we expect, for example heating recipes having heatable items. It will error or warn in the log if these conditions are not met.").define("enableDatapackTests", !FMLEnvironment.production);
    }
}