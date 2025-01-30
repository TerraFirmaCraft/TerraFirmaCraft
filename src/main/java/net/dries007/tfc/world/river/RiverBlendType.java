/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import java.util.function.Function;

import net.dries007.tfc.world.Seed;

public enum RiverBlendType
{
    NONE(seed -> RiverNoiseSampler.NONE),
    WIDE(RiverNoise::wide),
    CANYON(RiverNoise::canyon),
    TALL_CANYON(RiverNoise::tallCanyon),
    CAVE(RiverNoise::cave);

    public static final RiverBlendType[] ALL = values();
    public static final int SIZE = ALL.length;

    private final Function<Seed, RiverNoiseSampler> factory;

    RiverBlendType(Function<Seed, RiverNoiseSampler> factory)
    {
        this.factory = factory;
    }

    public RiverNoiseSampler createNoiseSampler(Seed seed)
    {
        return factory.apply(seed);
    }
}
