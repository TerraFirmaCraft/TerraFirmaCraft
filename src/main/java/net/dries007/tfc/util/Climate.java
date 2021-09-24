/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.settings.ClimateSettings;

/**
 * Central class for all TFC climate requirements.
 * This is only valid in the overworld!
 */
public final class Climate
{
    /**
     * Constants for temperature calculation. Do not reference these directly, they do not have much meaning outside the context they are used in.
     */
    public static final float MINIMUM_TEMPERATURE_SCALE = -20f;
    public static final float MAXIMUM_TEMPERATURE_SCALE = 30f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE = -3f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_MEAN = 15f;
    public static final float REGIONAL_TEMPERATURE_SCALE = 2f;
    public static final float REGIONAL_RAINFALL_SCALE = 50f;

    /**
     * Magic numbers. These probably mean something
     */
    public static final float MINIMUM_RAINFALL = 0f;
    public static final float MAXIMUM_RAINFALL = 500f;

    public static final float SNOW_MELT_TEMPERATURE = 4f;
    public static final float SNOW_STACKING_TEMPERATURE = -4f;
    public static final float ICE_MELT_TEMPERATURE = -2f;
    public static final float SEA_ICE_FREEZE_TEMPERATURE = -6f;
    public static final float MIN_ICICLE_TEMPERATURE = -10f;
    public static final float MAX_ICICLE_TEMPERATURE = -2f;
    public static final float LAVA_LEVEL_TEMPERATURE = 15f;

    public static final float SEA_LEVEL = TFCChunkGenerator.SEA_LEVEL_Y;
    public static final float DEPTH_LEVEL = -64;

    @Nullable private static ClimateSettings overworldTemperatureSettings;

    public static ClimateSettings getOverworldTemperatureSettings()
    {
        return overworldTemperatureSettings != null ? overworldTemperatureSettings : ClimateSettings.DEFAULT_TEMPERATURE;
    }

    public static void setOverworldTemperatureSettings(ClimateSettings settings)
    {
        overworldTemperatureSettings = settings;
    }

    /**
     * Gets the equivalent to {@link Biome#getTemperature(BlockPos)} for TFC biomes.
     * Called from injected code.
     */
    public static float getVanillaBiomeTemperature(Biome biome, @Nullable LevelAccessor world, BlockPos pos)
    {
        if (world != null && TFCBiomes.getExtension(biome) != null)
        {
            return toVanillaTemperature(getTemperature(world, pos));
        }
        return biome.getTemperature(pos);
    }

    public static Biome.Precipitation getVanillaBiomePrecipitation(Biome biome, @Nullable LevelAccessor world, BlockPos pos)
    {
        if (world != null && TFCBiomes.getExtension(biome) != null)
        {
            return getPrecipitation(world, pos);
        }
        return biome.getPrecipitation();
    }

