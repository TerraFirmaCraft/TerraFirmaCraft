/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.DataManager;

/**
 * A range of <strong>hydration</strong> and <strong>temperature</strong> to check for growing plants or crops. This uses hydration
 * which can be influenced by nearby water, rather than rainfall. This is as such not intended for use in world generation.
 */
public record ClimateRange(
    int minHydration, int maxHydration, int hydrationWiggleRange,
    float minTemperature, float maxTemperature, float temperatureWiggleRange
)
{
    public static final Codec<ClimateRange> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.INT.optionalFieldOf("min_hydration", 0).forGetter(c -> c.minHydration),
        Codec.INT.optionalFieldOf("max_hydration", 100).forGetter(c -> c.maxHydration),
        Codec.INT.optionalFieldOf("hydration_wiggle_range", 0).forGetter(c -> c.hydrationWiggleRange),
        Codec.FLOAT.optionalFieldOf("min_temperature", -100f).forGetter(c -> c.minTemperature),
        Codec.FLOAT.optionalFieldOf("max_temperature", 100f).forGetter(c -> c.maxTemperature),
        Codec.FLOAT.optionalFieldOf("temperature_wiggle_range", 0f).forGetter(c -> c.temperatureWiggleRange)
    ).apply(i, ClimateRange::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClimateRange> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, c -> c.minHydration,
        ByteBufCodecs.VAR_INT, c -> c.maxHydration,
        ByteBufCodecs.VAR_INT, c -> c.hydrationWiggleRange,
        ByteBufCodecs.FLOAT, c -> c.minTemperature,
        ByteBufCodecs.FLOAT, c -> c.maxTemperature,
        ByteBufCodecs.FLOAT, c -> c.temperatureWiggleRange,
        ClimateRange::new
    );


    public static final DataManager<ClimateRange> MANAGER = new DataManager<>(Helpers.identifier("climate_ranges"), ClimateRange.CODEC, ClimateRange.STREAM_CODEC);
    public static final ClimateRange NOOP = new ClimateRange(0, 100, 0, -100, 100, 0);

    public int getMinHydration(boolean allowWiggle)
    {
        return allowWiggle ? minHydration - hydrationWiggleRange : minHydration;
    }

    public int getMaxHydration(boolean allowWiggle)
    {
        return allowWiggle ? maxHydration + hydrationWiggleRange : maxHydration;
    }

    public float getMinTemperature(boolean allowWiggle)
    {
        return allowWiggle ? minTemperature - temperatureWiggleRange : minTemperature;
    }

    public float getMaxTemperature(boolean allowWiggle)
    {
        return allowWiggle ? maxTemperature + temperatureWiggleRange : maxTemperature;
    }

    public Result checkHydration(int hydration, boolean allowWiggle)
    {
        return check(hydration, minHydration, maxHydration, hydrationWiggleRange, allowWiggle);
    }

    public Result checkTemperature(float temperature, boolean allowWiggle)
    {
        return check(temperature, minTemperature, maxTemperature, temperatureWiggleRange, allowWiggle);
    }

    public boolean checkBoth(int hydration, float temperature, boolean allowWiggle)
    {
        return checkHydration(hydration, allowWiggle) == Result.VALID && checkTemperature(temperature, allowWiggle) == Result.VALID;
    }

    @NotNull
    private Result check(float value, float min, float max, float range, boolean allowRange)
    {
        if (allowRange)
        {
            min -= range;
            max += range;
        }
        if (value < min)
        {
            return Result.LOW;
        }
        if (value > max)
        {
            return Result.HIGH;
        }
        return Result.VALID;
    }

    public enum Result
    {
        LOW, VALID, HIGH
    }
}
