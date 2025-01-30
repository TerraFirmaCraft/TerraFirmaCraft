/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.river;

import net.minecraft.util.Mth;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.Noise3D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.noise.OpenSimplex3D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public final class RiverNoise
{
    public static RiverNoiseSampler wide(Seed seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-2.5f, 1.5f);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.15f, 0.15f);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double caveWeight)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight = 58 + distFac * 7 + baseNoise.noise(x, z);

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                return y > height ? 0 : noiseIn;
            }
        };
    }

    public static RiverNoiseSampler canyon(Seed seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-7, 3);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.3f, 0.2f);
            final Noise2D lowFreqCliffNoise = new OpenSimplex2D(seed.next()).spread(0.0007f).clamped(0, 1);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double caveWeight)
            {
                final double distFac = info.normDistSq() * 1.3 + distNoise.noise(x, z);
                final double adjDistFac = distFac > 0.6 ? distFac * 0.4 + 0.8 : distFac;

                final double riverHeight = 55 + Mth.lerp(lowFreqCliffNoise.noise(x, z), distFac, adjDistFac) * 16 + baseNoise.noise(x, z);

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                return y > height ? 0 : noiseIn;
            }
        };
    }

    public static RiverNoiseSampler tallCanyon(Seed seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-7, 3);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.3f, 0.2f);
            final Noise3D cliffNoise = new OpenSimplex3D(seed.next()).octaves(2).spread(0.1f).scaled(0, 3);

            private double distFac; // 0 ~ center of river, 1 ~ distant from river
            private int x, z;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double caveWeight)
            {
                final double distFac = info.normDistSq() * 1.3 + distNoise.noise(x, z);
                final double adjDistFac = distFac > 0.32 ? distFac * 0.2 + 1.6 : distFac;

                final double riverHeight = 55 + adjDistFac * 16 + baseNoise.noise(x, z);

                this.distFac = Math.max(0, distFac * 0.7);
                this.x = x;
                this.z = z;

                return Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                return Mth.clampedLerp(rawNoise(y), noiseIn, distFac);
            }

            private double rawNoise(int y)
            {
                if (y > SEA_LEVEL_Y + 35)
                {
                    return 0;
                }
                else if (y > SEA_LEVEL_Y + 20)
                {
                    final double easing = 1 - (y - SEA_LEVEL_Y - 20) / 15f;
                    return easing * cliffNoise.noise(x, y, z);
                }
                else if (y > SEA_LEVEL_Y)
                {
                    return cliffNoise.noise(x, y, z);
                }
                else if (y > SEA_LEVEL_Y - 8)
                {
                    final double easing = (y - SEA_LEVEL_Y + 8) / 8d;
                    return easing * cliffNoise.noise(x, y, z);
                }
                return 0;
            }
        };
    }

    public static RiverNoiseSampler cave(Seed seed)
    {
        return new RiverNoiseSampler() {

            final Noise2D carvingCenterNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.02f).scaled(SEA_LEVEL_Y - 3, SEA_LEVEL_Y + 3);
            final Noise2D carvingHeightNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.15f).scaled(8, 14);

            double weight, height, carvingHeight, carvingCenter;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double caveWeight)
            {
                weight = Mth.clamp(info.normDistSq() * 1.3 - 0.1, 0d, 1d); // 0 = near center
                height = heightIn;
                carvingHeight = carvingHeightNoise.noise(x, z);
                carvingCenter = carvingCenterNoise.noise(x, z);

                final double minHeight = carvingCenter - carvingHeight; // The minimum height of the river base. Must keep the river below this value
                final double maxHeight = carvingCenter + carvingHeight; // The maximum height of the river tunnel. Any above-height must only occur above this value.

                if (caveWeight > 0.75) // Full cave carver
                {
                    return heightIn;
                }
                else if (caveWeight > 0.25) // Blended cave + exterior carver
                {
                    final double canyonMaxHeight = Math.min(55 + info.normDistSq() * 1.3 * 16, heightIn);
                    final double interiorHeight = caveWeight > 0.5 ?
                        Mth.map(caveWeight, 0.5, 0.75, Math.min(maxHeight, heightIn), heightIn) :
                        Math.min(heightIn, Mth.map(caveWeight, 0.25, 0.5, canyonMaxHeight, minHeight));
                    final double exteriorHeight = caveWeight > 0.5 ?
                        Mth.map(caveWeight, 0.5, 0.75, Math.min(canyonMaxHeight, heightIn), heightIn) :
                        canyonMaxHeight;

                    return height = Mth.lerp(weight, interiorHeight, exteriorHeight);
                }

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