    /**
     * Used to calculate the actual temperature at a world and position.
     * Will be valid when used on both logical sides.
     * MUST NOT be used by world generation, it should use {@link Climate#calculateTemperature(BlockPos, float, Calendar)} instead, with the average temperature obtained through the correct chunk data source.
     */
    public static float getTemperature(LevelAccessor world, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(world, pos);
        final ICalendar calendar = Calendars.get(world);
        return calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static Biome.Precipitation getPrecipitation(LevelAccessor world, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(world, pos);
        final ICalendar calendar = Calendars.get(world);
        final float rainfall = data.getRainfall(pos);
        if (rainfall < 100)
        {
            return Biome.Precipitation.NONE;
        }
        final float temperature = calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
        return temperature < 0 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
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
     * The inverse of {@link Climate#toVanillaTemperature(float)}
     */
    public static float toActualTemperature(float vanillaTemperature)
    {
        return (vanillaTemperature - 0.15f) / 0.0217f;
    }

    /**
     * Calculates the average monthly temperature for a location and given month.
     */
    public static float calculateMonthlyAverageTemperature(int z, int y, float averageTemperature, float monthFactor)
    {
        final float monthlyTemperature = calculateMonthlyTemperature(z, monthFactor);
        return adjustTemperatureByElevation(y, averageTemperature, monthlyTemperature, 0);
    }

    /**
     * Calculates the exact temperature at a given location and time.
     */
    public static float calculateTemperature(BlockPos pos, float averageTemperature, Calendar calendar)
    {
        return calculateTemperature(pos.getZ(), pos.getY(), averageTemperature, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    /**
     * Calculates the exact temperature at a given location and time.
     */
    public static float calculateTemperature(int z, int y, float averageTemperature, long calendarTime, long daysInMonth)
    {
        // Month temperature
        final Month currentMonth = ICalendar.getMonthOfYear(calendarTime, daysInMonth);
        final float delta = ICalendar.getFractionOfMonth(calendarTime, daysInMonth);
        final float monthFactor = Mth.lerp(delta, currentMonth.getTemperatureModifier(), currentMonth.next().getTemperatureModifier());

        final float monthTemperature = calculateMonthlyTemperature(z, monthFactor);
        final float dailyTemperature = calculateDailyTemperature(calendarTime);

        return adjustTemperatureByElevation(y, averageTemperature, monthTemperature, dailyTemperature);
    }

    /**
     * Adjusts a series of temperature factors by elevation. Returns the sum temperature after adjustment.
     */
    private static float adjustTemperatureByElevation(int y, float averageTemperature, float monthTemperature, float dailyTemperature)
    {
        // Adjust temperature based on elevation
        // Above sea level, temperature lowers linearly with y.
        // Below sea level, temperature tends towards the average temperature for the area (having less influence from daily and monthly temperature)
        // Towards the bottom of the world, temperature tends towards a constant as per the existence of "lava level"
        if (y > SEA_LEVEL)
        {
            // -1.6 C / 10 blocks above sea level
            float elevationTemperature = Mth.clamp((y - SEA_LEVEL) * 0.16225f, 0, 17.822f);
            return averageTemperature + monthTemperature - elevationTemperature + dailyTemperature;
        }
        else if (y > 0)
        {
            // The influence of daily and monthly temperature is reduced as depth increases
            float monthInfluence = Helpers.inverseLerp(y, 0, SEA_LEVEL);
            float dailyInfluence = Mth.clamp(monthInfluence * 3f - 2f, 0, 1); // Range 0 - 1, decays faster than month influence
            return averageTemperature + Mth.lerp(monthInfluence, (float) 0, monthTemperature) + Mth.lerp(dailyInfluence, (float) 0, dailyTemperature);
        }
        else
        {
            // At y = 0, there will be no influence from either month or daily temperature
            // Between this and the bottom of the world, linearly scale average temperature towards depth temperature
            float depthInfluence = Helpers.inverseLerp(y, DEPTH_LEVEL, 0);
            return Mth.lerp(depthInfluence, LAVA_LEVEL_TEMPERATURE, averageTemperature);
        }
    }

    /**
     * Calculates the monthly temperature for a given latitude and month modifier
     */
    private static float calculateMonthlyTemperature(int z, float monthTemperatureModifier)
    {
        return monthTemperatureModifier * Helpers.triangle(LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE, LATITUDE_TEMPERATURE_VARIANCE_MEAN, 1f / (4f * getOverworldTemperatureSettings().scale()), z);
    }

    /**
     * Calculates the daily variation temperature at a given time.
     * Influenced by both random variation day by day, and the time of day.
     * Range: -3.9 - 3.9
     */
    private static float calculateDailyTemperature(long calendarTime)
    {
        // Hottest part of the day at 12, coldest at 0
        int hourOfDay = ICalendar.getHourOfDay(calendarTime);
        if (hourOfDay > 12)
        {
            // Range: 0 - 12
            hourOfDay = 24 - hourOfDay;
        }
        // Range: -1 - 1
        float hourModifier = (hourOfDay / 6f) - 1f;

        // Note: this does not use world seed, as that is not synced from server - client, resulting in the seed being different
        long day = ICalendar.getTotalDays(calendarTime);
        final Random random = new Random(day); // avoid thread corruption by using a random local to the method rather than a static instance
        return ((random.nextFloat() - random.nextFloat()) + 0.3f * hourModifier) * 3f;
    }

    private Climate() {}
}