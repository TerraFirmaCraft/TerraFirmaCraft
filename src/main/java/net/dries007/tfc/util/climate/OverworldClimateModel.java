/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.tracker.WeatherHelpers;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.chunkdata.ChunkData;

/**
 * The climate model for TerraFirmaCraft's overworld. Provides a number of mechanics including:
 * - seasonal, monthly, and daily temperature variance.
 * - altitude based temperature, including both above and below ground effects.
 * - time varying precipitation types.
 */
public class OverworldClimateModel implements ClimateModel
{
    public static final float LAVA_LEVEL_TEMPERATURE = 15f;

    public static final float SEA_LEVEL = TFCChunkGenerator.SEA_LEVEL_Y;
    public static final float DEPTH_LEVEL = -64;

    public static final int FOGGY_DAY_RARITY = 10;
    public static final float FOGGY_RAINFALL_MINIMUM = 150f;
    public static final float FOGGY_RAINFALL_PEAK = 300f;

    public static final StreamCodec<ByteBuf, OverworldClimateModel> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, c -> c.climateSeed,
        ByteBufCodecs.FLOAT, c -> c.temperatureScale,
        OverworldClimateModel::new
    );

    /**
     * Obtain the climate model for the current dimension, assuming it is an {@link OverworldClimateModel}
     * This is intended for use in select world generation, which is fine with only functioning in an overworld climate model like scenario
     */
    @Nullable
    @Deprecated
    public static OverworldClimateModel getIfPresent(Object maybeLevel)
    {
        final Level unsafeLevel = Helpers.getUnsafeLevel(maybeLevel);
        if (unsafeLevel != null)
        {
            final ClimateModel model = Climate.get(unsafeLevel);
            if (model instanceof OverworldClimateModel overworldClimateModel)
            {
                return overworldClimateModel;
            }
        }
        return null;
    }

    protected final long climateSeed;
    protected final float temperatureScale;

    public OverworldClimateModel(ServerLevel level, ChunkGeneratorExtension extension)
    {
        this(
            LinearCongruentialGenerator.next(level.getSeed(), 719283741234L),
            extension.settings().temperatureScale()
        );
    }

    protected OverworldClimateModel(long climateSeed, float temperatureScale)
    {
        this.climateSeed = climateSeed;
        this.temperatureScale = temperatureScale;
    }

    @Override
    public ClimateModelType<?> type()
    {
        return ClimateModels.OVERWORLD.get();
    }

    @Override
    public float hemisphereScale()
    {
        return temperatureScale;
    }

    @Override
    public float getAverageTemperature(LevelReader level, BlockPos pos)
    {
        return ChunkData.get(level, pos).getAverageTemp(pos);
    }

    @Override
    public float getTemperature(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        final ChunkData data = ChunkData.get(level, pos);

        // Month temperature
        final Month currentMonth = ICalendar.getMonthOfYear(calendarTicks, daysInMonth);
        final float delta = ICalendar.getFractionOfMonth(calendarTicks, daysInMonth);
        final float monthFactor = Mth.lerp(delta, currentMonth.getTemperatureModifier(), currentMonth.next().getTemperatureModifier());

        final float monthTemperature = calculateMonthlyTemperature(pos.getZ(), monthFactor);
        final float dailyTemperature = calculateDailyTemperature(calendarTicks);

        return adjustTemperatureByElevation(pos.getY(), data.getAverageTemp(pos), monthTemperature, dailyTemperature);
    }

    @Override
    public float getAverageRainfall(LevelReader level, BlockPos pos)
    {
        return ChunkData.get(level, pos).getRainfall(pos);
    }

    @Override
    public float getRainfallVariance(LevelReader level, BlockPos pos)
    {
        return ChunkData.get(level, pos).getRainVariance(pos);
    }

    @Override
    public float getRainfall(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        final ChunkData data = ChunkData.get(level, pos);
        final float rainVariance = data.getRainVariance(pos);
        final float rainAverage = data.getRainfall(pos);
        final float fractionOfYear = ICalendar.getFractionOfYear(calendarTicks, daysInMonth);

        // For positive values of variance, drought in winter, rain in summer, reverse for negative values
        return rainVariance == 0 ? 0 : Helpers.triangle(rainVariance * rainAverage, rainAverage, 1f, fractionOfYear + 0.75f);
    }

    @Override
    public float getBaseGroundwater(LevelReader level, BlockPos pos)
    {
        return ChunkData.get(level, pos).getBaseGroundwater(pos);
    }

    @Override
    public float getAverageGroundwater(LevelReader level, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(level, pos);
        return Math.clamp(data.getBaseGroundwater(pos) + data.getRainfall(pos), MIN_RAINFALL, MAX_RAINFALL);
    }

    @Override
    public float getGroundwater(LevelReader level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        final float baseGroundwater = getBaseGroundwater(level, pos);
        final float monthlyRainfall = getRainfall(level, pos, calendarTicks, daysInMonth);

        return Math.clamp(baseGroundwater + monthlyRainfall, 0f, 1000f);
    }

    /**
     * In vanilla, rain is simulated as {@code [12_000, 24_000]} ticks on, {@code [12_000, 180_000]} ticks off.
     */
    @Override
    public float getRain(long calendarTicks)
    {
        final long salt = 8917234598231321L;
        final long segmentLength = 66_000;
        final long segmentId = Math.floorDiv(calendarTicks, segmentLength);
        final long segmentLeft = segmentId * segmentLength;

        // This works by breaking up the entire timeline into "segments", of exactly 66_000 in length. We generate exactly
        // one rainfall section into each segment, of a random length between 12_000 and 24_000. This mirrors vanilla behavior
        // fairly well, although is a bit more regular overall. It is roughly twice vanilla P(rain), which we scale down
        // based on rainfall and intensity.
        //
        // Vanilla has rain as [12_000, 24_000] ticks on, [12_000, 180_000] ticks off. Our baseline here is 2x vanilla,
        // and then we interpolate based on the rainfall at a given position to know if it is truly raining.

        // Infer the default position of the next segment rainfall, in order to apply boundary conditions
        final RandomSource nextSegment = seededRandom(segmentId + 1, salt);
        final int nextLength = nextSegment.nextIntBetweenInclusive(12_000, 24_000);
        final int nextLeft = (int) (nextSegment.nextFloat() * (segmentLength - 12_000 - nextLength)); // Need to use `nextFloat()` here for stability

        // The boundary we leave on the right, in order to prevent merging
        final int boundaryRight = Math.min(0, 12_000 - nextLeft);

        // Calculate the current segment
        final RandomSource segment = seededRandom(segmentId, salt);
        final int length = segment.nextIntBetweenInclusive(12_000, 24_000);
        final int left = (int) (segment.nextFloat() * (segmentLength - boundaryRight - nextLength));

        if (calendarTicks < segmentLeft + left || calendarTicks > segmentLeft + left + length)
        {
            return -1; // Not raining, since we're not within the target segment
        }

        // We are raining, so calculate intensity, and distance to center
        final int halfLength = length / 2;
        final float rainIntensity = segment.nextFloat();
        final float timeIntensity = 1f - Math.abs((segmentLeft + left + halfLength) - calendarTicks) / (float) halfLength;

        // Average the two factors
        return 0.5f * (rainIntensity + timeIntensity);
    }

    @Override
    public boolean getThunder(long calendarTicks)
    {
        // Thunder is simulated using a similar segment system, and checking for overlap with rain. In vanilla, thunder is
        // [3600, 15600] ticks on, [12000, 180000] ticks off, or 9600 on / 96000 off. P(thunder | rain) = 0.1, and P(thunder) = 0.01875
        final long salt = 9871293851234123L;
        final int segmentLength = 105_600;
        final long segmentId = Math.floorDiv(calendarTicks, segmentLength);
        final long segmentLeft = segmentId * segmentLength;

        final RandomSource segment = seededRandom(segmentId, salt);
        final int length = segment.nextIntBetweenInclusive(3600, 15_600);
        final int left = segment.nextInt(segmentLength - length);

        // Thunder|Rain if we are within the segment
        return calendarTicks >= segmentLeft + left && calendarTicks <= segmentLeft + left + length;
    }

    @Override
    public boolean supportsRain()
    {
        return true;
    }

    @Override
    public float getFog(LevelReader level, BlockPos pos, long calendarTime)
    {
        // seed as if we're 2 hours in the future, in order to start the cycle at 4am (2 hours before sunrise)
        final long day = ICalendar.getTotalDays(calendarTime + (2 * ICalendar.TICKS_IN_HOUR));
        final RandomSource random = seededRandom(day, 129341623413L);
        if (random.nextInt(FOGGY_DAY_RARITY) != 0)
        {
            return 0;
        }

        final float fogModifier = random.nextFloat(); // untransformed value of the fog

        final long dayTime = Calendars.get(level).getCalendarDayTime();
        final float scaledTime; // a value between 0 and 1
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
            scaledTime = Mth.map(dayTime, 4000, 6000, 1, 0);
        }
        else // 12pm to 4am
        {
            scaledTime = 0;
        }

        final float rainfall = getRainfall(level, pos);
        final float rainfallModifier = Mth.clampedMap(rainfall, FOGGY_RAINFALL_MINIMUM, FOGGY_RAINFALL_PEAK, 0, 1);
        final float skylightModifier = Mth.clampedMap(level.getBrightness(LightLayer.SKY, pos), 0f, 10f, 0f, 1f);

        return Helpers.easeInOutCubic(scaledTime) * fogModifier * rainfallModifier * skylightModifier;
    }

    @Override
    public Vec2 getWind(Level level, BlockPos pos, long calendarTicks, int daysInMonth)
    {
        // No wind below sea level
        final int y = pos.getY();
        if (y < SEA_LEVEL - 6)
        {
            return Vec2.ZERO;
        }

        final RandomSource random = seededRandom(ICalendar.getTotalDays(calendarTicks), 129341623413L);
        final boolean isRaining = WeatherHelpers.isPrecipitating(getRain(calendarTicks), getRainfall(level, pos, calendarTicks, daysInMonth));
        final Holder<Biome> biome = level.getBiome(pos);

        if (biome.is(TFCTags.Biomes.HAS_PREDICTABLE_WINDS))
        {
            // Predictable winds occur in oceans, and other biomes which want to have a wind functionality that is more than cosmetic
            // This is really provided for i.e. aleki's ships mod.
            //
            // The basic formulation is that we have prevailing winds which head in predictable angles. They will alternate based
            // on if it is currently day or night, and based on the "band", which are strips of coordinates which alternate N-S (based
            // on the Z-coordinate)
            final boolean isDay = level.getDayTime() % 24000 < 12000;

            final int windScale = TFCConfig.SERVER.oceanWindScale.get();
            final boolean oddBand = pos.getZ() < 0 ?
                pos.getZ() % (windScale * 2) < windScale :
                pos.getZ() % (windScale * 2) > windScale;
            final float intensity = random.nextFloat() * 0.3f + 0.3f + (isRaining ? 0.4f : 0);
            float angle;
            if (isDay && oddBand)
                angle = Mth.PI / 4;
            else if (isDay)
                angle = 7 * Mth.PI / 4;
            else if (oddBand)
                angle = 5 * Mth.PI / 4;
            else
                angle = 3 * Mth.PI / 4;
            angle += random.nextFloat() * 0.2f - 0.1f;
            return new Vec2(Mth.cos(angle), Mth.sin(angle)).scale(intensity);
        }

        final float preventFrequentWindyDays = random.nextFloat() < 0.1f ? 1f : random.nextFloat();
        final float intensity = Math.min(0.5f * random.nextFloat() * preventFrequentWindyDays
            + 0.4f * Mth.clampedMap(y, SEA_LEVEL, SEA_LEVEL + 65, 0f, 1f)
            + (isRaining ? 0.6f : 0), 1f);
        final float angle = random.nextFloat() * Mth.TWO_PI;
        return new Vec2(Mth.cos(angle) * intensity, Mth.sin(angle) * intensity);
    }

    /**
     * Calculates the average monthly temperature for a location and given month.
     */
    public float getAverageMonthlyTemperature(int z, int y, float averageTemperature, float monthFactor)
    {
        final float monthlyTemperature = calculateMonthlyTemperature(z, monthFactor);
        return adjustTemperatureByElevation(y, averageTemperature, monthlyTemperature, 0);
    }

    /**
     * Adjusts a series of temperature factors by elevation. Returns the sum temperature after adjustment.
     * <ul>
     *     <li>Above sea level, temperature lowers linearly with y.</li>
     *     <li>Below sea level, temperature tends towards the average temperature for the area (having less influence from
     *     daily and monthly temperature)</li>
     *     <li>Towards the bottom of the world, temperature tends towards a constant as per the existence of "lava level"</li>
     * </ul>
     */
    protected float adjustTemperatureByElevation(int y, float averageTemperature, float monthTemperature, float dailyTemperature)
    {
        if (y > SEA_LEVEL)
        {
            // -1.6 C / 10 blocks above sea level
            final float elevationTemperature = Mth.clamp((y - SEA_LEVEL) * 0.16225f, 0, 17.822f);
            return averageTemperature + monthTemperature - elevationTemperature + dailyTemperature;
        }
        else if (y > 0)
        {
            // The influence of daily and monthly temperature is reduced as depth increases
            final float monthInfluence = Helpers.inverseLerp(y, 0, SEA_LEVEL);
            final float dailyInfluence = Mth.clamp(monthInfluence * 3f - 2f, 0, 1); // Range 0 - 1, decays faster than month influence
            return averageTemperature + Mth.lerp(monthInfluence, (float) 0, monthTemperature) + Mth.lerp(dailyInfluence, (float) 0, dailyTemperature);
        }
        else
        {
            // At y = 0, there will be no influence from either month or daily temperature
            // Between this and the bottom of the world, linearly scale average temperature towards depth temperature
            final float depthInfluence = Helpers.inverseLerp(y, DEPTH_LEVEL, 0);
            return Mth.lerp(depthInfluence, LAVA_LEVEL_TEMPERATURE, averageTemperature);
        }
    }

    /**
     * Calculates the monthly temperature for a given latitude and month modifier
     */
    protected float calculateMonthlyTemperature(int z, float monthTemperatureModifier)
    {
        return monthTemperatureModifier * (temperatureScale == 0 ? 0 : Helpers.triangle(-3f, 15f, 1f / (2f * temperatureScale), z));
    }

    /**
     * Calculates the daily variation temperature at a given time. Influenced by both random variation day by day, and the time of day.
     * @return A value in the range {@code [-4.0, 4.0]}
     */
    protected float calculateDailyTemperature(long calendarTime)
    {
        // Hottest part of the day at 12, coldest at 0
        int hourOfDay = ICalendar.getHourOfDay(calendarTime);
        if (hourOfDay > 12)
        {
            // Range: 0 - 12
            hourOfDay = 24 - hourOfDay;
        }
        // Range: -1 - 1
        final float hourModifier = (hourOfDay / 6f) - 1f;

        // Note: this does not use world seed, as that is not synced from server - client, resulting in the seed being different
        final long day = ICalendar.getTotalDays(calendarTime);
        final RandomSource random = seededRandom(day, 1986239412341L);
        return ((random.nextFloat() - random.nextFloat()) + 0.3f * hourModifier) * 3f;
    }

    protected RandomSource seededRandom(long day, long salt)
    {
        return new XoroshiroRandomSource(LinearCongruentialGenerator.next(day, climateSeed), salt);
    }
}
