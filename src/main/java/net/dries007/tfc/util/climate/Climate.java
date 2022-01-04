/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.mixin.accessor.BiomeAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.settings.ClimateSettings;

/**
 * Central location for all climate handling.
 * Each dimension provides a climate model. Addons or other mods wishing to register their dimensions may do so via {@link #register(ResourceKey, ClimateModel)}.
 * This model is responsible for calculating the climate parameters (temperature, rainfall, precipitation, etc.) at given positions and locations.
 * TFC hooks all vanilla based locations to go through this. If no climate model is registered, it will fallback to the {@link #DEFAULT} model, which uses biome based properties instead.
 * All methods here are really bouncers to their relevant methods in {@link ClimateModel}, and are named the same. See them for documentation.
 *
 * @see ClimateModel
 */
public final class Climate
{
    private static final Map<ResourceKey<Level>, ClimateModel> DIMENSIONS = new IdentityHashMap<>();
    private static final WorldGenClimateModel DEFAULT = new BiomeBasedClimateModel();

    static
    {
        register(Level.OVERWORLD, OverworldClimateModel.INSTANCE);
    }

    /**
     * Register a climate model for a dimension.
     * This method is safe to call during parallel mod loading.
     */
    public static synchronized void register(ResourceKey<Level> dimension, ClimateModel model)
    {
        DIMENSIONS.put(dimension, model);
    }

    public static float getAverageTemperature(Level level, BlockPos pos)
    {
        return model(level).getAverageTemperature(level, pos);
    }

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

    public static float getRainfall(Level level, BlockPos pos)
    {
        return model(level).getRainfall(level, pos);
    }

    public static Biome.Precipitation getPrecipitation(Level level, BlockPos pos)
    {
        return model(level).getPrecipitation(level, pos);
    }

    public static float getFogginess(Level level, BlockPos pos)
    {
        return model(level).getFogginess(level, pos, Calendars.get(level).getTicks());
    }

    /**
     * @see Biome#coldEnoughToSnow(BlockPos)
     */
    public static boolean coldEnoughToSnow(Level level, BlockPos pos)
    {
        return !warmEnoughToRain(level, pos);
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
     * @see Biome#coldEnoughToSnow(BlockPos)
     */
    public static boolean coldEnoughToSnow(LevelReader level, BlockPos pos, Biome fallback)
    {
        return !warmEnoughToRain(level, pos, fallback);
    }

    /**
     * Defensive version, when it's unknown if we're in world generation or not
     * @see Biome#warmEnoughToRain(BlockPos)
     */
    public static boolean warmEnoughToRain(LevelReader level, BlockPos pos, Biome fallback)
    {
        return getVanillaBiomeTemperatureSafely(level, pos, fallback) >= 0.15f;
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

    public static float getVanillaBiomeTemperatureSafely(LevelReader maybeLevel, BlockPos pos, Biome fallback)
    {
        final Level unsafeLevel = Helpers.getUnsafeLevel(maybeLevel);
        if (unsafeLevel != null)
        {
            final ICalendar calendar = Calendars.get(maybeLevel);
            final ClimateModel model = model(unsafeLevel);
            if (maybeLevel instanceof WorldGenRegion worldGenLevel)
            {
                // World generation. If the model supports direct calls, then find the correct chunk data and pass it in
                if (model instanceof WorldGenClimateModel worldGenModel)
                {
                    final ChunkData data = ChunkDataProvider.get(worldGenLevel).get(worldGenLevel.getChunk(pos));
                    return toVanillaTemperature(worldGenModel.getTemperature(worldGenLevel, pos, data, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth()));
                }
            }
            else
            {
                // Pretty sure we're not in world generation, so we can call the model directly.
                return toVanillaTemperature(model.getTemperature(maybeLevel, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth()));
            }
        }
        return ((BiomeAccessor) (Object) fallback).invoke$getTemperature(pos);
    }

    /**
     * Update the per-dimension temperature settings when a world loads.
     */
    public static void onWorldLoad(Level level, ClimateSettings settings)
    {
        model(level).updateCachedTemperatureSettings(settings);
    }

    private static ClimateModel model(Level level)
    {
        return DIMENSIONS.getOrDefault(level.dimension(), DEFAULT);
    }
}
