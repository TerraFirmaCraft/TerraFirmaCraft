/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import java.util.function.Function;

import net.dries007.tfc.world.Seed;

public enum CenteredFeatureBlendType
{
    CINDER_CONE(CenteredFeatureNoise::cinder), // The original, small-cone volcanoes
    TUYA(CenteredFeatureNoise::tuya), // Flat-topped mounds formed around ice sheets
    TUFF_RING(CenteredFeatureNoise::tuffRing), // Rings of tuff, similar to Diamond Head, Molokini, etc.
    ATOLL(CenteredFeatureNoise::atolls), // Atolls
    STRATOVOLCANO(CenteredFeatureNoise::stratovolcano); // Stratovolcanos, similar to Mt. Fuji etc.

    public static final CenteredFeatureBlendType[] ALL = values();
    public static final int SIZE = ALL.length;

    private final Function<Seed, CenteredFeatureNoiseSampler> factory;

    CenteredFeatureBlendType(Function<Seed, CenteredFeatureNoiseSampler> factory)
    {
        this.factory = factory;
    }

    public CenteredFeatureNoiseSampler createNoiseSampler(Seed seed)
    {
        return factory.apply(seed);
    }
}
