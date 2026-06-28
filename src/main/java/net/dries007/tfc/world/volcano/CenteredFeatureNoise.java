/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.ChunkHeightFiller;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.biome.BiomeSourceExtension;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class CenteredFeatureNoise
{
    public static final double STRATOVOLCANO_SPREAD_FACTOR = 0.0021f;

    public static CenteredFeatureNoiseSampler cinder(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed()).spread(0.009f);
            final Noise2D jitterNoise = new OpenSimplex2D(seed.seed() + 8179234123L).octaves(2).scaled(-0.0016f, 0.0016f).spread(0.128f);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final int xC = (int) cell.x();
                final int zC = (int) cell.y();
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock(xC), QuartPos.fromBlock(zC));
                if (biome.hasCinderCones())
                {
                    final float rarity = biome.getCenteredFeatureFrequency();
                    if (checkCellRarity(cell, rarity))
                    {
                        final int baseHeight;
                        if (biome == TFCBiomes.ACTIVE_SHIELD_VOLCANO)
                        {
                            baseHeight = (int) BiomeNoise.activeShieldVolcano(seed.seed(), BiomeNoise.activeHotSpots(seed.seed())).noise(xC, zC) - 11;
                        }
                        else if (biome == TFCBiomes.RIFT_VALLEY)
                        {
                            baseHeight = (int) BiomeNoise.riftValley(seed.seed(), 2, 30, false).noise(xC, zC) - 11;
                        }
                        else
                        {
                            baseHeight = SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight();
                        }
                        return modifyHeight(cell, x, z, baseHeight, biome.getCenteredFeatureScaleHeight(), heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            /**
             * Calculate the closeness value to a volcano, in the range [0, 1]. 1 = Center of a volcano, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, int baseHeight, int scaleHeight, double heightIn)
            {
                final double f1 = cell.f1();
                final double easing = Mth.clamp(calculateEasing((float) f1) + jitterNoise.noise(x, z), 0, 1);
                final double rim = 0.025;
                final double shape = calculateShape(1 - easing, rim);
                final double volcanoAdditionalHeight = shape * scaleHeight;
                final double volcanoHeight = baseHeight + volcanoAdditionalHeight;
                final double weight = 10f * Mth.clamp((float) cell.f2() - f1, 0f, 0.1f);
                final double adjEasing = easing * weight;
                if (adjEasing > 1 - rim)
                {
                    return volcanoHeight;
                }
                else
                {
                    return Mth.map(adjEasing, 0, 1 - rim, heightIn, volcanoHeight);
                }
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            /**
             * @param t The unscaled square distance from the volcano, roughly in [0, 1.2]
             * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
             */
            private static double calculateShape(double t, double rim)
            {
                if (t > rim)
                {
                    return (5 / (9 * t + 1) - 0.5) * 0.279173646008;
                }
                else
                {
                    double a = (t * 9 + 0.05);
                    return (8 * a * a + 2.97663265306) * 0.279173646008;
                }
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasCinderCones();
            }

            public double maxSafeDiameter(Cellular2D.Cell cell, int x, int z)
            {
                // This step is necessary because the exact center of a cell is not aligned with the exact center of a block
                // Which causes the sampled position to be on one side of the center, and potentially closer to a different cell
                final int deltaX = x - (int) cell.x();
                final int deltaZ = z - (int) cell.y();
                return cellNoise.cell(Math.signum(deltaX), Math.signum(deltaZ)).f2();
            }

            /**
             * Calculate the center of the nearest volcano, if one exists, to the given x, z, at the given y.
             */
            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency) & maxSafeDiameter(cell, x, z) > 0.46)
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }

            @Override
            public Cellular2D getCellularNoise()
            {
                return cellNoise;
            }

            @Override
            public @Nullable VolcanoVariant getVolcanoVariant(Cellular2D.Cell cell)
            {
                return null;
            }
        };
    }

    public static CenteredFeatureNoiseSampler tuffRing(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 0.2f, 1).spread(0.003f);
            final Noise2D jitterNoise = new OpenSimplex2D(seed.seed() + 1234123L).octaves(2).scaled(-0.032f, 0.032f).spread(0.064f);
            final Noise2D addedCliffNoise = new OpenSimplex2D(seed.seed()).octaves(2).spread(0.1).scaled(-2, 10);
            final Noise2D everywhereNoise = new OpenSimplex2D(seed.next()).octaves(3).spread(0.03).scaled(-10, 10);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                if (biome.hasTuffRings())
                {
                    final float rarity = biome.getCenteredFeatureFrequency();
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                if (cell != null)
                {
                    final double f1 = cell.f1();
                    final double easing = Mth.clamp(calculateEasing((float) f1) + jitterNoise.noise(x, z), 0, 1);
                    final double shape = calculateShape(1 - easing);
                    final double ringAdditionalHeight = (shape * biome.getCenteredFeatureScaleHeight() + (shape > 0.5 ? addedCliffNoise.noise(x, z) : 0f));
                    final double gapAdjustedAdditionalHeight = Math.min(ringAdditionalHeight * getGapVerticalEasing(cell), ringAdditionalHeight);
                    final double ringHeight = SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + gapAdjustedAdditionalHeight + everywhereNoise.noise(x, z);

                    // Abruptly scales down to the baseHeight near the edges of cells
                    final double delta = 25 * Mth.clamp(cell.f2() - f1, 0, 0.04);
                    return Mth.lerp(delta, heightIn, Math.max(ringHeight, heightIn));
                }
                return heightIn;
            }

            /**
             * Calculate the closeness value to a tuff ring center, in the range [0, 1]. 1 = Center, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            /**
             * @param t The unscaled square distance from the tuff ring center, roughly in [0, 1.2]
             * @return A noise function determining the tuff ring's height at any given position, in the range [0, 1]
             */
            private static double calculateShape(double t)
            {
                return t < 0.03f ? 0 // Flat
                    : t < 0.10f ? t * 7f - 0.21f // Slope up to 0.5
                    : t < 0.15f ? t * 6.6667f // Cliff, 0.67 slope to 1.0
                    : t < 0.20f ? 1 - (t - 0.15f) * 6.6667f // Ridge, slope 1.0 down to 0.67
                    : 1.5f - (5 * t); // Cliff base, slope 0.5 down
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasTuffRings();
            }

            /**
             * Calculate the center of the nearest tuff ring, if one exists, to the given x, z, at the given y.
             */
            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }

            @Override
            public Cellular2D getCellularNoise()
            {
                return cellNoise;
            }

            @Override
            public @Nullable VolcanoVariant getVolcanoVariant(Cellular2D.Cell cell)
            {
                return null;
            }
        };
    }

    /**
     * Method for adding a single gap to a cellular feature at a random angle
     *
     * @return a value ranging from 1 far from the gap, to 0 in the middle of a large gap
     */
    private static double getGapVerticalEasing(Cellular2D.Cell cell)
    {
        // Gap size from 0 to 2
        final double gapSize = 2 * Helpers.hashDouble(cell.noise(), 112);

        // Only adjust this if a gap should exist at all
        double gapVerticalEasing = 1;
        if (gapSize > 0)
        {
            final double aGap = 4 * Helpers.hashDouble(cell.noise(), 113);
            final double a1 = cell.angle();
            double angleToGap = Math.abs(a1 - aGap);
            angleToGap = Math.min(angleToGap, 4 - angleToGap);
            // If angle to gap is larger than the gap size, we are far from the gap and can skip calculations
            if (angleToGap < gapSize)
            {
                final double angleToGapEdge = Math.abs(Math.min(angleToGap + gapSize, angleToGap - gapSize));

                // Gap scale should be lowest at the center of the gap
                gapVerticalEasing = Mth.clampedMap(angleToGapEdge, 0, Math.max(0.25, 0.5 * gapSize), 1, 0);
            }
        }
        return gapVerticalEasing;
    }

    public static CenteredFeatureNoiseSampler atolls(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final double minThickness = -0.3, maxThickness = 0.4, thicknessAmplitude = maxThickness - minThickness;
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 0.5f, 2).spread(0.0023f);
            final Noise2D heightNoise = new OpenSimplex2D(seed.seed()).octaves(4).spread(0.03f).scaled(SEA_LEVEL_Y - 3, SEA_LEVEL_Y + 7);
            final Noise2D rimWarpNoise = new OpenSimplex2D(seed.seed() + 1431L).octaves(3).scaled(0f, 0.3f).spread(0.029f);
            final Noise2D unscaledThicknessNoise = new OpenSimplex2D(seed.seed() + 131L).octaves(3).spread(0.02f);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                if (biome.hasAtolls())
                {
                    final float rarity = biome.getCenteredFeatureFrequency();
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                if (cell != null)
                {
                    final double integrity = getAtollIntegrity(cell);
                    final double unclampedThickness = Mth.map(unscaledThicknessNoise.noise(x, z), -1, 1, minThickness, maxThickness);
                    final double easing = calculateEasing(cell, x, z);
                    final double lagoonHeight = 0.8 + 0.12 * Helpers.hashDouble(cell.noise(), 15413);
                    final double reefHeight = reefShape(easing, lagoonHeight, unclampedThickness, integrity);

                    double maxHeight = heightNoise.noise(x, z);

                    if (integrity < 1)
                    {
                        maxHeight = Mth.clampedMap(unscaledThicknessNoise.noise(x, z) + integrity, -1, 1, lagoonHeight * maxHeight, maxHeight);
                    }
                    final double atollHeight = Mth.map(reefHeight, 0, 1, SEA_LEVEL_Y - 60, maxHeight);

                    return Math.max(heightIn, atollHeight);
                }
                return heightIn;
            }

            private double reefShape(double easing, double lagoonDepth, double unclampedThickness, double integrity)
            {
                final double edgeDist = 0.47;
                final double beachDist = edgeDist + 0.11;
                final double clampedThickness = Math.clamp(unclampedThickness, 0, 0.3);
                final double lagoonDist = beachDist + 0.19 + clampedThickness;
                final double baseShape;
                if (easing < edgeDist)
                {
                    // Ocean floor to edge of beach
                    // Return immediately, integrity has no effect here
                    return Mth.map(easing, 0, edgeDist, 0, lagoonDepth);
                }
                else if (easing < beachDist)
                {
                    // Shallow sloping beach to crest
                    baseShape = Mth.map(easing, edgeDist, beachDist, lagoonDepth, 1);
                }
                else
                {
                    // Shallow beach into lagoon
                    baseShape = Mth.clampedMap(easing, beachDist, lagoonDist, 1, lagoonDepth);
                }
                if (integrity >= 1)
                {
                    return baseShape;
                }
                else
                {
                    return Mth.clampedMap(unclampedThickness, minThickness - thicknessAmplitude * integrity, maxThickness - thicknessAmplitude * integrity, lagoonDepth, baseShape);
                }
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasAtolls();
            }

            @Override
            public float calculateEasing(int x, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return calculateEasing(cell, x, z);
                }
                return 0;
            }

            private float calculateEasing(Cellular2D.Cell cell, int x, int z)
            {
                final double f1 = cell.f1(), f2 = cell.f2(), f2f1 = f2 - f1;
                final float easing = (float) (Math.sqrt(cell.f2()) - Math.sqrt(cell.f1()) + rimWarpNoise.noise(x, z));
                if (f2f1 > 0.18) // Ensure no *extremely* sharp edges at cell edges
                {
                    return easing;
                }
                else
                {
                    return easing * (float) Mth.map(f2f1, 0, 0.18, 0, 1);
                }
            }

            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }

            @Override
            public Cellular2D getCellularNoise()
            {
                return cellNoise;
            }

            @Override
            public @Nullable VolcanoVariant getVolcanoVariant(Cellular2D.Cell cell)
            {
                return null;
            }
        };
    }

    public static double getAtollIntegrity(Cellular2D.Cell cell)
    {
        return Math.min(0.4 + Helpers.hashDouble(cell.noise(), 523), 1);
    }

    public static CenteredFeatureNoiseSampler tuya(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 0.21f, 1).spread(0.0033f);
            final Noise2D jitterNoise = new OpenSimplex2D(seed.seed() + 1234123L).octaves(2).scaled(-0.016f, 0.016f).spread(0.128f);
            final Noise2D addedNoise = new OpenSimplex2D(seed.seed()).octaves(2).spread(0.1).scaled(-2, 3);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                final float rarity = biome.getCenteredFeatureFrequency();
                if (biome.hasTuyas())
                {
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            public double modifyHeight(Cellular2D.Cell cell, double x, double z, BiomeExtension biome, double heightIn)
            {
                if (cell != null)
                {
                    final double easing = Mth.clamp(calculateEasing((float) cell.f1()) + jitterNoise.noise(x, z), 0, 1);
                    final double shape = biome.getCenteredFeatureIce() ? calculateIcyShape(1 - easing) : calculateShape(1 - easing);
                    final double additionalHeight = (float) (shape * biome.getCenteredFeatureScaleHeight() + addedNoise.noise(x, z));
                    final double tuyaHeight = SEA_LEVEL_Y + biome.getCenteredFeatureBaseHeight() + additionalHeight;
                    return Mth.lerp(easing, heightIn, 0.5f * (tuyaHeight + Math.max(tuyaHeight, heightIn + 0.4f * additionalHeight)));
                }
                return heightIn;
            }

            /**
             * @param t The unscaled square distance from the tuya, roughly in [0, 1.2]
             * @return A noise function determining the tuya's height at any given position, in the range [0, 1]
             */
            private static double calculateShape(double t)
            {
                return t < 0.015f ? Mth.map(t, 0f, 0.015f, 0.8f, 1f)// Caldera
                    : t < 0.125f ? Mth.map(t, 0.025f, 0.125f, 1f, 0.75f) // Shallow slope down
                    : Mth.clampedMap(t, 0.125f, 0.16f, 0.75f, 0f); // Cliff
            }

            /**
             * @param t The unscaled square distance from the tuya, roughly in [0, 1.2]
             * @return A noise function determining the tuya's height at any given position, in the range [0, 1]
             */
            private static double calculateIcyShape(double t)
            {
                return t < 0.015f ? Mth.map(t, 0f, 0.015f, 0.8f, 1f) // Caldera
                    : t < 0.125f ? Mth.map(t, 0.025f, 0.125f, 1f, 0.75f) // Shallow slope down
                    : t < 0.16 ? Mth.map(t, 0.125f, 0.16f, 0.75f, 0f) // Cliff
                    : Mth.clampedMap(t, 0.16f, 0.19f, 0f, 0.5f); // Ice edge
            }

            /**
             * Calculate the closeness value to a tuya, in the range [0, 1]. 1 = Center of a tuya, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasTuyas();
            }

            /**
             * Calculate the center of the nearest tuya, if one exists, to the given x, z, at the given y.
             */
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }

            @Override
            public Cellular2D getCellularNoise()
            {
                return cellNoise;
            }

            @Override
            public @Nullable VolcanoVariant getVolcanoVariant(Cellular2D.Cell cell)
            {
                return null;
            }
        };
    }

    public static CenteredFeatureNoiseSampler stratovolcano(Seed seed)
    {
        return new CenteredFeatureNoiseSampler()
        {
            final Cellular2D cellNoise = new Cellular2D(seed.seed(), 2).spread(STRATOVOLCANO_SPREAD_FACTOR);

            @Override
            public double setColumnAndSampleHeight(double heightIn, int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                final BiomeExtension biome = biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
                if (biome.hasStratovolcanoes())
                {
                    final float rarity = biome.getCenteredFeatureFrequency();
                    if (checkCellRarity(cell, rarity))
                    {
                        return modifyHeight(cell, x, z, biome, heightIn);
                    }
                }
                return ChunkHeightFiller.NOT_PRESENT_RETURN;
            }

            public BiomeExtension getCenterBiome(int x, int z, BiomeSourceExtension biomeSource)
            {
                Cellular2D.Cell cell = cellNoise.cell(x, z);
                return biomeSource.getBiomeExtension(QuartPos.fromBlock((int) cell.x()), QuartPos.fromBlock((int) cell.y()));
            }

            /**
             * Calculate the closeness value to a volcano, in the range [0, 1]. 1 = Center of a volcano, 0 = Nowhere near.
             */
            public float calculateEasing(int x, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return calculateClampedEasing((float) cell.f1());
                }
                return 0;
            }

            private double modifyHeight(Cellular2D.Cell cell, int x, int z, BiomeExtension biome, double heightIn)
            {
                // We start by determining the diameter and height of the cone
                // Note that apex heights are scaled to equal the actual diameter of the feature
                double maxDiameter = Math.sqrt(Math.min(1, maxSafeDiameterSquared(cell, cellNoise)));

                final VolcanoVariant variant = getVolcanoVariant(cell);
                final double volcanoHeight;
                if (variant != null)
                {
                    volcanoHeight = variant.getHeight(heightIn, x, z, maxDiameter, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell);
                }
                else
                {
                    volcanoHeight = heightIn;
                }
                return volcanoHeight;
            }

            private static float calculateEasing(float f1)
            {
                return Mth.map(f1, 0, 0.23f, 1, 0);
            }

            private static float calculateClampedEasing(float f1)
            {
                return Mth.clamp(calculateEasing(f1), 0, 1);
            }

            @Override
            public boolean isValidBiome(BiomeExtension biome)
            {
                return biome.hasStratovolcanoes();
            }

            /**
             * Calculate the center of the nearest volcano, if one exists, to the given x, z, at the given y.
             */
            @Override
            @Nullable
            public BlockPos calculateCenter(int x, int y, int z, float frequency)
            {
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                if (checkCellRarity(cell, frequency))
                {
                    return new BlockPos((int) cell.x(), y, (int) cell.y());
                }
                return null;
            }

            @Override
            public Cellular2D getCellularNoise()
            {
                return cellNoise;
            }

            @Override
            public @Nullable VolcanoVariant getVolcanoVariant(Cellular2D.Cell cell)
            {
                // We start by determining the diameter and height of the cone
                // Note that apex heights are scaled to equal the actual diameter of the feature
                double maxDiameter = Math.sqrt(Math.min(1, maxSafeDiameterSquared(cell, cellNoise))); // TODO: I'd like to do this earlier on, but I shouldn't try and optimize right now

                if (maxDiameter >= 1)
                {
                    double noise = Helpers.hashDouble(cell.noise(), 317);
                    if (noise > 0.4)
                    {
                        // Kelimutu has a lot of internal variation compared to Crater Lake, so it is relatively common
                        return VolcanoVariants.kelimutu(seed);
                    }
                    return VolcanoVariants.craterLake(seed);
                }
                else if (maxDiameter >= 0.7)
                {
                    double noise = Helpers.hashDouble(cell.noise(), 317);
                    if (noise > 0.7)
                    {
                        // Kelimutu has a lot of internal variation compared to Crater Lake/Tahoma, so it is relatively common
                        return VolcanoVariants.kelimutu(seed);
                    }
                    if (noise > 0.45)
                    {
                        return VolcanoVariants.craterLake(seed);
                    }
                    if (noise > 0.2)
                    {
                        return VolcanoVariants.tahoma(seed);
                    }
                    return VolcanoVariants.fuji(seed);
                }
                else if (maxDiameter < 0.4)
                {
                    return VolcanoVariants.batholith(seed);
                }
                else if (maxDiameter < 0.55)
                {
                    double noise = Helpers.hashDouble(cell.noise(), 317);
                    if (noise > 0.5)
                    {
                        return VolcanoVariants.batholith(seed);
                    }
                    else
                    {
                        return VolcanoVariants.fuji(seed);
                    }
                }
                else
                {
                    return VolcanoVariants.fuji(seed);
                }
            }
        };
    }

    public static double maxSafeDiameterSquared(Cellular2D.Cell cell, Cellular2D cellNoise)
    {
        // This step is necessary because the exact center of a cell is not aligned with the exact center of a block
        // Which causes the sampled position to be on one side of the center, and potentially closer to a different cell
        double f2 = Math.min(cellNoise.cell(cell.x() + 1, cell.y()).f2(), cellNoise.cell(cell.x() - 1, cell.y()).f2());
        f2 = Math.min(f2, cellNoise.cell(cell.x(), cell.y() + 1).f2());
        f2 = Math.min(f2, cellNoise.cell(cell.x(), cell.y() - 1).f2());

        return f2;
    }
}
