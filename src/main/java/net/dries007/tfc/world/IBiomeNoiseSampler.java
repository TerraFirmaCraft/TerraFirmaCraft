/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.dries007.tfc.world.noise.Noise2D;

public interface IBiomeNoiseSampler
{
    double FULL = 0;

    static IBiomeNoiseSampler fromHeightNoise(Noise2D heightNoise)
    {
        return new IBiomeNoiseSampler()
        {
            private float height;

            @Override
            public void setColumn(int x, int z)
            {
                height = heightNoise.noise(x, z);
            }

            @Override
            public double height()
            {
                return height;
            }

            @Override
            public double noise(int y)
            {
                return FULL;
            }
        };
    }

    static IBiomeNoiseSampler fromHeightAndCarvingNoise(Noise2D heightNoise, Noise2D carvingCenterNoise, Noise2D carvingHeightNoise)
    {
        return new IBiomeNoiseSampler()
        {
            private float height, carvingHeight, carvingCenter;

            @Override
            public void setColumn(int x, int z)
            {
                height = heightNoise.noise(x, z);
                carvingHeight = carvingHeightNoise.noise(x, z);
                carvingCenter = carvingCenterNoise.noise(x, z);
            }

            @Override
            public double height()
            {
                return height;
            }

            @Override
            public double noise(int y)
            {
                float distance = Math.abs(y - carvingCenter) / carvingHeight;
                return Math.max(1 - (distance * distance), 0);
            }
        };
    }

    void setColumn(int x, int z);

    double height();

    /**
     * @param y Always < height
     */
    double noise(int y);
}
