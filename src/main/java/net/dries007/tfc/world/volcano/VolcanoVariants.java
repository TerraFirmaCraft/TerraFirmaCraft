/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Cellular1D;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class VolcanoVariants
{
    // Simple cone shape, similar to Mt. Fuji, Japan
    public static VolcanoVariant fuji(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.05, 0.05);
        final Noise2D footprintNoise = new OpenSimplex2D(seed.seed() + 6143L).octaves(2).spread(0.0028).scaled(-0.06, 0.06);
        final double maxRadiusScale = 0.45;

        return new VolcanoVariant()
        {
            @Override
            public String name()
            {
                return "fuji";
            }

            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double noise = cell.noise();

                // Simple cone
                final double baseR = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double r = distortOceanicFootprint(baseR, heightIn, x, z, cell, footprintNoise, 0.58, 0.94);
                final double craterSize = 0.04 + 0.1 * Helpers.hashDouble(noise, 10);
                double shape = maxDiam * calculateSimpleRadialShapeWithSkirt(r, craterSize, 0.9, cell.f1(), cell.f2(), 2);
                shape = shape * (1 - 0.1 * calculateCircumferentialErosion(cell, craterSize, 0.2, 0.9, 1, r, 3, (int) (maxDiam * 16), ridgeWarpNoise.noise(x, z)));

                if (r > 0.65)
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 0.65, 1.2, 0, 1);
                }

                return Math.max(scaleShape(shape, biomeBaseHeight, biomeScaleHeight), heightIn);
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler)
            {
                if (oceanFloorHeight <= preVolcanicHeight + 2)
                {
                    // Surface building failed because volcano is not influencing height
                    return false;
                }

                final BlockPos pos = context.pos();
                final int x = pos.getX();
                final int z = pos.getZ();

                final Cellular2D cellNoise = sampler.getCellularNoise();
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                final double noise = cell.noise();
                final double maxDiam = Math.min(1, Math.sqrt(CenteredFeatureNoise.maxSafeDiameterSquared(cell, cellNoise)));
                final BiomeExtension biome = context.stratovolcanoBiome();
                final int unerodedHeight = (int) getUnerodedHeight(maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell);

                final double baseR = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double r = distortOceanicFootprint(baseR, preVolcanicHeight, x, z, cell, footprintNoise, 0.58, 0.94);

                if (r > 1.2)
                {
                    // Surface building failed because far from volcano, fall back to default
                    return false;
                }

                final double craterSize = 0.04 + 0.1 * Helpers.hashDouble(noise, 10);
                final double featurePlacementHash = Helpers.hashDouble(cell.noise(), 3199);
                if (r <= craterSize || (featurePlacementHash < 0.6 && r <= 2 * craterSize))
                {
                    buildStrataSurfaceOnly(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, noise, seed);
                }
                else
                {
                    buildNormalSurfaceWithStrata(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, 0, noise, seed, SurfaceStates.VOLCANIC_TOP_GRASS_TO_TUFF_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_TUFF_GRAVEL, Fluids.LAVA.getSource().defaultFluidState().createLegacyBlock());
                }
                return true;
            }

            public double getUnerodedHeight(double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double noise = cell.noise();

                // Simple truncated cone
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.04 + 0.1 * Helpers.hashDouble(noise, 10);
                double shape = maxDiam * calculateCone(r, craterSize);

                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }
        };
    }

    // Large Crater with a central lake, Similar to Crater Lake, Oregon
    // This version returns the land height, not the water surface
    public static VolcanoVariant craterLake(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D rimWarpNoise = new OpenSimplex2D(seed.seed() + 1431L).octaves(2).scaled(-0.08f, 0.08f).spread(0.03f);
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24482L).octaves(3).spread(0.06).scaled(0.85, 1.08);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.09, 0.09);
        final Noise2D footprintNoise = new OpenSimplex2D(seed.seed() + 26143L).octaves(2).spread(0.0028).scaled(-0.05, 0.05);
        final double maxRadiusScale = 0.45;

        return new VolcanoVariant()
        {
            @Override
            public String name()
            {
                return "crater_lake";
            }

            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double landHeight = getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
                final double waterHeight = getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);

                return Math.max(landHeight, waterHeight);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Simple cone
                final double f1 = cell.f1();
                final double baseR = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double r = distortOceanicFootprint(baseR, heightIn, x, z, cell, footprintNoise, 0.68, 0.97);
                final double craterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape
                final double rimHeight = 0.42;
                double shape = rimHeight * calculateSimpleRadialShapeWithSkirt(r, craterSize, 2, f1, cell.f2(), 5);
                shape = shape * (1 - 0.12 * calculateCircumferentialErosion(cell, craterSize, craterSize + 0.06, 0.95, 1, r, 24, (int) (maxDiam * 32), ridgeWarpNoise.noise(x, z)));
                shape = shape * (1 - 0.08 * calculateCircumferentialErosion(cell, craterSize * 0.4, craterSize * 0.8, craterSize * 0.8, craterSize, r, 24, (int) (maxDiam * 32), ridgeWarpNoise.noise(x, z)));

                if (r < craterSize)
                {
                    // Add "Wizard Island" with random height and offset
                    final double noise = cell.noise();
                    final double apexHeight = rimHeight * (0.65 + 0.45 * Helpers.hashDouble(noise, 8));
                    final double xOffset = -45 + 90 * Helpers.hashDouble(noise, 6);
                    final double zOffset = -45 + 90 * Helpers.hashDouble(noise, 7);
                    shape = addOffsetCone(shape, cell.x() + xOffset, cell.y() + zOffset, x, z, 0, apexHeight, apexHeight * maxDiam * 300, noise);
                    heightIn = SEA_LEVEL_Y + 10 + biomeBaseHeight;
                }
                else if (r > 1)
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 1, 1.2, 0, 1);
                }
                else
                {

                }
                shape *= textureNoise.noise(x, z);
                shape = scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
                if (r > craterSize && heightIn > shape)
                {
                    heightIn = Mth.clampedMap(r, craterSize + 0.1, craterSize, heightIn, shape);
                }
                return Math.max(shape, heightIn);
            }

            // Use a point within the cell, rather than the cell center, for the peak of a cone
            public double addOffsetCone(double shapeIn, double xCenter, double zCenter, double x, double z, double baseHeight, double apexHeight, double maxRadius, double noise)
            {
                final double maxDiam = apexHeight - baseHeight;
                final double r = Mth.map(Math.sqrt((x - xCenter) * (x - xCenter) + (z - zCenter) * (z - zCenter)), 0, maxRadius, 0, 1);
                final double a = Helpers.diamondAngle((x - xCenter), (z - zCenter)) + Helpers.hashDouble(noise, 3213);

                // Simple cone
                final double craterSize = 0.03 + 0.03 * Helpers.hashDouble(noise, 10);
                double shape = calculateSimpleRadialShapeNoSkirt(r, craterSize, 1.1);
                shape = shape * (0.9 + 0.1 * calculateOffsetCircumferentialErosion(craterSize, 0.2, 0.9, 1, r, a, (int) (3 + Helpers.hashDouble(noise, 1313) * maxDiam * 12), ridgeWarpNoise.noise(x, z), noise));
                shape = Mth.map(shape, 0, 1, baseHeight, apexHeight);
                return Math.max(shape, shapeIn);
            }

            // Use a point within the cell, rather than the cell center, for the center of circumferential erosion
            public static double calculateOffsetCircumferentialErosion(double rInner0, double rInner1, double rOuter1, double rOuter0, double r, double aIn, int ridges, double ridgeWarpNoise, double noiseIn)
            {
                final double noise = Helpers.hashDouble(noiseIn, 213);
                final double ridgeWarping = ridgeWarpNoise / ridges;
                double a = aIn + ridgeWarping;
                a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

                final double erosion = (2 - noise);
                final double fluvialShape = Math.abs((a * 0.5 * ridges % 2) - 1);

                // Smooth out ridges at an inner and outer radius
                final double easing;
                if (r <= rInner1)
                {
                    easing = Mth.clampedMap(r, rInner0, rInner1, 0, 1);
                }
                else
                {
                    easing = Mth.clampedMap(r, rOuter0, rOuter1, 0, 1);
                }

                // Scale ridges larger on volcanoes with fewer ridges
                final double ridgeScale = Mth.clampedMap(ridges, 3, 10, 1.5, 0.5);

                return (fluvialShape - 1) * erosion * easing * ridgeScale;
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Simple cone
                final double f1 = cell.f1();
                final double r = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape
                if (r < craterSize)
                {
                    return scaleShape(0.2, biomeBaseHeight, biomeScaleHeight);
                }
                else
                {
                    return 0;
                }
            }

            @Override
            public boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler)
            {
                final BlockPos pos = context.pos();
                final int x = pos.getX();
                final int z = pos.getZ();

                final Cellular2D cellNoise = sampler.getCellularNoise();
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                final double noise = cell.noise();
                final double maxDiam = Math.min(1, Math.sqrt(CenteredFeatureNoise.maxSafeDiameterSquared(cell, cellNoise)));
                final BiomeExtension biome = context.stratovolcanoBiome();
                final int landHeight = (int) Math.round(this.getLandHeight(preVolcanicHeight, x, z, maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell));
                final int waterHeight = (int) Math.round(this.getFluidHeight(preVolcanicHeight, x, z, maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell));
                final int unerodedHeight = (int) getUnerodedHeight(maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell);

                final double rMain = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double mainCraterSize = 0.5 + rimWarpNoise.noise(x, z); // Domain warp the rim to get a wavy shape

                if (rMain > 1.2 * mainCraterSize && oceanFloorHeight <= preVolcanicHeight + 2)
                {
                    // Surface building failed, fall back to default
                    return false;
                }

                double r2 = Double.MAX_VALUE;
                if (rMain < 1.2 * mainCraterSize)
                {
                    preVolcanicHeight = (int) Mth.clampedMap(rMain, mainCraterSize + 0.1, mainCraterSize, preVolcanicHeight, SEA_LEVEL_Y + 10);
                    if (rMain < mainCraterSize)
                    {
                        // Bare rock on wizard island, just do a constant radius of 9
                        final double xOffset = -45 + 90 * Helpers.hashDouble(noise, 6);
                        final double zOffset = -45 + 90 * Helpers.hashDouble(noise, 7);
                        final double xCenter = cell.x() + xOffset;
                        final double zCenter = cell.y() + zOffset;
                        r2 = (x - xCenter) * (x - xCenter) + (z - zCenter) * (z - zCenter);
                    }
                }

                if (r2 < 81)
                {
                    buildStrataSurfaceOnly(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, noise, seed);
                }
                else
                {
                    // We swap out the fluid for a variant inspired by Kawah Ijen
                    // Need to use the same hash as the feature placement to make sure things line up
                    final double featurePlacementHash = Helpers.hashDouble(cell.noise(), 3199);
                    final BlockState fluid = featurePlacementHash < 0.83 ? Fluids.WATER.getSource().defaultFluidState().createLegacyBlock() : TFCFluids.SPRING_WATER.getSource().defaultFluidState().createLegacyBlock();
                    buildNormalSurfaceWithStrata(context, unerodedHeight, preVolcanicHeight, Math.max(landHeight, Math.min(oceanFloorHeight, context.getSeaLevel())), waterHeight, noise, seed, SurfaceStates.VOLCANIC_TOP_GRASS_TO_TUFF_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_TUFF_GRAVEL, fluid);
                }
                return true;
            }

            public double getUnerodedHeight(double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Simple cone, we use crater size as an input but just use it to get slope
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.5;
                double shape = 0.42 * calculateCone(r, craterSize);

                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }
        };
    }

    // Large ridges, similar to Mt. Rainier, Washington
    public static VolcanoVariant tahoma(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.5f, 0.5f).spread(0.03f);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.05, 0.05);
        final Noise2D footprintNoise = new OpenSimplex2D(seed.seed() + 16143L).octaves(2).spread(0.0035).scaled(-0.06, 0.06);
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 248582L).octaves(3).spread(0.06).scaled(0.94, 1.06);
        final double maxRadiusScale = 0.45;

        return new VolcanoVariant()
        {
            @Override
            public String name()
            {
                return "tahoma";
            }

            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double noise = cell.noise();

                // A large center crater
                final double craterSize = 0.1 + 0.15 * Helpers.hashDouble(noise, 1013);

                // Transition points between inner and outer ridge systems
                final double transitionLength = 0.15;
                final double transitionCenter = craterSize + 2 * transitionLength;
                final double innerTransition = transitionCenter - transitionLength;
                final double outerTransition = transitionCenter + transitionLength;

                // Simple cone
                final double baseR = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); //Radius, range [0, 1]
                final double r = distortOceanicFootprint(baseR, heightIn, x, z, cell, footprintNoise, 0.60, 0.95);
                double shape = maxDiam * calculateSimpleRadialShapeWithSkirt(r, craterSize, 0.9, cell.f1(), cell.f2(), 2);

                // Transition to different number of ridges farther from center
                final double innerRidgeErosion = r > outerTransition ? 0 : calculateCircumferentialErosion(cell, craterSize, craterSize + transitionLength, innerTransition, outerTransition, r, 3, (int) (maxDiam * 8), ridgeWarpNoise.noise(x, z));
                final double outerRidgeErosion = r < innerTransition ? 0 : calculateCircumferentialErosion(cell, innerTransition, outerTransition, 1 - transitionLength, 1, r, 6, 2 * (int) (maxDiam * 8), ridgeWarpNoise.noise(x, z));
                shape = shape - Mth.clampedMap(r, craterSize, transitionCenter, 0, 0.12) * (1.6 + innerRidgeErosion + outerRidgeErosion);

                if (r < 0.65)
                {
                    // Large crater rim
                    shape *= (1 - 0.52 * craterSize * calculateVariableCraterRim(cell, 0.5 * craterSize, craterSize, 2 * craterSize, r, (int) (1 + Helpers.hashDouble(noise, 978) * 3), x, z));
                    // Inner cone
                    if (r < craterSize)
                    {
                        final double innerConeScale = 1.1 * (0.65 + 0.35 * Helpers.hashDouble(noise, 8973)) * craterSize;
                        final double craterBaseHeight = maxDiam * (1 - 0.9 * craterSize);
                        final double innerCone = craterBaseHeight + innerConeScale * calculateSimpleRadialShapeNoSkirt(Mth.clampedMap(r, 0, 0.9 * craterSize, 0, 1), craterSize, 1);
                        shape = Math.max(shape, innerCone);
                    }
                }
                else
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 0.65, 1.2, 0, 1);
                }

                return Math.max(scaleShape(shape, biomeBaseHeight, biomeScaleHeight), heightIn);
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler)
            {
                if (oceanFloorHeight <= preVolcanicHeight + 2)
                {
                    // Surface building failed, fall back to default
                    return false;
                }
                final BlockPos pos = context.pos();
                final int x = pos.getX();
                final int z = pos.getZ();

                final Cellular2D cellNoise = sampler.getCellularNoise();
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                final double noise = cell.noise();
                final double maxDiam = Math.min(1, Math.sqrt(CenteredFeatureNoise.maxSafeDiameterSquared(cell, cellNoise)));
                final BiomeExtension biome = context.stratovolcanoBiome();
                final int unerodedHeight = (int) getUnerodedHeight(maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell);

                final double craterSize = 0.7 * (0.20 + 0.25 * Helpers.hashDouble(noise, 1013));
                final double baseR = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double r = distortOceanicFootprint(baseR, preVolcanicHeight, x, z, cell, footprintNoise, 0.60, 0.95);

                if (r > 1.2)
                {
                    // Surface building failed because far from volcano, fall back to default
                    return false;
                }

                if (r <= craterSize)
                {
                    buildStrataSurfaceOnly(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, noise, seed);
                }
                else
                {
                    buildNormalSurfaceWithStrata(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, 0, noise, seed, SurfaceStates.VOLCANIC_TOP_GRASS_TO_TUFF_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_TUFF_GRAVEL, Fluids.LAVA.getSource().defaultFluidState().createLegacyBlock());
                }
                return true;
            }

            public double getUnerodedHeight(double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double noise = cell.noise();

                // Simple truncated cone
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.20 + 0.25 * Helpers.hashDouble(noise, 1013);
                double shape = maxDiam * calculateCone(r, craterSize);

                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }

            public double calculateVariableCraterRim(Cellular2D.Cell cell, double rInner, double rCrater, double rOuter, double r, int peakCount, int x, int z)
            {
                final double noise = Helpers.hashDouble(cell.noise(), 43);
                double a = cell.angle() + noise;
                a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

                final double peakShape = Math.abs((a * 0.5 * peakCount % 2) - 1);

                // Smooth out peaks/valleys away from rim
                if (r < rInner || r > rOuter)
                {
                    return 0;
                }

                final double easing;
                if (r <= rCrater)
                {
                    easing = Mth.map(r, rInner, rCrater, 0, 1);
                }
                else
                {
                    easing = Mth.map(r, rCrater, rOuter, 1, 0);
                }

                return peakShape * easing * textureNoise.noise(x, z);
            }
        };
    }

    // Granite dome/batholith: https://en.wikipedia.org/wiki/Granite_dome
    public static VolcanoVariant batholith(Seed seed)
    {
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24852L).octaves(4).spread(0.2).scaled(0.8, 1.2);
        final Noise2D radialWarpNoise = new OpenSimplex2D(seed.seed() + 133L).octaves(2).scaled(0f, 0.006f).spread(0.05f);

        return new VolcanoVariant()
        {
            @Override
            public String name()
            {
                return "batholith";
            }

            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Variable radii
                final double maxR = maxDiam * 0.5 - 0.08 * (Helpers.hashDouble(cell.noise(), 1398));

                // Max some domes eroded
                final double erosionWarp = Helpers.hashDouble(cell.noise(), 1348) > 0.6 ? radialWarpNoise.noise(x, z) : 0;

                // Simple dome, we don't take sqrt of f1 because our end shape is an r2 function anyways
                final double r2 = Mth.map(cell.f1() + erosionWarp, 0, maxR * maxR, 0, 1); // Radius squared, range [0, 1]
                final double verticalScale = maxDiam * (0.8 + 0.4 * Helpers.hashDouble(cell.noise(), 83));

                double shape = verticalScale * (1 - r2);
                shape *= textureNoise.noise(x, z);

                return Math.max(scaleShape(shape, biomeBaseHeight, biomeScaleHeight), heightIn);
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler)
            {
                if (oceanFloorHeight <= preVolcanicHeight + 2)
                {
                    // Surface building failed, fall back to default
                    return false;
                }
                final double noise = sampler.getCellularNoise().cell(context.pos().getX(), context.pos().getZ()).noise();
                if (noise > 0)
                {
                    buildNormalBatholithSurface(context, oceanFloorHeight, preVolcanicHeight, SurfaceStates.VOLCANIC_TOP_GRASS_TO_GRANITE_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_GRANITE_GRAVEL, SurfaceStates.GRANITE_GRAVEL, SurfaceStates.GRANITE_GRAVEL, SurfaceStates.GRANITE_GRAVEL, SurfaceStates.GRANITE);
                }
                else
                {
                    buildNormalBatholithSurface(context, oceanFloorHeight, preVolcanicHeight, SurfaceStates.VOLCANIC_TOP_GRASS_TO_DIORITE_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_DIORITE_GRAVEL, SurfaceStates.DIORITE_GRAVEL, SurfaceStates.DIORITE_GRAVEL, SurfaceStates.DIORITE_GRAVEL, SurfaceStates.DIORITE);
                }
                return true;
            }
        };
    }

    // Multiple medium craters with lakes of different fluids, inspired by Kelimutu, Indonesia
    public static VolcanoVariant kelimutu(Seed seed)
    {
        final Noise2D ridgeWarpNoise = new OpenSimplex2D(seed.seed() + 23L).octaves(2).scaled(-0.4f, 0.4f).spread(0.09f);
        final Noise2D textureNoise = new OpenSimplex2D(seed.seed() + 24482L).octaves(3).spread(0.06).scaled(0.85, 1.08);
        final Noise2D skirtTextureNoise = new OpenSimplex2D(seed.seed() + 2982L).octaves(3).spread(0.09).scaled(-0.05, 0.05);
        final Noise2D footprintNoise = new OpenSimplex2D(seed.seed() + 36143L).octaves(2).spread(0.003).scaled(-0.06, 0.06);
        final double maxRadiusScale = 0.45;

        return new VolcanoVariant()
        {
            @Override
            public String name()
            {
                return "kelimutu";
            }

            @Override
            public double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double landHeight = getLandHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
                final double waterHeight = getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);

                return Math.max(landHeight, waterHeight);
            }

            @Override
            public double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                final double noise = cell.noise();

                // Start by generating 3 truncated cones:
                // Main Cone
                final double f1 = cell.f1();
                final double baseR = Mth.map(Mth.sqrt((float) f1), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double r = distortOceanicFootprint(baseR, heightIn, x, z, cell, footprintNoise, 0.60, 0.92);
                final double crater0Size = 0.06 + 0.06 * Helpers.hashDouble(noise, 67); // Domain warp the rim to get a wavy shape
                final double rimHeight0 = 0.65;
                double shape = rimHeight0 * calculateTruncatedCone(r, crater0Size);
                shape *= (1 - 0.12 * calculateCircumferentialErosion(cell, crater0Size, crater0Size + 0.06, 0.95, 1, r, 6, (int) (maxDiam * 12), ridgeWarpNoise.noise(x, z)));

                // Add secondary cone w/ random height and offset
                final double randOffsetX1 = 2 * (0.5 - Helpers.hashDouble(noise, 68));
                final double randOffsetZ1 = 2 * (0.5 - Helpers.hashDouble(noise, 69));
                final double xOffset1 = randOffsetX1 > 0 ? 20 + randOffsetX1 * 70 : -20 + randOffsetX1 * 70;
                final double zOffset1 = randOffsetZ1 > 0 ? 20 + randOffsetZ1 * 70 : -20 + randOffsetZ1 * 70;

                // We want the size to be proportional to distance between centers
                final double crater1Size = 0.06 + 0.08 * (Math.abs(randOffsetX1) + Math.abs(randOffsetZ1));

                // We also want the crater depth to be proportional to the height difference to the center cone
                final double randRimHeight1 = Helpers.hashDouble(noise, 70);
                final double rimHeight1 = rimHeight0 * (0.8 + 0.2 * randRimHeight1);
                final int ridgeCount1 = 3 + (int) (rimHeight1 * 8 + Helpers.hashDouble(noise, 101) * 4);
                shape = addOffsetTruncatedCone(shape, cell.x() + xOffset1, cell.y() + zOffset1, x, z, rimHeight1, rimHeight1 * maxDiam * 300, crater1Size, ridgeCount1, noise);

                // Add craters for each cone, and subtract them from the overall shape
                double craterShape = rimHeight0 * calculateCrater(r / crater0Size, crater0Size, 0.6 + Helpers.hashDouble(noise, 74));
                craterShape = addOffsetCrater(craterShape, cell.x() + xOffset1, cell.y() + zOffset1, x, z, 0, rimHeight1, rimHeight1 * maxDiam * 300, crater1Size, 1.3 + randRimHeight1);

                // Chance to not add a third cone
                if (Helpers.hashDouble(noise, 1066) > 0.3)
                {
                    // Add tertiary cone w/ random height and offset
                    final double randOffsetX2 = 2 * (0.5 - Helpers.hashDouble(noise, 71));
                    final double randOffsetZ2 = 2 * (0.5 - Helpers.hashDouble(noise, 72));
                    final double xOffset2 = randOffsetX2 > 0 ? 20 + randOffsetX2 * 70 : -20 + randOffsetX2 * 73;
                    final double zOffset2 = randOffsetZ2 > 0 ? 20 + randOffsetZ2 * 70 : -20 + randOffsetZ2 * 74;
                    final double crater2Size = 0.06 + 0.08 * (Math.abs(randOffsetX2) + Math.abs(randOffsetZ2));

                    final double randRimHeight2 = Helpers.hashDouble(noise, 73);
                    final double rimHeight2 = rimHeight0 * (0.7 + 0.3 * randRimHeight2);
                    final int ridgeCount2 = 3 + (int) (rimHeight2 * 8 + Helpers.hashDouble(noise, 101) * 4);
                    shape = addOffsetTruncatedCone(shape, cell.x() + xOffset2, cell.y() + zOffset2, x, z, rimHeight2, rimHeight2 * maxDiam * 300, crater2Size, ridgeCount2, noise);

                    // Add the third crater shape
                    craterShape = addOffsetCrater(craterShape, cell.x() + xOffset2, cell.y() + zOffset2, x, z, 0, rimHeight2, rimHeight2 * maxDiam * 300, crater2Size, 1.3 + randRimHeight2);
                }

                shape = Math.min(shape, craterShape);
                final double crateredHeightIn = Math.min(heightIn, scaleShape(craterShape, biomeBaseHeight, biomeScaleHeight));

                shape *= textureNoise.noise(x, z);

                if (r > 0.7)
                {
                    // Add some texture to the volcano "skirt"
                    shape += skirtTextureNoise.noise(x, z) * Mth.clampedMap(r, 0.7, 1, 0, 1);

                    // Simple cell edge handling
                    shape -= Mth.clampedMap(cell.f2() - cell.f1(), 0, 0.3, 0.2, 0);
                }

                return Math.max(scaleShape(shape, biomeBaseHeight, biomeScaleHeight), crateredHeightIn);
            }

            // Use a point within the cell, rather than the cell center, for the peak of a cone
            public double addOffsetTruncatedCone(double shapeIn, double xCenter, double zCenter, double x, double z, double rimHeight, double maxRadius, double craterSize, int ridgeCount, double noise)
            {
                final double r = Mth.map(Math.sqrt((x - xCenter) * (x - xCenter) + (z - zCenter) * (z - zCenter)), 0, maxRadius, 0, 1);
                final double a = Helpers.diamondAngle((x - xCenter), (z - zCenter)) + Helpers.hashDouble(noise, 3213);

                // Simple cone
                double shape = rimHeight * calculateTruncatedCone(r, craterSize);
                shape *= (1 - 0.15 * calculateOffsetCircumferentialErosion(craterSize, 0.2, 0.9, 1, r, a, ridgeCount, ridgeWarpNoise.noise(x, z), noise));
                return Math.max(shape, shapeIn);
            }

            // Use a point within the cell, rather than the cell center, for the center of a crater
            public double addOffsetCrater(double shapeIn, double xCenter, double zCenter, double x, double z, double baseHeight, double rimHeight, double maxRadius, double craterSize, double craterDepthScale)
            {
                final double r = Mth.map(Math.sqrt((x - xCenter) * (x - xCenter) + (z - zCenter) * (z - zCenter)), 0, craterSize * maxRadius, 0, 1);

                // Simple crater
                double shape = rimHeight * calculateCrater(r, craterSize, craterDepthScale);
                shape = Mth.map(shape, 0, 1, baseHeight, rimHeight);
                return Math.min(shape, shapeIn);
            }

            // Use a point within the cell, rather than the cell center, for the center of circumferential erosion
            public static double calculateOffsetCircumferentialErosion(double rInner0, double rInner1, double rOuter1, double rOuter0, double r, double aIn, int ridges, double ridgeWarpNoise, double noiseIn)
            {
                final double noise = Helpers.hashDouble(noiseIn, 213);
                final double ridgeWarping = ridgeWarpNoise / ridges;
                double a = aIn + ridgeWarping;
                a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

                final double erosion = (2 - noise);
                final double fluvialShape = Math.abs((a * 0.5 * ridges % 2) - 1);

                // Smooth out ridges at an inner and outer radius
                final double easing;
                if (r <= rInner1)
                {
                    easing = Mth.clampedMap(r, rInner0, rInner1, 0, 1);
                }
                else
                {
                    easing = Mth.clampedMap(r, rOuter0, rOuter1, 0, 1);
                }

                // Scale ridges larger on volcanoes with fewer ridges
                final double ridgeScale = Mth.clampedMap(ridges, 3, 10, 1.5, 0.5);

                return (fluvialShape - 1) * erosion * easing * ridgeScale;
            }

            @Override
            public double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                return VolcanoVariant.super.getFluidHeight(heightIn, x, z, maxDiam, biomeScaleHeight, biomeBaseHeight, cell);
            }

            @Override
            public boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler)
            {

                final BlockPos pos = context.pos();
                final int x = pos.getX();
                final int z = pos.getZ();

                final Cellular2D cellNoise = sampler.getCellularNoise();
                final Cellular2D.Cell cell = cellNoise.cell(x, z);
                final double noise = cell.noise();
                final double maxDiam = Math.min(1, Math.sqrt(CenteredFeatureNoise.maxSafeDiameterSquared(cell, cellNoise)));
                final double baseR = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double surfaceR = distortOceanicFootprint(baseR, preVolcanicHeight, x, z, cell, footprintNoise, 0.60, 0.92);
                if (surfaceR > 1.2)
                {
                    // Surface building failed because far from volcano, fall back to default
                    return false;
                }
                final BiomeExtension biome = context.stratovolcanoBiome();
                final int unerodedHeight = (int) getUnerodedHeight(maxDiam, biome.getCenteredFeatureScaleHeight(), biome.getCenteredFeatureBaseHeight(), cell);

                // We check if we are inside any of the three craters, and put stone there if so
                // Main crater
                final double crater0Size = 0.06 + 0.06 * Helpers.hashDouble(noise, 67);
                boolean insideCrater = false;
                if (baseR < 2 * crater0Size)
                {
                    insideCrater = baseR < crater0Size;

                    // Shenanigans to make sure that the surface gets built inside the crater even if the base terrain would be taller than the crater
                    preVolcanicHeight = Math.min(preVolcanicHeight, (int) Mth.clampedMap(baseR, 2 * crater0Size, 1.6 * crater0Size, oceanFloorHeight - 1, oceanFloorHeight - 15));
                }

                // Second crater
                final double rimHeight0 = 0.65;
                if (!insideCrater)
                {
                    // Add secondary cone w/ random height and offset
                    final double randOffsetX1 = 2 * (0.5 - Helpers.hashDouble(noise, 68));
                    final double randOffsetZ1 = 2 * (0.5 - Helpers.hashDouble(noise, 69));
                    final double xCenter1 = cell.x() + (randOffsetX1 > 0 ? 20 + randOffsetX1 * 70 : -20 + randOffsetX1 * 70);
                    final double zCenter1 = cell.y() + (randOffsetZ1 > 0 ? 20 + randOffsetZ1 * 70 : -20 + randOffsetZ1 * 70);

                    // We want the size to be proportional to distance between centers
                    final double randRimHeight1 = Helpers.hashDouble(noise, 70);
                    final double rimHeight1 = rimHeight0 * (0.8 + 0.2 * randRimHeight1);
                    final double crater1Size = 0.06 + 0.08 * (Math.abs(randOffsetX1) + Math.abs(randOffsetZ1));
                    final double r1 = Mth.map(Math.sqrt((x - xCenter1) * (x - xCenter1) + (z - zCenter1) * (z - zCenter1)), 0, rimHeight1 * maxDiam * 300, 0, 1);
                    insideCrater = r1 < crater1Size;
                    if (r1 < 2 * crater1Size)
                    {
                        preVolcanicHeight = Math.min(preVolcanicHeight, (int) Mth.clampedMap(r1, 2 * crater1Size, 1.6 * crater1Size, oceanFloorHeight - 1, oceanFloorHeight - 15));
                    }
                }

                // Third crater
                if (!insideCrater && Helpers.hashDouble(noise, 1066) > 0.3)
                {
                    // Add tertiary cone w/ random height and offset
                    final double randOffsetX2 = 2 * (0.5 - Helpers.hashDouble(noise, 71));
                    final double randOffsetZ2 = 2 * (0.5 - Helpers.hashDouble(noise, 72));
                    final double xCenter2 = cell.x() + (randOffsetX2 > 0 ? 20 + randOffsetX2 * 70 : -20 + randOffsetX2 * 73);
                    final double zCenter2 = cell.y() + (randOffsetZ2 > 0 ? 20 + randOffsetZ2 * 70 : -20 + randOffsetZ2 * 74);

                    final double randRimHeight2 = Helpers.hashDouble(noise, 73);
                    final double rimHeight2 = rimHeight0 * (0.7 + 0.3 * randRimHeight2);
                    final double crater2Size = 0.06 + 0.08 * (Math.abs(randOffsetX2) + Math.abs(randOffsetZ2));
                    final double r2 = Mth.map(Math.sqrt((x - xCenter2) * (x - xCenter2) + (z - zCenter2) * (z - zCenter2)), 0, rimHeight2 * maxDiam * 300, 0, 1);
                    insideCrater = r2 < crater2Size;
                    if (r2 < 4 * crater2Size)
                    {
                        preVolcanicHeight = Math.min(preVolcanicHeight, (int) Mth.clampedMap(r2, 2 * crater2Size, 1.6 * crater2Size, oceanFloorHeight, oceanFloorHeight - 15));
                    }
                }


                if (oceanFloorHeight <= preVolcanicHeight + 2)
                {
                    // Surface building failed, fall back to default
                    return false;
                }
                if (insideCrater)
                {
                    buildStrataSurfaceOnly(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, noise, seed);
                }
                else
                {
                    buildNormalSurfaceWithStrata(context, unerodedHeight, preVolcanicHeight, oceanFloorHeight, 0, noise, seed, SurfaceStates.VOLCANIC_TOP_GRASS_TO_TUFF_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_TUFF_GRAVEL, Fluids.WATER.getSource().defaultFluidState().createLegacyBlock());
                }
                return true;
            }

            public double getUnerodedHeight(double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
            {
                // Simple cone, we use crater size as an input but just use it to get slope
                final double r = Mth.map(Mth.sqrt((float) cell.f1()), 0, maxDiam * maxRadiusScale, 0, 1); // Radius, range [0, 1]
                final double craterSize = 0.06 + 0.06 * Helpers.hashDouble(cell.noise(), 67);
                double shape = 0.65 * calculateCone(r, craterSize);

                return scaleShape(shape, biomeBaseHeight, biomeScaleHeight);
            }
        };
    }


    /**
     * @param r       The scaled, non-square distance from the volcano, from 0 at center to 1 at edge of influence
     * @param rCrater The radius of the crater
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    public static double calculateSimpleRadialShapeWithSkirt(double r, double rCrater, double craterDepthScale, double f1, double f2, double skirtSlope)
    {
        if (r >= 1)
        {
            // Outside of the detailed radius, decrease below base level. Faster near cell edge
            return (1 - r) * Mth.clampedMap(f2 - f1, 0, 0.1, skirtSlope, 1);
        }
        else
        {
            return calculateSimpleRadialShape(r, rCrater, craterDepthScale);
        }
    }

    /**
     * @param r       The scaled, non-square distance from the volcano, from 0 at center to 1 at edge of influence
     * @param rCrater The radius of the crater
     * @return A noise function determining the volcano's height at any given position, in the range [0, 1]
     */
    public static double calculateSimpleRadialShapeNoSkirt(double r, double rCrater, double craterDepthScale)
    {
        if (r >= 1)
        {
            // Outside of the radius, return 0
            return 0;
        }
        else
        {
            return calculateSimpleRadialShape(r, rCrater, craterDepthScale);
        }
    }

    public static double calculateSimpleRadialShape(double r, double rCrater, double craterDepthScale)
    {
        if (r > rCrater)
        {
            // Main slopes
            double x = Mth.map(r, rCrater, 1, 0, 1);
            return Helpers.hyperbolicSection(x, 1, 1);
        }
        else
        {
            // Interior of crater
            double craterBaseHeight = 1 - craterDepthScale * rCrater;
            return Helpers.hyperbolicSection(rCrater - r, rCrater, craterDepthScale * rCrater) + craterBaseHeight;
        }
    }

    public static double calculateTruncatedCone(double r, double rCrater)
    {
        if (r > 1)
        {
            return 1 - r;
        }
        else if (r > rCrater)
        {
            // Main slopes
            double x = Mth.map(r, rCrater, 1, 0, 1);
            return Helpers.hyperbolicSection(x, 1, 1);
        }
        else
        {
            // Interior of crater
            return 1;
        }
    }

    public static double calculateCone(double r, double rCrater)
    {
        return Mth.map(r, 1, rCrater, 0, 1);
    }

    public static double calculateCrater(double r, double rCrater, double craterDepthScale)
    {
        final double craterDepth = rCrater * craterDepthScale;
        return (1 - craterDepth) + craterDepth * r * r;
    }

    public static double calculateCircumferentialErosion(Cellular2D.Cell cell, double rInner0, double rInner1, double rOuter1, double rOuter0, double r, int minRidgeCount, int addedRidgeCount, double ridgeWarpNoise)
    {
        final double noise = Helpers.hashDouble(cell.noise(), 213);
        final int ridges = (int) (noise * addedRidgeCount) + minRidgeCount;
        final double ridgeWarping = ridgeWarpNoise / ridges;
        double a = cell.angle() + ridgeWarping;
        a = a >= 4 ? a - 4 : a < 0 ? a + 4 : a;

        final double erosion = (2 - noise);
        final double fluvialShape = Math.abs((a * 0.5 * ridges % 2) - 1);

        // Smooth out ridges at an inner and outer radius
        final double easing;
        if (r <= rInner1)
        {
            easing = Mth.clampedMap(r, rInner0, rInner1, 0, 1);
        }
        else
        {
            easing = Mth.clampedMap(r, rOuter0, rOuter1, 0, 1);
        }

        // Scale ridges larger on volcanoes with fewer ridges, from 0.6 to 1.4
        final double ridgeScale = Mth.clampedMap(ridges, minRidgeCount, minRidgeCount + addedRidgeCount, 1.5, 0.5);

        return (fluvialShape - 1) * erosion * easing * ridgeScale;
    }

    public static double scaleShape(double shape, double base, double scale)
    {
        return SEA_LEVEL_Y + base + shape * scale;
    }

    // Bare stone, following strata patterns
    public static void buildStrataSurfaceOnly(SurfaceBuilderContext context, int unerodedY, int baseY, int landHeight, double cellNoiseIn, Seed seed)
    {
        final double rockTypeNoise = Helpers.hashDouble(cellNoiseIn, 856);
        Cellular1D cells = new Cellular1D(seed.seed()).spread(0.25);

        for (int y = landHeight; y >= baseY; --y)
        {
            final BlockState stateAt = context.getBlockState(y);
            if (context.isDefaultBlock(stateAt))
            {
                context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.RAW));
            }
        }
    }

    public static void buildNormalSurfaceWithStrata(SurfaceBuilderContext context, int unerodedY, int baseY, int landHeight, int waterHeight, double cellNoiseIn, Seed seed, SurfaceState topState, SurfaceState midState, BlockState fluidState)
    {
        final double rockTypeNoise = Helpers.hashDouble(cellNoiseIn, 856);
        Cellular1D cells = new Cellular1D(seed.seed()).spread(0.25);
        int surfaceDepth = -1;
        int surfaceY = 0;
        SurfaceState surfaceState = null;
        boolean isUnderWater;
        final int shoreLevel = 4 + (int) BiomeNoise.shoreTideLevelNoise(seed).noise(context.pos().getX(), context.pos().getZ());

        // Fluid lakes
        if (waterHeight > landHeight)
        {
            // This loop is necessary to place the water
            for (int y = waterHeight; y >= landHeight; --y)
            {
                context.setBlockState(y, fluidState);
            }
            // And this loop is necessary to make sure the water doesn't get carved into by a river
            context.setBlockState(landHeight, getStratifiedStoneBlock(unerodedY, landHeight, cells, rockTypeNoise, Rock.BlockType.GRAVEL));
            for (int y = landHeight - 1; y >= landHeight - 5; --y)
            {
                context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.RAW));
            }
            isUnderWater = true;
        }
        else
        {
            isUnderWater = false;
        }

        for (int y = landHeight; y >= baseY; --y)
        {
            final BlockState stateAt = context.getBlockState(y);
            if (stateAt.isAir())
            {
                surfaceDepth = -1; // Reached air, reset surface depth
            }
            else if (context.isDefaultBlock(stateAt))
            {
                if (surfaceDepth == -1)
                {
                    surfaceY = y; // Reached surface. Place top state and switch to subsurface layers
                    if (y < shoreLevel || isUnderWater)
                    {
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, -1);
                        if (surfaceDepth < -1)
                        {
                            // No surface layers
                            surfaceDepth = 0;
                            context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.RAW));
                        }
                        else if (surfaceDepth == -1)
                        {
                            // Place one subsurface layer, skipping the top layer entirely
                            surfaceDepth = 0;
                            context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.GRAVEL));
                        }
                        else
                        {
                            context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.GRAVEL));
                        }
                    }
                    else
                    {
                        // Min return value = -3, is equivalent to ROCKY normal surface builder
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, -3);
                        if (surfaceDepth < -1)
                        {
                            // No surface layers
                            surfaceDepth = 0;
                            context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.RAW));
                        }
                        else if (surfaceDepth == -1)
                        {
                            surfaceDepth = 0;
                            context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.GRAVEL));
                        }
                        else
                        {
                            context.setBlockState(y, topState);
                        }
                        surfaceState = midState;
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    if (surfaceState == null)
                    {
                        context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.GRAVEL));
                    }
                    else
                    {
                        context.setBlockState(y, surfaceState);
                    }
                }
                // At this point, we are below the surface and subsurface
                else
                {
                    context.setBlockState(y, getStratifiedStoneBlock(unerodedY, y, cells, rockTypeNoise, Rock.BlockType.RAW));
                }
            }
        }
    }

    public static BlockState getStratifiedStoneBlock(int unerodedY, int y, Cellular1D cells, double rockTypeNoiseIn, Rock.BlockType blockType)
    {
        final int strataDepth = unerodedY - y;
        final Cellular1D.Cell cell = cells.cell(strataDepth);

        if (cell.noise() > 0.3)
        {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.TUFF).get(blockType).get().defaultBlockState();
        }
        else if (cell.noise() < -0.8)
        {
            return TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(blockType).get().defaultBlockState();
        }
        else
        {
            final double rockTypeNoise = Helpers.hashDouble(rockTypeNoiseIn, 9673);
            final Rock rock = rockTypeNoise > 0.67 ? Rock.ANDESITE : rockTypeNoise > 0.33 ? Rock.DACITE : Rock.RHYOLITE;
            return TFCBlocks.ROCK_BLOCKS.get(rock).get(blockType).get().defaultBlockState();
        }
    }

    public static void buildNormalBatholithSurface(SurfaceBuilderContext context, int startY, int endY, SurfaceState topState, SurfaceState midState, SurfaceState underState, SurfaceState underWaterState, SurfaceState thinUnderWaterState, SurfaceState rockState)
    {
        int surfaceDepth = -1;
        int surfaceY = 0;
        boolean underwaterLayer = false, firstLayer = false;
        SurfaceState surfaceState = SurfaceStates.RAW;

        for (int y = startY; y >= endY; --y)
        {
            final BlockState stateAt = context.getBlockState(y);
            if (stateAt.isAir())
            {
                surfaceDepth = -1; // Reached air, reset surface depth
            }
            else if (context.isDefaultBlock(stateAt))
            {
                if (surfaceDepth == -1)
                {
                    surfaceY = y; // Reached surface. Place top state and switch to subsurface layers
                    firstLayer = true;
                    if (y < context.getSeaLevel() - 1)
                    {
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, -1);
                        if (surfaceDepth < -1)
                        {
                            // No surface layers
                            surfaceDepth = 0;
                            context.setBlockState(y, rockState);
                        }
                        else if (surfaceDepth == -1)
                        {
                            // Place one subsurface layer, skipping the top layer entirely
                            surfaceDepth = 0;
                            context.setBlockState(y, thinUnderWaterState);
                        }
                        else
                        {
                            context.setBlockState(y, underWaterState);
                        }
                        surfaceState = underWaterState;
                        underwaterLayer = true;
                    }
                    else
                    {
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, -3);
                        if (surfaceDepth < -1)
                        {
                            // No surface layers
                            surfaceDepth = 0;
                            context.setBlockState(y, rockState);
                        }
                        else if (surfaceDepth == -1)
                        {
                            surfaceDepth = 0;
                            context.setBlockState(y, underState);
                        }
                        else
                        {
                            context.setBlockState(y, topState);
                        }
                        surfaceState = midState;
                        underwaterLayer = false;
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(y, surfaceState);
                    if (surfaceDepth == 0)
                    {
                        // Next subsurface layer
                        if (firstLayer)
                        {
                            firstLayer = false;
                            if (underwaterLayer)
                            {
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 0);
                                surfaceState = thinUnderWaterState;
                            }
                            else
                            {
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 0);
                                surfaceState = underState;
                            }
                        }
                    }
                }
                // At this point, we are below the surface and subsurface
                else
                {
                    context.setBlockState(y, rockState);
                }
            }
        }
    }

    private static double distortOceanicFootprint(double r, double terrainHeight, int x, int z, Cellular2D.Cell cell, Noise2D footprintNoise, double warpStart, double warpFull)
    {
        final double underwaterStrength = Mth.clampedMap(SEA_LEVEL_Y - terrainHeight, 0, 18, 0, 1);

        if (underwaterStrength <= 0)
        {
            return r;
        }

        double lowerSlopeStrength = Mth.clampedMap(r, warpStart, warpFull, 0, 1);

        lowerSlopeStrength = lowerSlopeStrength * lowerSlopeStrength * (3 - 2 * lowerSlopeStrength);

        if (lowerSlopeStrength <= 0)
        {
            return r;
        }

        final double angle = Math.atan2(z - cell.y(), x - cell.x());
        final double cellNoise = cell.noise();
        final double phase2 = 2 * Math.PI * Helpers.hashDouble(cellNoise, 6101);
        final double phase3 = 2 * Math.PI * Helpers.hashDouble(cellNoise, 6102);
        final double phase5 = 2 * Math.PI * Helpers.hashDouble(cellNoise, 6103);
        final double elongation = 0.02 + 0.015 * Helpers.hashDouble(cellNoise, 6104);
        final double threeLobeStrength = 0.035 + 0.02 * Helpers.hashDouble(cellNoise, 6105);
        final double fiveLobeStrength = 0.01 + 0.01 * Helpers.hashDouble(cellNoise, 6106);

        double radiusScale = 1 + elongation * Math.cos(2 * (angle - phase2)) + threeLobeStrength * Math.cos(3 * (angle - phase3)) + fiveLobeStrength * Math.cos(5 * (angle - phase5)) + footprintNoise.noise(x, z);

        radiusScale = Mth.clamp(radiusScale, 0.86, 1.18);

        final double distortedR = r / radiusScale;

        return Mth.lerp(underwaterStrength * lowerSlopeStrength, r, distortedR);
    }
}
