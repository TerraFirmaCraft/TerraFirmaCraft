/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.OptionalLong;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;

import net.dries007.tfc.common.blocks.IcePileBlock;
import net.dries007.tfc.common.blocks.SnowPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendar;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
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
    public static final float SNOW_FREEZE_TEMPERATURE = 0f;
    public static final float SNOW_MELT_TEMPERATURE = 4f;

    public static final float ICE_FREEZE_TEMPERATURE = -2f;
    public static final float ICE_MELT_TEMPERATURE = 2f;

    public static final float ICICLE_MIN_FREEZE_TEMPERATURE = -10f;
    public static final float ICICLE_MAX_FREEZE_TEMPERATURE = -2f;
    public static final float ICICLE_MELT_TEMPERATURE = 2f;

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

    public static final OverworldClimateModel INSTANCE = new OverworldClimateModel();

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
    private OptionalLong climateSeed = OptionalLong.empty();

    // For world generation climate
    private Noise2D snowPatchNoise = (x, z) -> 0;
    private Noise2D icePatchNoise = (x, z) -> 0;

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
        final Random random = seededRandom(day, 129341623413L);
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
    public void onChunkLoad(WorldGenLevel level, ChunkAccess chunk, ChunkData chunkData)
    {
        final ChunkPos chunkPos = chunk.getPos();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockState snowState = Blocks.SNOW.defaultBlockState();

        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++)
        {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++)
            {
                mutablePos.set(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);

                final float noise = snowPatchNoise.noise(x, z);
                final float temperature = OverworldClimateModel.getTemperature(mutablePos, chunkData, Calendars.SERVER);
                final float snowTemperatureModifier = Mth.clampedMap(temperature, -10f, 2f, -1, 1);

                // Handle snow
                BlockState stateAt = level.getBlockState(mutablePos);
                if (snowTemperatureModifier + noise < 0)
                {
                    // Snow
                    if (stateAt.isAir() && snowState.canSurvive(level, mutablePos))
                    {
                        // Place snow
                        level.setBlock(mutablePos, Blocks.SNOW.defaultBlockState(), 2);
                        mutablePos.move(Direction.DOWN);
                        level.setBlock(mutablePos, Helpers.setProperty(level.getBlockState(mutablePos), SnowyDirtBlock.SNOWY, true), 2);
                        mutablePos.move(Direction.UP);
                    }
                    else if (SnowPileBlock.canPlaceSnowPile(level, mutablePos, stateAt))
                    {
                        SnowPileBlock.placeSnowPile(level, mutablePos, stateAt, false);
                        level.setBlock(mutablePos, Helpers.setProperty(level.getBlockState(mutablePos), SnowyDirtBlock.SNOWY, true), 2);
                    }
                }
                else
                {
                    // No snow (try and melt existing snow if we find it, which would be one block down)
                    if (EnvironmentHelpers.isSnow(stateAt))
                    {
                        SnowPileBlock.removePileOrSnow(level, mutablePos, stateAt, true);
                    }
                }


                // Handle ice
                mutablePos.move(Direction.DOWN);
                stateAt = level.getBlockState(mutablePos);

                if (EnvironmentHelpers.isWater(stateAt) || EnvironmentHelpers.isIce(stateAt))
                {
                    final float temperatureModifier, waterDepthModifier;
                    final float threshold = icePatchNoise.noise(x * 0.2f, z * 0.2f) + Mth.clamp(temperature * 0.1f, -0.2f, 0.2f);

                    if (Helpers.isBlock(stateAt, Blocks.ICE) || Helpers.isBlock(stateAt, Blocks.WATER))
                    {
                        // Fresh water areas don't freeze over in deep water
                        final int waterDepth = mutablePos.getY() - level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
                        waterDepthModifier = Mth.clampedMap(waterDepth, 0, 5, 0, 1);

                        // And have a fairly conservative freezing threshold
                        temperatureModifier = Mth.clampedMap(temperature, ICE_FREEZE_TEMPERATURE, ICE_MELT_TEMPERATURE, -0.4f, 1);
                    }
                    else
                    {
                        // Oceans (or specifically, salt water), freezes at a much lower point, and also is time invariant (meaning it queries the maximum annual temperature and uses that), and also doesn't care about depth (since oceans are deep yo)
                        final float maxAnnualTemperature = getAverageMonthlyTemperature(z, TFCChunkGenerator.SEA_LEVEL_Y, chunkData.getAverageTemp(x, z), 1);
                        waterDepthModifier = 0;
                        temperatureModifier = Mth.clampedMap(maxAnnualTemperature, -4f, 8f, -0.8f, 1);
                    }

                    if (waterDepthModifier + temperatureModifier < threshold && temperatureModifier < 1)
                    {
                        // Sea Ice, Ice, or Ice Pile
                        if (Helpers.isBlock(stateAt, TFCBlocks.SALT_WATER.get()))
                        {
                            level.setBlock(mutablePos, TFCBlocks.SEA_ICE.get().defaultBlockState(), 2);
                        }
                        else // Fresh water
                        {
                            IcePileBlock.placeIcePileOrIce(level, mutablePos, stateAt, true);
                        }
                    }
                    else
                    {
                        // None of the above - melt ice if possible
                        if (Helpers.isBlock(stateAt, TFCBlocks.SEA_ICE.get()))
                        {
                            level.setBlock(mutablePos, TFCBlocks.SALT_WATER.get().defaultBlockState(), 2);
                            level.scheduleTick(mutablePos, TFCFluids.SALT_WATER.getSource(), 0);
                        }
                        else
                        {
                            IcePileBlock.removeIcePileOrIce(level, mutablePos, stateAt);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateCachedTemperatureSettings(ClimateSettings settings, long climateSeed)
    {
        if (this.climateSeed.isEmpty() || this.climateSeed.getAsLong() != climateSeed)
        {
            this.temperatureSettings = settings;
            this.climateSeed = OptionalLong.of(climateSeed);
            this.snowPatchNoise = new OpenSimplex2D(climateSeed + 72397489123L).octaves(2).spread(0.3f).scaled(-1, 1);
            this.icePatchNoise = new OpenSimplex2D(climateSeed + 192639412341L).octaves(3).spread(0.6f);
        }
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
        final Random random = seededRandom(day, 1986239412341L);
        return ((random.nextFloat() - random.nextFloat()) + 0.3f * hourModifier) * 3f;
    }

    private Random seededRandom(long day, long salt)
    {
        long seed = LinearCongruentialGenerator.next(climateSeed.orElse(0L), day);
        seed = LinearCongruentialGenerator.next(seed, salt);
        return new Random(seed);
    }
}
