/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.common.NeoForge;

import net.dries007.tfc.mixin.accessor.BiomeAccessor;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.events.SelectClimateModelEvent;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * Central location for all climate handling.
 * Models are assigned during world load with {@link SelectClimateModelEvent}
 *
 * This model is responsible for calculating the climate parameters (temperature, rainfall, precipitation, etc.) at given positions and locations.
 * All methods here are really bouncers to their relevant methods in {@link ClimateModel}, and are named the same. See them for documentation.
 *
 * @see ClimateModel
 */
public final class Climate
{
    public static float getTemperature(Level level, BlockPos pos, long calendarTick, int daysInMonth)
    {
        return model(level).getTemperature(level, pos, calendarTick, daysInMonth);
    }

    public static float getTemperature(Level level, BlockPos pos, ICalendar calendar, long calendarTick)
    {
        return model(level).getTemperature(level, pos, calendarTick, calendar.getCalendarDaysInMonth());
    }

    public static float getTemperature(Level level, BlockPos pos, ICalendar calendar)
    {
        return model(level).getTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static float getTemperature(Level level, BlockPos pos)
    {
        return getTemperature(level, pos, Calendars.get(level));
    }

    public static float getAverageTemperature(Level level, BlockPos pos)
    {
        return model(level).getAverageTemperature(level, pos);
    }

    public static float getAverageTempElevationAdjusted(Level level, BlockPos pos)
    {
        return model(level).getElevationAdjustedAverageTemperature(level, pos);
    }

    public static float getRainfall(Level level, BlockPos pos)
    {
        return model(level).getRainfall(level, pos);
    }

    public static float getRainVariance(Level level, BlockPos pos)
    {
        return model(level).getRainVariance(level, pos);
    }

    public static float getMonthlyRainfall(Level level, BlockPos pos, ICalendar calendar)
    {
        return model(level).getMonthlyRainfall(level, pos, calendar.getCalendarFractionOfYear());
    }

    public static float getMonthlyRainfall(Level level, BlockPos pos)
    {
        return getMonthlyRainfall(level, pos, Calendars.get(level));
    }

    public static float getBaseGroundwater(Level level, BlockPos pos)
    {
        return model(level).getBaseGroundwater(level, pos);
    }

    public static float getGroundwater(Level level, BlockPos pos)
    {
        return model(level).getGroundwater(level, pos);
    }

    public static float getMonthlyGroundwater(Level level, BlockPos pos, ICalendar calendar)
    {
        return model(level).getMonthlyGroundwater(level, pos, calendar.getCalendarFractionOfYear());
    }

    public static float getMonthlyGroundwater(Level level, BlockPos pos)
    {
        return getMonthlyGroundwater(level, pos, Calendars.get(level));
    }

    public static float getFogginess(Level level, BlockPos pos)
    {
        return model(level).getFogginess(level, pos, Calendars.get(level).getTicks());
    }

    public static float getWaterFogginess(Level level, BlockPos pos)
    {
        return model(level).getWaterFogginess(level, pos, Calendars.get(level).getTicks());
    }

    public static Vec2 getWindVector(Level level, BlockPos pos)
    {
        return model(level).getWindVector(level, pos, Calendars.get(level).getTicks());
    }

    public static Biome.Precipitation getPrecipitation(Level level, BlockPos pos)
    {
        return EnvironmentHelpers.isRainingOrSnowing(level, pos) ? Climate.warmEnoughToRain(level, pos) ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW : Biome.Precipitation.NONE;
    }

    /**
     * @see Biome#warmEnoughToRain(BlockPos)
     */
    public static boolean warmEnoughToRain(Level level, BlockPos pos)
    {
        return getVanillaBiomeTemperature(level, pos) >= 0.15f;
    }

    /**
     * Defensive version, when it's unknown if we're in world generation or not
     *
     * @see Biome#warmEnoughToRain(BlockPos)
     */
    public static boolean warmEnoughToRain(LevelReader level, BlockPos pos, Biome fallback)
    {
        return getVanillaBiomeTemperatureSafely(level, pos, fallback) >= 0.15f;
    }

    public static void onChunkLoad(WorldGenLevel level, ChunkAccess chunk, ChunkData chunkData)
    {
        model(level.getLevel()).onChunkLoad(level, chunk, chunkData);
    }

    public static void onWorldLoad(ServerLevel level)
    {
        final SelectClimateModelEvent event = new SelectClimateModelEvent(level);
        NeoForge.EVENT_BUS.post(event);
        WorldTracker.get(level).setClimateModel(event.getModel());
        model(level).onWorldLoad(level);
    }

    /**
     * Calculates the temperature, scaled to vanilla like values.
     * References: 0.15 ~ 0 C (freezing point of water). Vanilla typically ranges from -0.5 to +1 in the overworld.
     * This scales 0 C -> 0.15, -30 C -> -0.51, +30 C -> 0.801
     */
    public static float toVanillaTemperature(float actualTemperature)
    {
        return actualTemperature * 0.0217f + 0.15f;
    }

    /**
     * The inverse of {@link #toVanillaTemperature(float)}
     */
    public static float toActualTemperature(float vanillaTemperature)
    {
        return (vanillaTemperature - 0.15f) / 0.0217f;
    }

    public static float getVanillaBiomeTemperature(Level level, BlockPos pos)
    {
        return toVanillaTemperature(getTemperature(level, pos, Calendars.get(level)));
    }

    public static float getVanillaBiomeTemperatureSafely(LevelReader level, BlockPos pos, Biome fallback)
    {
        final Level unsafeLevel = Helpers.getUnsafeLevel(level);
        if (unsafeLevel != null)
        {
            final ICalendar calendar = Calendars.get(level);
            final ClimateModel model = model(unsafeLevel);
            return model.getTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
        }
        return ((BiomeAccessor) (Object) fallback).invoke$getTemperature(pos);
    }

    public static ClimateModel model(Level level)
    {
        return WorldTracker.get(level).getClimateModel();
    }
}
