/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.noise.NoiseUtil;

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

    public static final float SEA_LEVEL = TFCChunkGenerator.SEA_LEVEL;
    public static final float DEPTH_LEVEL = SEA_LEVEL * 2 / 3;

    private static final Random RANDOM = new Random(); // Used for daily temperature variations

    /**
     * Gets the equivalent to {@link Biome#getTemperature(BlockPos)} for TFC biomes.
     * Called from injected code.
     */
    public static float getVanillaBiomeTemperature(Biome biome, @Nullable IWorld world, BlockPos pos)
    {
        if (world != null && TFCBiomes.getExtension(biome) != null)
        {
            return toVanillaTemperature(getTemperature(world, pos));
        }
        return biome.getTemperature(pos);
    }

    public static Biome.RainType getVanillaBiomePrecipitation(Biome biome, @Nullable IWorld world, BlockPos pos)
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
     * MUST NOT be used by world generation, it should use {@link Climate#calculateTemperature(BlockPos, float, Calendar)} instead, with the average temperature obtained through the correct chunk data source
     */
    public static float getTemperature(IWorld world, BlockPos pos)
    {
        ChunkData data = ChunkData.get(world, pos);
        ICalendar calendar = Calendars.get(world);
        return calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static Biome.RainType getPrecipitation(IWorld world, BlockPos pos)
    {
        ChunkData data = ChunkData.get(world, pos);
        ICalendar calendar = Calendars.get(world);
        float rainfall = data.getRainfall(pos);
        if (rainfall < 100)
        {
            return Biome.RainType.NONE;
        }
        float temperature = calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
        return temperature < 0 ? Biome.RainType.SNOW : Biome.RainType.RAIN;
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
     * The reverse of {@link Climate#toVanillaTemperature(float)}
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
        final float monthFactor = NoiseUtil.lerp(currentMonth.getTemperatureModifier(), currentMonth.next().getTemperatureModifier(), delta);

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
        y = MathHelper.clamp(y, 0, 255); // Future proofing for 1.17
        if (y > SEA_LEVEL)
        {
            // -1.6 C / 10 blocks above sea level
            float elevationTemperature = MathHelper.clamp((y - SEA_LEVEL) * 0.16225f, 0, 17.822f);
            return averageTemperature + monthTemperature - elevationTemperature + dailyTemperature;
        }
        else if (y > DEPTH_LEVEL)
        {
            // The influence of daily and monthly temperature is reduced as depth increases
            float monthInfluence = (y - DEPTH_LEVEL) / (SEA_LEVEL - DEPTH_LEVEL); // Range 0 - 1
            float dailyInfluence = MathHelper.clamp(monthInfluence * 3f - 2f, 0, 1); // Range 0 - 1, decays faster than month influence
            return averageTemperature + NoiseUtil.lerp(0, monthTemperature, monthInfluence) + NoiseUtil.lerp(0, dailyTemperature, dailyInfluence);
        }
        else // y <= DEPTH_LEVEL
        {
            // At y = DEPTH_LEVEL, there will be no influence from either month or daily temperature
            // Between this and y = 0, linearly scale average temperature towards depth temperature
            float depthInfluence = y / DEPTH_LEVEL;
            return NoiseUtil.lerp(LAVA_LEVEL_TEMPERATURE, averageTemperature, depthInfluence);
        }
    }

    /**
     * Calculates the monthly temperature for a given latitude and month modifier
     */
    private static float calculateMonthlyTemperature(int z, float monthTemperatureModifier)
    {
        float temperatureScale = TFCConfig.SERVER.temperatureScale.get();
        return monthTemperatureModifier * NoiseUtil.triangle(LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE, LATITUDE_TEMPERATURE_VARIANCE_MEAN, 1 / (2 * temperatureScale), 0, z);
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
        RANDOM.setSeed(day);
        return ((RANDOM.nextFloat() - RANDOM.nextFloat()) + 0.3f * hourModifier) * 3f;
    }

    private Climate() {}
}