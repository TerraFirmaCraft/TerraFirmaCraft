/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import com.mojang.serialization.Codec;

public class NormalSurfaceBuilder extends ContextSurfaceBuilder<SurfaceBuilderBaseConfiguration>
{
    public NormalSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, int minSurfaceHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderBaseConfiguration config)
    {
        apply(context, x, z, startHeight, minSurfaceHeight, slope, temperature, rainfall, saltWater, SurfaceStates.TOP_SOIL, SurfaceStates.MID_SOIL, SurfaceStates.LOW_SOIL);
    }

    public void apply(SurfaceBuilderContext context, int x, int z, int startHeight, int minSurfaceHeight, double slope, float temperature, float rainfall, boolean saltWater, SurfaceState topState, SurfaceState midState, SurfaceState underState)
    {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int surfaceDepth = -1;
        int localX = x & 15;
        int localZ = z & 15;

        int surfaceY = 0;
        boolean underwaterLayer = false, firstLayer = false;
        SurfaceState surfaceState = SurfaceStates.RAW;

        pos.set(localX, startHeight, localZ);
        for (int y = startHeight; y >= minSurfaceHeight; --y)
        {
            pos.setY(y);

            final BlockState stateAt = context.getBlockState(pos);
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
                        surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 2, 0.1, -1);
                        if (surfaceDepth == -1)
                        {
                            surfaceDepth = 0;
                            context.setBlockState(pos, SurfaceStates.WATER, temperature, rainfall, saltWater);
                        }
                        else
                        {
                            context.setBlockState(pos, SurfaceStates.TOP_UNDERWATER, temperature, rainfall, saltWater);
                        }
                        surfaceState = SurfaceStates.TOP_UNDERWATER;
                        underwaterLayer = true;
                    }
                    else
                    {
                        surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 3, 0, -1);
                        if (surfaceDepth == -1)
                        {
                            surfaceDepth = 0;
                            context.setBlockState(pos, Blocks.AIR.defaultBlockState());
                        }
                        else
                        {
                            context.setBlockState(pos, topState, temperature, rainfall, saltWater);
                        }
                        surfaceState = midState;
                        underwaterLayer = false;
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(pos, surfaceState, temperature, rainfall, saltWater);
                    if (surfaceDepth == 0)
                    {
                        // Next subsurface layer
                        if (firstLayer)
                        {
                            firstLayer = false;
                            if (underwaterLayer)
                            {
                                surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 4, 0.4, 0);
                                surfaceState = SurfaceStates.LOW_UNDERWATER;
                            }
                            else
                            {
                                surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 7, 0.3, 0);
                                surfaceState = underState;
                            }
                        }
                    }
                }
            }
        }
    }
}