/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.world.settings.ClimateSettings;

/**
 * Represents a model of the climate for a dimension.
 */
public interface ClimateModel
{
    float MINIMUM_RAINFALL = 0f;
    float MAXIMUM_RAINFALL = 500f;

    /**
     * Get the temperature at a given position, and timestamp.
     *
     * @return A temperature, in degrees Celsius. Typically in the range [-40, 40]
     */
    float getTemperature(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth);

    /**
     * Get the average annual temperature for a given position.
     *
     * @return A temperature, in degrees Celsius. Typically in the range [-25, 25]
     */
    float getAverageTemperature(LevelReader level, BlockPos pos);

    /**
     * Get the precipitation type for a given position, and the current time (Can obtain a timestamp via {@code Calendars.get(level)}).
     *
     * @return A precipitation.
     */
    Biome.Precipitation getPrecipitation(LevelReader level, BlockPos pos);

    /**
     * Get the average annual rainfall for a given position.
     * Should be <strong>time invariant</strong>.
     *
     * @return The average annual rainfall, roughly equivalent to mm/year. Should be in the range [0, 500]
     */
    float getRainfall(LevelReader level, BlockPos pos);

    /**
     * Provides a {@link ClimateSettings} to models that may use them, when a world loads.
     */
    default void updateCachedTemperatureSettings(ClimateSettings settings) {}
}
