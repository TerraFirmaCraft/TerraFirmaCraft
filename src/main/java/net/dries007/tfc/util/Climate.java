/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;
import net.dries007.tfc.world.noise.NoiseUtil;

/**
 * Central class for all TFC climate requirements.
 * This is only valid in the overworld!
 */
public final class Climate
{
    /**
     * Constants for temperature calculation. Do not reference these directly, they do not have much meaning outside the context they are used in
     */
    public static final float MAXIMUM_TEMPERATURE_SCALE = 30f;
    public static final float MINIMUM_TEMPERATURE_SCALE = -28f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE = 6.5f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_MEAN = 13.5f;

    private static final Random RANDOM = new Random(); // Used for daily temperature variations
    private static final float PI = (float) Math.PI;

    public static float getAverageTemperature(IWorld world, BlockPos pos)
    {
        ChunkData data = ChunkData.get(world, pos);
        return data.getAverageTemp(pos);
    }

    public static float getTemperature(IWorld world, BlockPos pos)
    {
        ChunkData data = ChunkData.get(world, pos);
        return calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), Calendars.SERVER.getCalendarTicks(), Calendars.SERVER.getCalendarDaysInMonth());
    }

    public static float getRainfall(IWorld world, BlockPos pos)
    {
        ChunkData data = ChunkData.get(world, pos);
        return data.getRainfall(pos);
    }

    public static float getAverageTemperature(BlockPos pos)
    {
        ChunkData data = ChunkDataCache.getUnsided().getOrEmpty(pos);
        return data.getAverageTemp(pos);
    }

    public static float getTemperature(BlockPos pos)
    {
        ChunkData data = ChunkDataCache.getUnsided().getOrEmpty(pos);
        return calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), Calendars.SERVER.getCalendarTicks(), Calendars.SERVER.getCalendarDaysInMonth());
    }

    /**
     * For when the logical side is known
     */
    public static float getTemperature(BlockPos pos, boolean isClient)
    {
        ChunkData data = (isClient ? ChunkDataCache.CLIENT : ChunkDataCache.SERVER).getOrEmpty(pos);
        Calendar calendar = isClient ? Calendars.CLIENT : Calendars.SERVER;
        return calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static float getRainfall(BlockPos pos, boolean isClient)
    {
        ChunkData data = (isClient ? ChunkDataCache.CLIENT : ChunkDataCache.SERVER).getOrEmpty(pos);
        return data.getRainfall(pos);
    }

    public static float getRainfall(BlockPos pos)
    {
        ChunkData data = ChunkDataCache.getUnsided().getOrEmpty(pos);
        return data.getRainfall(pos);
    }

    public static float getRainfall(BlockPos pos, ChunkDataCache cache)
    {
        ChunkData data = ChunkDataCache.getUnsided().getOrEmpty(pos);
        return data.getRainfall(pos);
    }

    private static float getTemperature(BlockPos pos, ChunkDataCache cache)
    {
        ChunkData data = cache.getOrEmpty(pos);
        return calculateTemperature(pos.getZ(), pos.getY(), data.getAverageTemp(pos), Calendars.SERVER.getCalendarTicks(), Calendars.SERVER.getCalendarDaysInMonth());
    }

    private static float calculateTemperature(int z, int y, float averageTemperature, long calendarTime, long daysInMonth)
    {
        // Start by checking the monthly / seasonal temperature
        Month currentMonth = ICalendar.getMonthOfYear(calendarTime, daysInMonth);
        Month nextMonth = currentMonth.next();
        float delta = ICalendar.getFractionOfMonth(calendarTime, daysInMonth);
        float monthFactor = NoiseUtil.lerp(currentMonth.getTemperatureModifier(), nextMonth.getTemperatureModifier(), delta);
        float monthTemperature = monthFactor * (LATITUDE_TEMPERATURE_VARIANCE_MEAN + LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE * MathHelper.sin(PI * z / TFCConfig.COMMON.temperatureLayerScale.get()));

        // Next, add hourly and daily variations
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
        float dailyTemperature = ((RANDOM.nextFloat() - RANDOM.nextFloat()) + 0.3f * hourModifier) * 3f;

        // Finally, add elevation based temperature
        // Internationally accepted average lapse time is 6.49 K / 1000 m, for the first 11 km of the atmosphere. Our temperature is scales the 110 m against 2750 m, so that gives us a change of 1.6225 / 10 blocks.
        float elevationTemperature = MathHelper.clamp((y - TFCConfig.COMMON.seaLevel.get()) * 0.16225f, 0, 17.822f);

        // Sum all different temperature values.
        return averageTemperature + monthTemperature + dailyTemperature + elevationTemperature;
    }

    private Climate() {}
}