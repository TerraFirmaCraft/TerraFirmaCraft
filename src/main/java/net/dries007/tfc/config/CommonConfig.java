/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Supplier;
import net.neoforged.fml.loading.FMLEnvironment;

import net.dries007.tfc.util.calendar.Calendar;

/**
 * Common Config
 * - not synced, saved per instance
 * - use for things that are only important server side (i.e. world gen), or make less sense to have per-world.
 */
public class CommonConfig extends BaseConfig
{
    // General
    public final Supplier<String> defaultWorldPreset;

    // Calendar
    public final Supplier<Integer> defaultMonthLength;
    public final Supplier<Integer> defaultCalendarStartDay;
    public final Supplier<Integer> defaultCalendarDayLength;

    // Debug
    public final Supplier<Boolean> enableDatapackTests;

    CommonConfig(ConfigBuilder builder)
    {
        builder.push("general");

        defaultWorldPreset = builder.comment(
            "If the TFC world preset 'tfc:overworld' should be set as the default world generation when creating a new world."
        ).define("defaultWorldPreset", "tfc:overworld");

        builder.swap("calendar");

        defaultMonthLength = builder.comment(
            "The number of days in a month, for newly created worlds.",
            "",
            "This can be modified in existing worlds using the /time command"
        ).define("defaultMonthLength", Calendar.DEFAULT_MONTH_LENGTH, 1, Integer.MAX_VALUE);
        defaultCalendarStartDay = builder.comment(
            "The start date for newly created worlds, in a number of ticks, for newly created worlds",
            "This represents a number of days offset from January 1, 1000",
            "The default is (5 * daysInMonth) = 40, which starts at June 1, 1000 (with the default daysInMonth = 8)"
        ).define("defaultCalendarStartDay", (5 * 8), -1, Integer.MAX_VALUE);
        defaultCalendarDayLength = builder.comment(
            "The month length (in minutes) for newly created worlds",
            "The default (in TFC) is 24, which at 20 TPS makes one in-game hour equal to exactly one minute of real time.",
            "",
            "This can be modified in existing worlds using the /time command"
        ).define("defaultCalendarDayLength", 24);

        builder.swap("debug");

        enableDatapackTests = builder.comment("If enabled, TFC will validate that certain pieces of reloadable data fit the conditions we expect, for example heating recipes having heatable items. It will error or warn in the log if these conditions are not met.").define("enableDatapackTests", !FMLEnvironment.production);
    }
}