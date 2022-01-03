/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.settings.ClimateSettings;

/**
 * The climate model for TFC's overworld. Provides a number of mechanics including:
 * - seasonal, monthly, and daily temperature variance.
 * - altitude based temperature, including both above and below ground effects.
 * - time varying precipitation types.
 *
 * This class can be used directly via static methods <strong>only if</strong> it is used through world generation, and the call site can assume that it is in the overworld.
 */
public class OverworldClimateModel implements WorldGenClimateModel
{
    public static final float SNOW_MELT_TEMPERATURE = 4f;
    public static final float SNOW_STACKING_TEMPERATURE = -4f;
    public static final float ICE_MELT_TEMPERATURE = -2f;
    public static final float SEA_ICE_FREEZE_TEMPERATURE = -6f;
    public static final float MIN_ICICLE_TEMPERATURE = -10f;
    public static final float MAX_ICICLE_TEMPERATURE = -2f;
    public static final float LAVA_LEVEL_TEMPERATURE = 15f;

    /**
     * Constants for temperature calculation. Do not reference these directly, they do not have much meaning outside the context they are used in.
     */
    public static final float MINIMUM_TEMPERATURE_SCALE = -20f;
    public static final float MAXIMUM_TEMPERATURE_SCALE = 30f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE = -3f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_MEAN = 15f;
    public static final float REGIONAL_TEMPERATURE_SCALE = 2f;
    public static final float REGIONAL_RAINFALL_SCALE = 50f;

    public static final float SEA_LEVEL = TFCChunkGenerator.SEA_LEVEL_Y;
    public static final float DEPTH_LEVEL = -64;

    public static final int FOGGY_DAY_RARITY = 10;
    public static final float FOGGY_RAINFALL_MINIMUM = 150f;
    public static final float FOGGY_RAINFALL_PEAK = 300f;


    static final OverworldClimateModel INSTANCE = new OverworldClimateModel();

    /**
     * Calculates the average monthly temperature for a location and given month.
     */
    public static float getAverageMonthlyTemperature(int z, int y, float averageTemperature, float monthFactor)
    {
        final float monthlyTemperature = INSTANCE.calculateMonthlyTemperature(z, monthFactor);
        return INSTANCE.adjustTemperatureByElevation(y, averageTemperature, monthlyTemperature, 0);
    }

    /**
     * Calculates the exact temperature at a given location and time.
     */
    public static float getTemperature(BlockPos pos, ChunkData data, Calendar calendar)
    {
        return INSTANCE.getTemperature(null, pos, data, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    private ClimateSettings temperatureSettings = ClimateSettings.DEFAULT_TEMPERATURE;

    @Override
    public float getTemperature(@Nullable LevelReader level, BlockPos pos, ChunkData data, long calendarTicks, int daysInMonth)
    {
        // Month temperature
        final Month currentMonth = ICalendar.getMonthOfYear(calendarTicks, daysInMonth);
        final float delta = ICalendar.getFractionOfMonth(calendarTicks, daysInMonth);
        final float monthFactor = Mth.lerp(delta, currentMonth.getTemperatureModifier(), currentMonth.next().getTemperatureModifier());

        final float monthTemperature = calculateMonthlyTemperature(pos.getZ(), monthFactor);
        final float dailyTemperature = calculateDailyTemperature(calendarTicks);

        return adjustTemperatureByElevation(pos.getY(), data.getAverageTemp(pos), monthTemperature, dailyTemperature);
    }

    @Override
    public float getAverageTemperature(LevelReader level, BlockPos pos)
    {
        return ChunkData.get(level, pos).getAverageTemp(pos);
    }

    @Override
    public float getRainfall(LevelReader level, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(level, pos);
        return data.getRainfall(pos);
    }

    @Override
    public Biome.Precipitation getPrecipitation(LevelReader level, BlockPos pos)
    {
        final float rainfall = getRainfall(level, pos);
        if (rainfall < 100)
        {
            return Biome.Precipitation.NONE;
        }
        final ICalendar calendar = Calendars.get(level);
        final float temperature = getTemperature(level, pos, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
        return temperature < 0 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
    }

    @Override
    public float getFogginess(LevelReader level, BlockPos pos, long calendarTime)
    {
        // seed as if we're 2 hours in the future, in order to start the cycle at 4am (2 hours before sunrise)
        final long day = ICalendar.getTotalDays(calendarTime + (2 * ICalendar.TICKS_IN_HOUR));
        final Random random = new Random(day); // todo: variation per world somehow?
        if (random.nextInt(FOGGY_DAY_RARITY) != 0)
        {
            return 0;
        }

        final float fogModifier = random.nextFloat(); // untransformed value of the fog

        final long dayTime = Calendars.get(level).getCalendarDayTime();
        float scaledTime; // a value between 0 and 1
        if (dayTime > 22000) // 4am to 6am
        {
            scaledTime = Mth.map(dayTime, 22000, 24000, 0, 1);
        }
        else if (dayTime >= 0 && dayTime < 4000) // 6am to 10am
        {
            scaledTime = 1;
        }
        else if (dayTime >= 4000 && dayTime < 6000) // 10am to 12pm
        {
            scaledTime = 1 - Mth.map(dayTime, 4000, 6000, 0, 1);
        }
        else // 12pm to 4am
        {
            scaledTime = 0;
        }

        final float rainfall = getRainfall(level, pos);
        final float rainfallModifier = Mth.clampedMap(rainfall, FOGGY_RAINFALL_MINIMUM, FOGGY_RAINFALL_PEAK, 0, 1);

        return Helpers.easeInOutCubic(scaledTime) * fogModifier * rainfallModifier;
    }

    @Override
    public void updateCachedTemperatureSettings(ClimateSettings settings)
    {
        temperatureSettings = settings;
    }

    /**
     * Adjusts a series of temperature factors by elevation. Returns the sum temperature after adjustment.
     */
    private float adjustTemperatureByElevation(int y, float averageTemperature, float monthTemperature, float dailyTemperature)
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
    private float calculateMonthlyTemperature(int z, float monthTemperatureModifier)
    {
        return monthTemperatureModifier * Helpers.triangle(LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE, LATITUDE_TEMPERATURE_VARIANCE_MEAN, 1f / (4f * temperatureSettings.scale()), z);
    }

    /**
     * Calculates the daily variation temperature at a given time.
     * Influenced by both random variation day by day, and the time of day.
     * Range: -3.9 - 3.9
     */
    private float calculateDailyTemperature(long calendarTime)
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
}
