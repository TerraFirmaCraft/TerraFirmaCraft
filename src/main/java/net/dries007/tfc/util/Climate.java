/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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
import net.dries007.tfc.world.noise.INoise1D;
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
    public static final float MINIMUM_TEMPERATURE_SCALE = -24f;
    public static final float MAXIMUM_TEMPERATURE_SCALE = 30f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE = -6.5f;
    public static final float LATITUDE_TEMPERATURE_VARIANCE_MEAN = 13.5f;
    public static final float REGIONAL_TEMPERATURE_SCALE = 2f;
    public static final float REGIONAL_RAINFALL_SCALE = 50f;

    /**
     * Magic numbers. These probably mean something
     */
    public static final float MINIMUM_RAINFALL = 0f;
    public static final float MAXIMUM_RAINFALL = 500f;
    public static final float SNOW_MELT_TEMPERATURE = 4f;
    public static final float SNOW_STACKING_TEMPERATURE = -4f;

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

    public static float calculateMonthlyTemperature(int z, int y, float averageTemperature, float monthTemperatureModifier)
    {
        float temperatureScale = TFCConfig.SERVER.temperatureScale.get();
        float monthTemperature = monthTemperatureModifier * INoise1D.triangle(LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE, LATITUDE_TEMPERATURE_VARIANCE_MEAN, 1 / (2 * temperatureScale), 0, z);
        float elevationTemperature = MathHelper.clamp((y - TFCChunkGenerator.SEA_LEVEL) * 0.16225f, 0, 17.822f);
        return averageTemperature + monthTemperature - elevationTemperature;
    }

    public static float calculateTemperature(BlockPos pos, float averageTemperature, Calendar calendar)
    {
        return calculateTemperature(pos.getZ(), pos.getY(), averageTemperature, calendar.getCalendarTicks(), calendar.getCalendarDaysInMonth());
    }

    public static float calculateTemperature(int z, int y, float averageTemperature, long calendarTime, long daysInMonth)
    {
        // Start by checking the monthly / seasonal temperature
        Month currentMonth = ICalendar.getMonthOfYear(calendarTime, daysInMonth);
        Month nextMonth = currentMonth.next();
        float delta = ICalendar.getFractionOfMonth(calendarTime, daysInMonth);
        float monthFactor = NoiseUtil.lerp(currentMonth.getTemperatureModifier(), nextMonth.getTemperatureModifier(), delta);
        float temperatureScale = TFCConfig.SERVER.temperatureScale.get();
        float monthTemperature = monthFactor * INoise1D.triangle(LATITUDE_TEMPERATURE_VARIANCE_AMPLITUDE, LATITUDE_TEMPERATURE_VARIANCE_MEAN, 1 / (2 * temperatureScale), 0, z);

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
        float elevationTemperature = MathHelper.clamp((y - TFCChunkGenerator.SEA_LEVEL) * 0.16225f, 0, 17.822f);

        // Sum all different temperature values.
        return averageTemperature + monthTemperature + dailyTemperature - elevationTemperature;
    }

    private Climate() {}
}