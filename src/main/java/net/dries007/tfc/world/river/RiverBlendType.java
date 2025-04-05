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
    BANKED(RiverNoise::banked), // Raised banks 1-2 blocks above water that may be higher than surrounding terrain in swampy biomes
    TALL_BANKED(RiverNoise::tallBanked), // Sim to banked, but taller for use in mud flat/salt flat biomes
    FLOODPLAIN(RiverNoise::floodplain), // Flat banks at water level with steep banks farther from river
    WIDE(RiverNoise::wide), // Wide, smooth V-shaped valleys
    CANYON(RiverNoise::canyon), // Tall, smooth V-shaped valleys
    TALL_CANYON(RiverNoise::tallCanyon), // Slot canyons with undercut walls
    TALUS(RiverNoise::talus), // Single line of cliffs with steep slopes above and below
    TERRACES(RiverNoise::terraces), // Stair-step canyons, like the Grand Canyon
    CAVE(RiverNoise::cave); // Underground river

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
