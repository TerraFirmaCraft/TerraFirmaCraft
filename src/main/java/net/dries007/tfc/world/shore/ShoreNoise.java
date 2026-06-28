/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.shore;

import net.minecraft.util.Mth;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.Noise3D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public final class ShoreNoise
{
    // Typical monoslope beach
    public static ShoreNoiseSampler sandyBeach(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            double sandHeight, weight;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                this.sandHeight = ShoreNoise.simpleBeach(seed, x, z, heightIn, landWeight, oceanWeight);
                this.weight = thisWeight;

                return sandHeight;
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {

                if (yIn <= sandHeight || weight > 0.5) return 0;

                final double heightMultiplier = Math.clamp((yIn - sandHeight) * 0.125, 0, 1);

                // Only carve with noise in near a biome edge, otherwise it's unnecessary
                return heightMultiplier * Mth.map(weight, 0, 0.5, 0.7, 0);
            }
        };
    }


    // Cliffs at a distance from the shore with vegetated shoreline below
    public static ShoreNoiseSampler setbackCliffs(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            final Noise2D bankNoise = new OpenSimplex2D(seed.seed()).spread(0.03).abs().scaled(-1, 8);
            final Noise3D cliffNoise = BiomeNoise.cliffNoise(seed);

            final double cliffBaseWeight = 0.25;
            final double cliffTopWeight = 0.45;

            double bankHeight;
            double landWeight;
            int x;
            int z;
            double yTop;

            double sandHeight;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                this.x = x;
                this.z = z;
                this.landWeight = landWeight;
                this.sandHeight = ShoreNoise.simpleBeachNoLandBlend(seed, x, z, heightIn, landWeight, oceanWeight);
                this.yTop = heightIn;
                final double tideLevelAtOcean = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z) - 4;

                final double shoreBankHeight = bankNoise.noise(x, z) + sandHeight;

                if (landWeight >= cliffBaseWeight)
                {
                    this.bankHeight = shoreBankHeight;
                    return heightIn;
                }

                if (oceanWeight > 0)
                {
                    this.bankHeight = Mth.clampedMap(oceanWeight, 0, 0.25, shoreBankHeight, tideLevelAtOcean);
                }
                else
                {
                    this.bankHeight = shoreBankHeight;
                }
                this.bankHeight = Mth.clampedMap(thisWeight + landWeight / 2, 0.5, 1, sandHeight, bankHeight);

                return bankHeight;
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {
                if (yIn <= bankHeight) return 0;
                if (landWeight >= cliffBaseWeight)
                {
                    double percentHeight = Mth.clampedMap(yIn, bankHeight, yTop, 0.30, 1);
                    double percentDepth = Mth.clampedMap(landWeight, cliffBaseWeight, cliffTopWeight, 0, 1);
                    double cliffNoiseValue = edgeFunction(percentHeight) * edgeFunction(percentDepth) * cliffNoise.noise(x, yIn, z);
                    return (percentHeight - cliffNoiseValue - percentDepth) * 3;
                }

                return 0.7;
            }

            public double edgeFunction(double input)
            {
                return Math.min(3 - 6 * Math.abs(input - 0.5), 1);
            }
        };
    }

    // Small sea dunes
    public static ShoreNoiseSampler dunes(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            final OpenSimplex2D warpNoise = new OpenSimplex2D(seed.seed()).scaled(-10, 10).spread(0.05);
            final Noise2D duneNoise = new OpenSimplex2D(seed.seed()).spread(0.03).abs().scaled(-2, 6.5).warped(warpNoise);

            double duneHeight;
            double sandHeight;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                this.sandHeight = ShoreNoise.simpleBeach(seed, x, z, heightIn, landWeight, oceanWeight);
                final double tideLevelAtOcean = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z) - 4;

                final double fullDuneHeight = duneNoise.noise(x, z) + sandHeight;
                if (oceanWeight > 0)
                {
                    this.duneHeight = Mth.clampedMap(oceanWeight, 0, 0.25, fullDuneHeight,tideLevelAtOcean);
                }
                else
                {
                    this.duneHeight = fullDuneHeight;
                }
                this.duneHeight = Mth.clampedMap(thisWeight, 0.5, 1, sandHeight, duneHeight);

                return duneHeight;
            }
        };
    }

    // Complex rocks and tidepools with sandy beaches
    public static ShoreNoiseSampler embayments(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            private final Noise2D rockShelfIntensity = new OpenSimplex2D(seed.seed() + 5252L).octaves(3).spread(0.03).scaled(-0.8, 1);
            private final Noise2D steepnessNoise = new OpenSimplex2D(seed.seed() + 224L).octaves(2).spread(0.11).clampedScaled(-0.7, 0.7, 0, 0.3);
            private final Noise2D roughnessNoise = new OpenSimplex2D(seed.seed()).octaves(3).spread(0.09).scaled(-3, 3);
            private final Noise2D tidepoolNoise = new OpenSimplex2D(seed.seed() + 492L).octaves(3).spread(0.09).clampedScaled(-1, 0, -5, 0);
            private final Noise3D caveNoise = BiomeNoise.cliffNoise(seed).scaled(0, 0.12);

            final double topShelfEdge = 0.7;
            final double midShelfEdge = 0.35;

            int x, z;

            double tideLevel;
            double oceanWeight;
            double sandHeight;
            double shelfProgress;
            double height;
            double topShelfHeight;
            double midShelfHeight;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                tideLevel = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);
                sandHeight = ShoreNoise.simpleBeach(tideLevel, heightIn, landWeight, oceanWeight);
                this.x = x;
                this.z = z;
                this.oceanWeight = oceanWeight;

                final double roughness = roughnessNoise.noise(x, z) + tidepoolNoise.noise(x, z);
                topShelfHeight = SEA_LEVEL_Y + roughness + 15;
                midShelfHeight = SEA_LEVEL_Y + roughness + 9;

                final double baseRockShelfNoise = rockShelfIntensity.noise(x, z);
                if (oceanWeight > 0.10)
                {
                    shelfProgress = Mth.clampedMap(oceanWeight, 0.1, 0.3, baseRockShelfNoise, baseRockShelfNoise - oceanWeight * 3);
                }
                else
                {
                    final double landInfluence = Mth.clampedMap(landWeight, 0.4, 0, baseRockShelfNoise + landWeight * 2, baseRockShelfNoise);
                    shelfProgress = Mth.clampedMap(oceanWeight, 0, 0.1, baseRockShelfNoise + landInfluence, baseRockShelfNoise);
                }

                final double steepness = steepnessNoise.noise(x, z);
                height = shelfProgress > topShelfEdge + steepness ? topShelfHeight
                    : shelfProgress > topShelfEdge ? Mth.map(shelfProgress, topShelfEdge, topShelfEdge + steepness, midShelfHeight, topShelfHeight)
                    : shelfProgress > midShelfEdge + steepness ? midShelfHeight
                    : shelfProgress > midShelfEdge ? Mth.map(shelfProgress, midShelfEdge, midShelfEdge + steepness, sandHeight, midShelfHeight)
                    : sandHeight;

                return height;
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {
                if (yIn <= sandHeight) return -0.6;

                if (height <= midShelfHeight)
                {
                    final double cliffCurveDepth = caveNoise.noise(x, yIn, z);
                    final double cliffProgress = widthFunction(true, yIn - tideLevel, midShelfHeight - tideLevel, midShelfEdge + cliffCurveDepth, midShelfEdge);
                    return 0.4 + 3 * (cliffProgress - shelfProgress);
                }

                if (yIn <= height) return 0;

                return 0.7;
            }
        };
    }

    // Complex rocks and tidepools
    public static ShoreNoiseSampler rockyShores(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            private final Noise2D rockShelfIntensity = new OpenSimplex2D(seed.seed() + 5252L).octaves(3).spread(0.03).scaled(-0.8, 1);
            private final Noise2D steepnessNoise = new OpenSimplex2D(seed.seed() + 224L).octaves(2).spread(0.11).clampedScaled(-0.7, 0.7, 0, 0.3);
            private final Noise2D roughnessNoise = new OpenSimplex2D(seed.seed()).octaves(3).spread(0.09).scaled(-3, 3);
            private final Noise2D tidepoolNoise = new OpenSimplex2D(seed.seed() + 492L).octaves(3).spread(0.09).clampedScaled(-1, 0, -5, 0);

            private final Cellular2D cellularNoise = new Cellular2D(seed.seed() + 323L).spread(0.022);
            private final Noise2D punchbowlCarvingNoise = (x, z) -> {
                Cellular2D.Cell cell = cellularNoise.cell(x, z);
                final double punchBowlRarity = 0.25;
                final double punchBowlDiameter = 0.5;
                if (cell.noise() > punchBowlRarity && cell.f2() >= punchBowlDiameter) return 1;
                return cell.f1();
            };
            private final Noise2D punchbowlSizeNoise = new OpenSimplex2D(seed.seed()).octaves(3).spread(0.06).clampedScaled(-0.7, 0.7, -0.05, 0.15);

            final double topShelfEdge = 0.7;
            final double midShelfEdge = 0.35;
            final double lowShelfEdge = 0.10;

            int x, z;

            double oceanWeight;
            double oceanEdgeHeight;
            double shelfProgress;
            double height;
            double topShelfHeight;
            double midShelfHeight;
            double lowShelfHeight;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                final double tideLevel = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);
                oceanEdgeHeight = tideLevel - 4;
                this.x = x;
                this.z = z;
                this.oceanWeight = oceanWeight;

                final double roughness = roughnessNoise.noise(x, z) + tidepoolNoise.noise(x, z);
                topShelfHeight = SEA_LEVEL_Y + roughness + 15;
                midShelfHeight = SEA_LEVEL_Y + roughness + 9;
                lowShelfHeight = SEA_LEVEL_Y + roughness + 3;

                final double baseRockShelfNoise = rockShelfIntensity.noise(x, z);
                if (oceanWeight > 0.10)
                {
                    shelfProgress = Mth.clampedMap(oceanWeight, 0.1, 0.3, baseRockShelfNoise, baseRockShelfNoise - oceanWeight * 3);
                }
                else
                {
                    final double landInfluence = Mth.clampedMap(landWeight, 0.4, 0, baseRockShelfNoise + landWeight * 2, baseRockShelfNoise);
                    shelfProgress = Mth.clampedMap(oceanWeight, 0, 0.1, baseRockShelfNoise + landInfluence, baseRockShelfNoise);
                }

                final double steepness = steepnessNoise.noise(x, z);
                height = shelfProgress > topShelfEdge + steepness ? topShelfHeight
                    : shelfProgress > topShelfEdge ? Mth.map(shelfProgress, topShelfEdge, topShelfEdge + steepness, midShelfHeight, topShelfHeight)
                    : shelfProgress > midShelfEdge + steepness ? midShelfHeight
                    : shelfProgress > midShelfEdge ? Mth.map(shelfProgress, midShelfEdge, midShelfEdge + steepness, lowShelfHeight, midShelfHeight)
                    : shelfProgress > lowShelfEdge - steepness ? lowShelfHeight
                    : oceanEdgeHeight;

                return height;
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {
                if (yIn <= oceanEdgeHeight) return 0;
                if (yIn >= height) return 0.7;

                final double cliffProgress;


                final double edgeIntensity;
                final double punchbowlRadius = punchbowlCarvingNoise.noise(x, z);
                double returnNoise = 0;
                if (height <= lowShelfHeight)
                {
                    cliffProgress = widthFunction(true, yIn - oceanEdgeHeight, lowShelfHeight - oceanEdgeHeight, lowShelfEdge + 0.11, lowShelfEdge);
                    returnNoise = 0.4 + 3 * (cliffProgress - shelfProgress);
                }

                if (punchbowlRadius < 0.25)
                {
                    final double intensityShift = punchbowlSizeNoise.noise(x, z);
                    edgeIntensity = widthFunction(true, yIn - oceanEdgeHeight, topShelfHeight - oceanEdgeHeight, 0.25 - intensityShift, 0.12 - intensityShift);
                    returnNoise = Math.max(returnNoise, 3 * (edgeIntensity - punchbowlRadius));
                }

                return returnNoise;
            }
        };
    }

    // Mirrors functionality of 1.20 coasts
    public static ShoreNoiseSampler classic(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            final Noise2D shoreNoise = new OpenSimplex2D(seed.seed() + 8719234132L).octaves(2).spread(0.003f).scaled(-0.1, 1.1);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                // First, calculate cliff "influence" factor (between 0 = no cliffs, 1.0 = full cliffs)
                // This is computed from a global influence noise, plus a factor from the initial height - higher areas have larger cliff influence
                final int cliffHeightAdjustment = biome.getShoreBaseHeight();

                final double cliffInfluence = Mth.clamp(
                    shoreNoise.noise(x, z) + Mth.map(heightIn, cliffHeightAdjustment, cliffHeightAdjustment + 20, 0, 0.6),
                    0.0, 1.0
                );
                final double adjustedCliffInfluence = 1.0 - (1.0 - cliffInfluence) * (1.0 - cliffInfluence);

                // Then, calculate the re-weighted shore and normal biome height
                final double x2 = Mth.lerp(adjustedCliffInfluence, 0.8, 0.515);
                final double y2 = 1.15 - 0.3 * x2;

                // Adjust shore weight based on a piecewise function that creates a sharper cliff, then a smoother flatter area
                final double adjustedShoreWeight = shoreWeight < x2
                    ? Mth.map(shoreWeight, 0.5, x2, 0.5, y2) // Cliff from [0.5, x2] -> rapidly increase shore weight
                    : Mth.map(shoreWeight, x2, 1.0, y2, 1.0); // From [x2, 1.0], interpolate high shore weight, creates flatter area

                final double normalWeight = 1.0 - shoreWeight;
                final double adjustedNormalWeight = 1.0 - adjustedShoreWeight;

                // Calculate the adjusted height, using this re-weighting
                // Only apply if we are above the cliff base height (sea level by default), by taking a max here
                final double adjustedHeight = Math.max(
                    (adjustedShoreWeight / shoreWeight) * shoreHeight + (adjustedNormalWeight / normalWeight) * normalHeight,
                    cliffHeightAdjustment
                );

                if (adjustedHeight < heightIn)
                {
                    heightIn = adjustedHeight;
                }
                return heightIn;
            }
        };
    }

    // Sea Stacks and arches
    public static ShoreNoiseSampler seaStacks(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            final double minStackDensity = 0.85;
            final double noiseScale = 3;

            final Cellular2D cellularNoise = new Cellular2D(seed.seed() + 323L).spread(0.05);

            final Noise2D seaStackDistributionNoise = new OpenSimplex2D(seed.seed() + 5424L).octaves(2).map(y -> 1 - Math.abs(y)).spread(0.015);
            final Noise2D f2MinusF1Noise = (x, z) -> {
                Cellular2D.Cell cell = cellularNoise.cell(x, z);
                final double f1 = cell.f1();
                final double f2 = cell.f2();
                return (f1 > 0 ? (f2 - f1) : 1);
            };
            final Noise2D f1Noise = (x, z) -> {
                Cellular2D.Cell cell = cellularNoise.cell(x, z);
                final double centerX = cell.x();
                final double centerZ = cell.y();
                final double stackDensity = seaStackDistributionNoise.noise(centerX, centerZ);
                if (stackDensity < minStackDensity) return noiseScale;
                return cell.f1();
            };

            final Noise3D cliffNoise = BiomeNoise.cliffNoise(seed);

            private double stackNoiseValue;
            private double sandHeight;
            private double landWeight;
            private double tideLevel;
            private double oceanWeightFactor;

            private int x, z;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                this.x = x;
                this.z = z;
                this.landWeight = landWeight;
                this.oceanWeightFactor = Mth.clampedMap(oceanWeight, 0.1, 0.25, 1, 0);

                this.tideLevel = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);
                final double typicalBeachSandHeight = ShoreNoise.simpleBeach(tideLevel, heightIn, landWeight, oceanWeight);
                // High-tide variants of this biome should completely swallow this beach
                this.sandHeight = Mth.clampedMap(thisWeight, 0.5, 0.8, typicalBeachSandHeight, Math.min(typicalBeachSandHeight, tideLevel - 1));

                final double f2MinusF1 = f2MinusF1Noise.noise(x, z);

                final double outputMin = Mth.clampedMap(seaStackDistributionNoise.noise(x, z), 0.2, 0.6, 3, 1);
                this.stackNoiseValue = f1Noise.noise(x, z) * Mth.clampedMap(Math.abs(f2MinusF1), 0, 0.25, outputMin, 1);

                return Mth.clampedMap(oceanWeight, 0, 0.5, heightIn, shoreHeight / shoreWeight) ;
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {
                if (yIn <= sandHeight) return 0;

                final double overhangHeight = SEA_LEVEL_Y + 14;
                final double stackBaseHeight = tideLevel + 1;

                double y = yIn - stackBaseHeight;

                final double cliffNoiseModifier = 0.04 * Math.abs(cliffNoise.noise(x, y, z));
                final double stackMinWidth = (0.06 + cliffNoiseModifier) * oceanWeightFactor;
                final double stackMaxWidth = (0.18 + cliffNoiseModifier) * oceanWeightFactor;
                final double cliffBorderTopWeight = 0.22 + cliffNoiseModifier;
                final double cliffBorderBaseWeight = 0.26 + cliffNoiseModifier;

                final double height = overhangHeight - stackBaseHeight;

                final double stackWidth = widthFunction(false, y, height, stackMinWidth, stackMaxWidth);
                final double stackOutput = Math.clamp((stackNoiseValue - stackWidth) * 10, 0, 1) * Mth.clampedMap(yIn, stackBaseHeight, overhangHeight, noiseScale, 0.75);

                // landWeight where cliff top edges form
                if (landWeight >= cliffBorderTopWeight)
                {
                    final double cliffBorderWeight = widthFunction(true, y, height, cliffBorderBaseWeight, cliffBorderTopWeight);
                    return Math.min(Math.clamp((cliffBorderWeight - landWeight) * 10, 0, 1) * noiseScale, stackOutput);
                }
                else
                {
                    return stackOutput;
                }
            }
        };
    }

    // Terraced coastline, upper
    public static ShoreNoiseSampler upperTerrace(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            final double noiseScale = 3;

            final Noise3D cliffNoise = BiomeNoise.cliffNoise(seed);
            final Noise2D lowerTerraceNoise = BiomeNoise.lowerTerraceNoise(seed);
            final Noise2D upperTerraceNoise = BiomeNoise.upperTerraceNoise(seed);

            private double landWeight, oceanWeight, sandHeight;

            private int x, z;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                this.x = x;
                this.z = z;
                this.landWeight = landWeight;
                this.oceanWeight = oceanWeight;
                this.sandHeight = ShoreNoise.simpleBeach(seed, x, z, heightIn, landWeight, oceanWeight);

                return upperTerraceNoise.noise(x, z);
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {
                final double lowerHeight = lowerTerraceNoise.noise(x, z);
                if (yIn >= lowerHeight)
                {
                    final double overhangHeight = SEA_LEVEL_Y + 25;
                    final double cliffBaseHeight = SEA_LEVEL_Y + 11;

                    final double y = yIn - cliffBaseHeight;
                    final double cliffNoiseModifier = 0.04 * Math.abs(cliffNoise.noise(x, y, z));
                    final double cliffBorderTopWeight = 0.32 + cliffNoiseModifier;
                    final double cliffBorderBaseWeight = 0.36 + cliffNoiseModifier;

                    final double height = overhangHeight - cliffBaseHeight;

                    // landWeight where cliff top edges form
                    if (landWeight >= cliffBorderTopWeight)
                    {
                        final double cliffBorderWeight = widthFunction(true, y, height, cliffBorderBaseWeight, cliffBorderTopWeight);
                        return Math.clamp((cliffBorderWeight - landWeight) * 10, 0, 1) * noiseScale;
                    }
                    else
                    {
                        return Mth.clampedMap(yIn, lowerHeight, lowerHeight + 6, 0, noiseScale);
                    }
                }
                else
                {
                    // Fall back to lower terrace noise if in the right area
                    if (yIn <= sandHeight) return 0;

                    final double overhangHeight = SEA_LEVEL_Y + 11;
                    final double cliffBaseHeight = SEA_LEVEL_Y - 4;

                    double y = yIn - cliffBaseHeight;

                    final double cliffNoiseModifier = 0.12 * Math.abs(cliffNoise.noise(x, y, z));
                    final double cliffBorderTopWeight = 0.26 - cliffNoiseModifier;
                    final double cliffBorderBaseWeight = 0.22 - cliffNoiseModifier;

                    final double height = overhangHeight - cliffBaseHeight;

                    // landWeight where cliff top edges form
                    if (oceanWeight >= cliffBorderBaseWeight)
                    {
                        final double cliffBorderWeight = widthFunction(false, y, height, cliffBorderBaseWeight, cliffBorderTopWeight);
                        return Math.clamp((oceanWeight - cliffBorderWeight) * 20, 0, 1) * noiseScale;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        };
    }

    // Terraced coastline, lower
    public static ShoreNoiseSampler lowerTerrace(Seed seed)
    {
        return new ShoreNoiseSampler()
        {
            final double noiseScale = 3;

            final Noise3D cliffNoise = BiomeNoise.cliffNoise(seed);
            final Noise2D lowerTerraceNoise = BiomeNoise.lowerTerraceNoise(seed);

            private double oceanWeight;
            private double lowerTerraceHeight;
            private double sandHeight;

            private int x, z;

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, double oceanWeight, double landWeight, double shoreWeight, double thisWeight, BiomeExtension biome, double shoreHeight, double normalHeight)
            {
                this.x = x;
                this.z = z;
                this.oceanWeight = oceanWeight;
                this.lowerTerraceHeight = lowerTerraceNoise.noise(x, z);
                this.sandHeight = ShoreNoise.simpleBeach(seed, x, z, heightIn, landWeight, oceanWeight);

                return lowerTerraceHeight;
            }

            @Override
            public double noise(int yIn, double noiseIn)
            {
                if (yIn <= sandHeight) return 0;

                final double overhangHeight = SEA_LEVEL_Y + 11;
                final double cliffBaseHeight = SEA_LEVEL_Y - 4;

                double y = yIn - cliffBaseHeight;

                final double cliffNoiseModifier = 0.12 * Math.abs(cliffNoise.noise(x, y, z));
                final double cliffBorderTopWeight = 0.26 - cliffNoiseModifier;
                final double cliffBorderBaseWeight = 0.22 - cliffNoiseModifier;

                final double height = overhangHeight - cliffBaseHeight;

                // landWeight where cliff top edges form
                if (oceanWeight >= cliffBorderBaseWeight)
                {
                    final double cliffBorderWeight = widthFunction(false, y, height, cliffBorderBaseWeight, cliffBorderTopWeight);
                    return Math.clamp((oceanWeight - cliffBorderWeight) * 20, 0, 1) * noiseScale;
                }
                else
                {
                    return 0;
                }
            }

        };
    }

    // Helper functions

    private static double widthFunction(boolean inverted, double y, double height, double baseWidth, double topWidth)
    {
        final double curve = baseWidth + (y * y / (height * height)) * (topWidth - baseWidth);
        if (inverted) return Math.max(curve, topWidth);
        return Math.min(curve, topWidth);
    }

    private static double simpleBeach(Seed seed, int x, int z, double heightIn, double landWeight, double oceanWeight)
    {
        final double tideLevel = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);
        return simpleBeach(tideLevel, heightIn, landWeight, oceanWeight);
    }

    private static double simpleBeach(double tideLevel, double heightIn, double landWeight, double oceanWeight)
    {

        // Basic heights that the shore height will approach at edges with other biomes
        final double simpleShoreLandHeight = tideLevel + 2;
        final double simpleShoreSelfHeight = tideLevel - 2;
        final double simpleShoreOceanHeight = tideLevel - 4;

        if (oceanWeight > 0.10)
        {
            return Mth.clampedMap(oceanWeight, 0.1, 0.25, simpleShoreSelfHeight, simpleShoreOceanHeight);
        }
        else
        {
            final double landDerivedHeight = landWeight < 0.3
                ? Mth.map(landWeight, 0.3, 0, Math.min(heightIn, simpleShoreLandHeight), simpleShoreSelfHeight)
                : Mth.clampedMap(landWeight, 0.3, 0.5, Math.min(heightIn, simpleShoreLandHeight), heightIn);
            return Mth.clampedMap(oceanWeight, 0, 0.1, landDerivedHeight, simpleShoreSelfHeight);
        }
    }

    private static double simpleBeachNoLandBlend(Seed seed, int x, int z, double heightIn, double landWeight, double oceanWeight)
    {
        final double tideLevel = BiomeNoise.shoreTideLevelNoise(seed).noise(x, z);

        // Basic heights that the shore height will approach at edges with other biomes
        final double simpleShoreSelfHeight = tideLevel - 1;
        final double simpleShoreOceanHeight = tideLevel - 4;

        return Mth.clampedMap(oceanWeight, 0, 0.5, simpleShoreSelfHeight, simpleShoreOceanHeight);
    }
}
