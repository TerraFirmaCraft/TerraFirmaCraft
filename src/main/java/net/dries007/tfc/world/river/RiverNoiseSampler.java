/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

/**
 * River noise samplers are implemented as modifiers on the original results produced by {@link net.dries007.tfc.world.BiomeNoiseSampler}s.
 * Thus, they take in the {@code height} and {@code noise} values, and generally do their own interpolation / blending, based on the distance to the river in question.
 */
public interface RiverNoiseSampler
{
    RiverNoiseSampler NONE = new RiverNoiseSampler() {};

    default double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double caveWeight)
    {
        return heightIn;
    }

    default double noise(int y, double noiseIn)
    {
        return noiseIn;
    }
}
