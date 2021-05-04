package net.dries007.tfc.world;

import net.dries007.tfc.world.noise.INoise2D;

public interface IBiomeNoiseSampler
{
    double FULL = 0;

    static IBiomeNoiseSampler fromHeightNoise(INoise2D heightNoise)
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

    static IBiomeNoiseSampler fromHeightAndCarvingNoise(INoise2D heightNoise, INoise2D carvingCenterNoise, INoise2D carvingHeightNoise)
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
                float factor = 1.2f;
                return factor * (1 - distance);
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
