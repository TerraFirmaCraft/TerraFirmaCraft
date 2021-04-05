/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.util.Lazy;

import com.mojang.serialization.Codec;

public class NormalSurfaceBuilder extends ContextSurfaceBuilder<SurfaceBuilderConfig>
{
    public NormalSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderConfig config)
    {
        apply(context, biome, x, z, startHeight, noise, slope, temperature, rainfall, saltWater, config, SurfaceStates.TOP_SOIL, SurfaceStates.MID_SOIL, SurfaceStates.LOW_SOIL);
    }

    @SuppressWarnings("deprecation")
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderConfig config, ISurfaceState topState, ISurfaceState midState, ISurfaceState underState)
    {
        final BlockPos.Mutable pos = new BlockPos.Mutable();
        int surfaceDepth = -1;
        int localX = x & 15;
        int localZ = z & 15;

        int surfaceY = 0;
        boolean underwaterLayer = false, firstLayer = false;
        ISurfaceState surfaceState = SurfaceStates.RAW;

        for (int y = startHeight; y >= 0; --y)
        {
            pos.set(localX, y, localZ);
            BlockState stateAt = context.getBlockState(pos);
            if (stateAt.isAir())
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
            }
            else if (stateAt.getBlock() == context.getDefaultBlock().getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceY = y;
                    firstLayer = true;
                    if (y < context.getSeaLevel() - 1)
                    {
                        surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 2, 0.1);
                        context.setBlockState(pos, SurfaceStates.TOP_UNDERWATER, temperature, rainfall, saltWater);
                        surfaceState = SurfaceStates.TOP_UNDERWATER;
                        underwaterLayer = true;
                    }
                    else
                    {
                        surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 3, 0);
                        context.setBlockState(pos, topState, temperature, rainfall, saltWater);
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
                                surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 4, 0.4);
                                surfaceState = SurfaceStates.LOW_UNDERWATER;
                            }
                            else
                            {
                                surfaceDepth = calculateAltitudeSlopeSurfaceDepth(surfaceY, slope, 7, 0.3);
                                surfaceState = underState;
                            }
                        }
                    }
                }
                else if (surfaceDepth == 0)
                {
                    // Underground layers
                    context.setBlockState(pos, SurfaceStates.RAW, temperature, rainfall, saltWater);
                }
            }
            else // Default fluid
            {
                context.setBlockState(pos, SurfaceStates.WATER, temperature, rainfall, saltWater);
            }
        }
    }
}