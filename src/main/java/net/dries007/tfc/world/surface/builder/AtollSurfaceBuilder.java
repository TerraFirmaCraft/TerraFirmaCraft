/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;

public class AtollSurfaceBuilder implements SurfaceBuilder
{
    public static SurfaceBuilderFactory create(SurfaceBuilderFactory parent)
    {
        return seed -> new AtollSurfaceBuilder(parent.apply(seed), seed);
    }

    private final SurfaceBuilder parent;
    private final Seed seed;

    private final Noise2D heightNoise;

    public AtollSurfaceBuilder(SurfaceBuilder parent, Seed seed)
    {
        this.parent = parent;
        this.seed = seed;
        this.heightNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.1f).scaled(-4, 4);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (context.atollBiome().hasAtolls())
        {
            final CenteredFeatureNoiseSampler sampler = CenteredFeatureNoise.atolls(seed);
            final float easing = sampler.calculateEasing(context.pos(), context.atollBiome());
            if (easing > 0)
            {
                final double randomHeight = heightNoise.noise(context.pos().getX(), context.pos().getZ());
                final int seaLevel = context.getSeaLevel();
                final int preAtollHeight = context.getPreVolcanicHeight();
                final int volcanoHeight = (int) Mth.clampedMap(easing, 0.4, 0.7, seaLevel - 70, seaLevel - 8 + randomHeight);
                final int maxDepth = Math.max(preAtollHeight, volcanoHeight);

                final SurfaceState grassState, sandState;
                final Cellular2D.Cell cell = sampler.getCellularNoise().cell(context.pos().getX(), context.pos().getZ());
                if (Helpers.hashDouble(cell.noise(), 6324) > 0.7)
                {
                    if (context.rainfall() > 375)
                    {
                        grassState = SurfaceStates.ATOLL_GRASS_TO_PINK_SAND;
                        sandState = SurfaceStates.PINK_SAND;
                    }
                    else
                    {
                        grassState = SurfaceStates.ATOLL_GRASS_TO_YELLOW_SAND;
                        sandState = SurfaceStates.YELLOW_SAND;
                    }
                }
                else
                {
                    grassState = SurfaceStates.ATOLL_GRASS_TO_WHITE_SAND;
                    sandState = SurfaceStates.WHITE_SAND;
                }

                final SurfaceState rockState = Helpers.hashDouble(cell.noise(), 624) > 0.7 ? SurfaceStates.DOLOMITE : SurfaceStates.LIMESTONE;

                final int oceanFloorY = context.chunk().getHeight(Heightmap.Types.OCEAN_FLOOR_WG, context.pos().getX(), context.pos().getZ());
                if (oceanFloorY > maxDepth + 2)
                {
                    int surfaceDepth = -1;
                    for (int y = startY; y >= maxDepth; y--)
                    {
                        final BlockState stateAt = context.getBlockState(y);
                        if (stateAt.isAir())
                        {
                            surfaceDepth = -1; // Reached air, reset surface depth
                        }
                        // Important that the easing threshold matches the beachDist variable in the atoll shape function
                        else if (y == seaLevel - 1 && CenteredFeatureNoise.getAtollIntegrity(cell) >= 1 && easing > 0.58 && stateAt.is(TFCFluids.SALT_WATER.createSourceBlock().getBlock()))
                        {
                            // Place a single floating layer of fresh water in 100% enclosed lagoons
                            context.setBlockState(y, Fluids.WATER.getSource().defaultFluidState().createLegacyBlock());
                        }
                        else if (context.isDefaultBlock(stateAt))
                        {
                            // If placing the topmost block
                            if (surfaceDepth == -1)
                            {
                                surfaceDepth = 0;
                                if (y > seaLevel + 2)
                                {
                                    context.setBlockState(y, grassState);
                                }
                                else if (y > seaLevel - 11 + randomHeight)
                                {
                                    context.setBlockState(y, sandState);
                                }
                                else
                                {
                                    context.setBlockState(y, rockState);
                                }
                            }
                            // Otherwise, at shallow depths
                            else if (surfaceDepth < 5 + randomHeight)
                            {
                                if (y > seaLevel - 11 + randomHeight)
                                {
                                    context.setBlockState(y, sandState);
                                    surfaceDepth++;
                                }
                                else
                                {
                                    context.setBlockState(y, rockState);
                                    surfaceDepth = 20; // Set deep enough to just go straight to stone next cycle
                                }
                            }
                            else
                            {
                                context.setBlockState(y, rockState);
                            }
                        }
                    }
                    return;
                }
            }
        }
        parent.buildSurface(context, startY, endY);
    }
}
