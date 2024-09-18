/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.events.SelectClimateModelEvent;
import net.dries007.tfc.util.tracker.WorldTracker;

/**
 * Provides access on a per-world basis to the underlying {@link ClimateModel}, along with additional utility methods for interacting
 * with the climate. Most of the methods here are simply bouncers to the methods on climate model, through the model for the given level.
 *
 * @see ClimateModel
 */
public final class Climate
{
    /**
     * @return The climate model for the provided {@code level}.
     */
    public static ClimateModel get(Level level)
    {
        return WorldTracker.get(level).getClimateModel();
    }

    /**
     * Selects and initializes the climate model for the world.
     */
    public static void chooseModelForWorld(ServerLevel level)
    {
        final SelectClimateModelEvent event = new SelectClimateModelEvent(level);
        NeoForge.EVENT_BUS.post(event);
        WorldTracker.get(level).setClimateModel(event.getModel());
    }

    public static float getTemperature(Level level, BlockPos pos, long calendarTick, int daysInMonth)
    {
        return get(level).getTemperature(level, pos, calendarTick, daysInMonth);
    }

    public static float getTemperature(Level level, BlockPos pos, ICalendar calendar, long calendarTick)
    {
        return get(level).getTemperature(level, pos, calendarTick, calendar.getCalendarDaysInMonth());
    }

    public static float getTemperature(Level level, BlockPos pos, ICalendar calendar)
    {
        return get(level).getTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static float getTemperature(Level level, BlockPos pos)
    {
        return getTemperature(level, pos, Calendars.get(level));
    }

    public static float getAverageTemperature(Level level, BlockPos pos)
    {
        return get(level).getAverageTemperature(level, pos);
    }

    public static float getRainfall(Level level, BlockPos pos)
    {
        return get(level).getAverageRainfall(level, pos);
    }

    public static float getGroundwater(Level level, BlockPos pos)
    {
        return get(level).getAverageGroundwater(level, pos);
    }

    /**
     * Converts a vanilla biome temperature into a TFC temperature in degrees Celsius.
     * <p>
     * Vanilla temperature assigns 0.15 as the freezing point of water (0C), and typically ranges from -0.5 to +1 in the overworld. We infer
     * this to mean O C -> 0.15, -30 C -> -0.51, and +30C -> 0.801
     */
    public static float fromVanilla(float vanillaTemperature)
    {
        return (vanillaTemperature - 0.15f) / 0.0217f;
    }
}
