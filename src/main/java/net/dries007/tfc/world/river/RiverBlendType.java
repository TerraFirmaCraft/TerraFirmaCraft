/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;

public enum RiverBlendType
{
    NONE(seed -> RiverNoiseSampler.NONE),
    WIDE(RiverNoise::wide),
    CANYON(RiverNoise::canyon),
    TALL_CANYON(RiverNoise::tallCanyon),
    CAVE(RiverNoise::cave);

    public static final RiverBlendType[] ALL = values();
    public static final int SIZE = ALL.length;

    private final Long2ObjectFunction<RiverNoiseSampler> factory;

    RiverBlendType(Long2ObjectFunction<RiverNoiseSampler> factory)
    {
        this.factory = factory;
    }

    public RiverNoiseSampler createNoiseSampler(long seed)
    {
        return factory.apply(seed);
    }
}
