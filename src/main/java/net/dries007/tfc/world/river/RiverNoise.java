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
import net.dries007.tfc.world.region.RiverEdge;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public final class RiverNoise
{
    // land near the edges of rivers, but not guaranteed away from edge
    public static RiverNoiseSampler banked(Seed seed)
    {
        return new RiverNoiseSampler()
        {
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(3).spread(0.05f).scaled(-0.2f, 0.2f);
            final Noise2D bankCutNoise = new OpenSimplex2D(seed.next()).octaves(3).abs().spread(0.025).scaled(0, 1, SEA_LEVEL_Y - 4, SEA_LEVEL_Y + 30);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight = 57 + (distFac < 1.0 ? distFac * 6 : 6);

                final double heightInWeight = Mth.clamp(distFac - 1, 0, 2);
                final double riverWeight = 2 - heightInWeight;

                return height = Math.min((heightIn * heightInWeight + riverHeight * riverWeight) / 2, bankCutNoise.noise(x, z));
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    // high banks near the edges of rivers that decrease away from edge
    public static RiverNoiseSampler tallBanked(Seed seed)
    {
        return new RiverNoiseSampler()
        {
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(3).spread(0.05f).scaled(-0.2f, 0.2f);
            final Noise2D surfaceNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.07).scaled(-2, 2);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight = 57 + (distFac < 1.0 ? distFac * 9 : 9) + surfaceNoise.noise(x, z);

                final double heightInWeight = Mth.clamp(distFac - 1, 0, 2);
                final double riverWeight = 2 - heightInWeight;

                return height = (heightIn * heightInWeight + riverHeight * riverWeight) / 2;
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    // wide, flat shores with slopes far from shore
    public static RiverNoiseSampler floodplain(Seed seed)
    {
        return new RiverNoiseSampler()
        {
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.2f, 0.2f);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight;
                if (distFac < 1)
                {
                    final double depth = Mth.clampedMap(info.widthSq(), RiverEdge.MIN_WIDTH * RiverEdge.MIN_WIDTH, RiverEdge.MAX_WIDTH * RiverEdge.MAX_WIDTH, 3, 6);
                    riverHeight = SEA_LEVEL_Y - 2 - depth + distFac * depth;
                }
                else if (distFac < 2.0)
                {
                    riverHeight = SEA_LEVEL_Y - 2;
                }
                else
                {
                    final double heightInWeight = Mth.clamp(2 * distFac - 4, 0, 1);
                    final double riverWeight = 1 - heightInWeight;
                    riverHeight = (SEA_LEVEL_Y - 1.5) * riverWeight + heightIn * heightInWeight;
                }

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                // For this blend type, we limit to where the river is reasonably deep to avoid wide, flat caves
                if (height < SEA_LEVEL_Y - 2.5 && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    // ~45 degree slopes
    public static RiverNoiseSampler wide(Seed seed)
    {
        return new RiverNoiseSampler()
        {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-2.5f, 1.5f);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.15f, 0.15f);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight = 58 + distFac * 7 + baseNoise.noise(x, z);

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    // A copy of wide, but a little deeper to help with shore blending
    public static RiverNoiseSampler wideDeep(Seed seed)
    {
        return new RiverNoiseSampler()
        {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-2.5f, 1.5f);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.15f, 0.15f);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 0.8f + distNoise.noise(x, z);
                final double riverHeight = 55 + distFac * 7 + baseNoise.noise(x, z);

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    public static RiverNoiseSampler canyon(Seed seed)
    {
        return new RiverNoiseSampler()
        {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-7, 3);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.3f, 0.2f);
            final Noise2D lowFreqCliffNoise = new OpenSimplex2D(seed.next()).spread(0.0007f).clamped(0, 1);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 1.3 + distNoise.noise(x, z);
                final double adjDistFac = distFac > 0.6 ? distFac * 0.4 + 0.8 : distFac;

                final double riverHeight = 55 + Mth.lerp(lowFreqCliffNoise.noise(x, z), distFac, adjDistFac) * 16 + baseNoise.noise(x, z);

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    public static RiverNoiseSampler tallCanyon(Seed seed)
    {
        return new RiverNoiseSampler()
        {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-7, 3);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.3f, 0.2f);
            final Noise3D cliffNoise = new OpenSimplex3D(seed.next()).octaves(2).spread(0.1f).scaled(0, 3);

            double height;
            private double distFac; // 0 ~ center of river, 1 ~ distant from river
            private int x, z;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = info.normDistSq() * 1.3 + distNoise.noise(x, z);
                final double adjDistFac = distFac > 0.32 ? distFac * 0.2 + 1.6 : distFac;

                final double riverHeight = 55 + adjDistFac * 16 + baseNoise.noise(x, z);

                // Carve more aggressively in wider rivers to reduce floating terrain
                final double widthFactor = Mth.clampedMap(info.widthSq(), 144, 324, 0.7, 1.1);

                this.distFac = Math.max(0, distFac * widthFactor);
                this.x = x;
                this.z = z;

                height = Math.min(riverHeight, heightIn);
                return height;
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
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

    // Vertical cliff with 45 degree slopes above and below
    public static RiverNoiseSampler talus(Seed seed)
    {
        return new RiverNoiseSampler()
        {

            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-2.5f, 1.5f);
            final Noise2D cliffHeightNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.1f).scaled(3f, 8f);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.15f, 0.15f);

            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = Math.sqrt(info.normDistSq()) + distNoise.noise(x, z);
                final double talusRiverHeight = 55 + distFac * 12 + baseNoise.noise(x, z) + (distFac > 1.5 ? cliffHeightNoise.noise(x, z) : 0);
                final double canyonRiverHeight = 55 + info.normDistSq() * 1.3 * 16;

                // Use noise similar to tall canyon at edges of talus biomes to avoid artifacts with other river noise functions
                final double riverHeight = Mth.clampedMap(thisWeight, 0.9, 1, canyonRiverHeight, talusRiverHeight);

                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    // Rows of vertical cliffs
    public static RiverNoiseSampler terraces(Seed seed)
    {
        return new RiverNoiseSampler()
        {
            final Noise2D baseNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-2.5f, 1.5f);
            final Noise2D cliffHeightNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.1f).scaled(4f, 8f);
            final Noise2D cliffBaseNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.06f).scaled(SEA_LEVEL_Y - 2, SEA_LEVEL_Y + 4);
            final Noise2D distNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.05f).scaled(-0.15f, 0.15f);
            double height;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                final double distFac = Math.sqrt(info.normDistSq()) * 0.85 + distNoise.noise(x, z);
                final double slopedRiverHeight = 54 + distFac * 12 + baseNoise.noise(x, z);
                final double cliffBaseHeight = cliffBaseNoise.noise(x, z);
                final double cliffHeight = cliffHeightNoise.noise(x, z);
                // A smaller set of cliffs nearer the water
                final double lowerTerrace = slopedRiverHeight > cliffBaseHeight ? Mth.clampedMap(slopedRiverHeight, cliffBaseHeight, cliffBaseHeight + 1.1, 0, cliffHeight) : 0;
                // A larger set of cliffs up higher
                final double upperTerrace = slopedRiverHeight > cliffBaseHeight + cliffHeight ? Mth.clampedMap(slopedRiverHeight, cliffBaseHeight + cliffHeight, cliffBaseHeight + cliffHeight + 1.4, 0, cliffHeight + 4) : 0;
                final double terraceRiverHeight = slopedRiverHeight + lowerTerrace + upperTerrace;
                final double canyonRiverHeight = 55 + info.normDistSq() * 1.3 * 16;

                // Use noise similar to tall canyon at edges of terrace biomes to avoid artifacts with other river noise functions
                final double riverHeight = Mth.clampedMap(thisWeight, 0.9, 1, canyonRiverHeight, terraceRiverHeight);
                return height = Math.min(riverHeight, heightIn);
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // Note that the height here is not the final terrain height, but rather the terrain height at the time river noise is being calculated
                // This if statement will carve a cave roof that is symmetrical to the river floor, and always tall enough to boat through
                if (height < SEA_LEVEL_Y && y > height && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - height))
                {
                    return 1;
                }
                return y > height ? 0 : noiseIn;
            }
        };
    }

    public static RiverNoiseSampler cave(Seed seed)
    {
        return new RiverNoiseSampler()
        {

            final Noise2D carvingCenterNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.02f).scaled(SEA_LEVEL_Y - 3, SEA_LEVEL_Y + 3);
            final Noise2D carvingHeightNoise = new OpenSimplex2D(seed.next()).octaves(4).spread(0.15f).scaled(8, 14);

            double distSquared, weight, height, carvingHeight, carvingCenter, canyonHeight;

            @Override
            public double setColumnAndSampleHeight(RiverInfo info, int x, int z, double heightIn, double thisWeight)
            {
                distSquared = Mth.clamp(info.normDistSq() * 1.3 - 0.1, 0d, 1d); // 0 = near center
                weight = thisWeight;
                height = heightIn;
                carvingHeight = carvingHeightNoise.noise(x, z);
                carvingCenter = carvingCenterNoise.noise(x, z);
                // The height returned by the tall canyon noise function
                canyonHeight = 55 + info.normDistSq() * 1.3 * 16;

                final double maxHeight = carvingCenter + carvingHeight; // The maximum height of the river tunnel. Any surface height above a cave must only occur above this value.

                if (thisWeight > 0.75) // Full cave carver
                {
                    // Return the normal terrain height as river is fully subterranean
                    return heightIn;
                }
                else
                {
                    final double canyonMaxHeight = Math.min(canyonHeight, heightIn);
                    if (thisWeight > 0.5) // Blended cave + exterior carver
                    {
                        final double interiorHeight = Mth.map(thisWeight, 0.5, 0.75, Math.min(maxHeight, heightIn), heightIn);

                        final double exteriorHeight = Mth.map(thisWeight, 0.5, 0.75, Math.min(canyonMaxHeight, heightIn), heightIn);

                        return height = Mth.lerp(distSquared, interiorHeight, exteriorHeight);
                    }
                    else
                    {
                        return canyonMaxHeight;
                    }
                }
            }

            @Override
            public double noise(int y, double noiseIn)
            {
                // This if statement will carve a cave roof that is symmetrical to the river floor, ensuring a minimum profile to boat through
                // Really only needed to deal with volcanoes that occur right at a cave-carver transition
                if (canyonHeight < SEA_LEVEL_Y && y > canyonHeight && y < SEA_LEVEL_Y + 2 + (SEA_LEVEL_Y - canyonHeight))
                {
                    return 1;
                }

                double vertDistance = (y - carvingCenter) / carvingHeight;
                // Create a cave mouth
                if (vertDistance > 0)
                {
                    vertDistance = vertDistance * weight * weight;
                }
                final double columnNoise = Math.max(1 - (vertDistance * vertDistance), 0);
                final double noise = Mth.lerp(distSquared, columnNoise, noiseIn);

                return noise * Mth.clampedMap(weight, 0.5, 0.25, 1, 0);
            }
        };
    }
}
