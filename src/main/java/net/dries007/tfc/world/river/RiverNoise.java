package net.dries007.tfc.world.river;

import net.minecraft.util.Mth;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public final class RiverNoise
{
    public static RiverNoiseSampler wide(long seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D baseNoise = new OpenSimplex2D(seed).octaves(4).spread(0.1f).scaled(-2.5f, 1.5f);
            final Noise2D distNoise = new OpenSimplex2D(seed + 71892341L).octaves(4).spread(0.1f).scaled(-0.1f, 0.1f);

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight = 58 + distFac * 7 + baseNoise.noise(x, z);

                return Math.min(riverHeight, heightIn);
            }
        };
    }

    public static RiverNoiseSampler canyon(long seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D baseNoise = new OpenSimplex2D(seed).octaves(4).spread(0.1f).scaled(-7, 3);
            final Noise2D distNoise = new OpenSimplex2D(seed).octaves(4).spread(0.1f).scaled(-0.23f, 0.15f);

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn)
            {
                final double distFac = info.normDistSq() * 1.3 + distNoise.noise(x, z);
                final double riverHeight = 55 + distFac * 16 + baseNoise.noise(x, z);

                return Math.min(riverHeight, heightIn);
            }
        };
    }

    public static RiverNoiseSampler cave(long seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D carvingCenterNoise = new OpenSimplex2D(seed).octaves(2).spread(0.02f).scaled(SEA_LEVEL_Y - 3, SEA_LEVEL_Y + 3);
            final Noise2D carvingHeightNoise = new OpenSimplex2D(seed + 1).octaves(4).spread(0.15f).scaled(8, 14);

            double weight, height, carvingHeight, carvingCenter;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn)
            {
                weight = Mth.clamp(info.normDistSq() * 1.6 - 0.3, 0d, 1d);
                height = heightIn;
                carvingHeight = carvingHeightNoise.noise(x, z);
                carvingCenter = carvingCenterNoise.noise(x, z);

                return heightIn;
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                final double distance = Math.abs(y - carvingCenter) / carvingHeight;
                final double noise = Math.max(1 - (distance * distance), 0);

                return Mth.lerp(weight, noise, noiseIn);
            }
        };
    }
}
