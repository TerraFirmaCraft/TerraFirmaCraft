/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public enum NormalSurfaceBuilder implements SurfaceBuilderFactory.Invariant
{
    INSTANCE;

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        buildSurface(context, startY, endY, SurfaceStates.TOP_SOIL, SurfaceStates.MID_SOIL, SurfaceStates.LOW_SOIL);
    }

    public void buildSurface(SurfaceBuilderContext context, int startY, int endY, SurfaceState topState, SurfaceState midState, SurfaceState underState)
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
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 2, 0.1, -1);
                        if (surfaceDepth == -1)
                        {
                            surfaceDepth = 0;
                            context.setBlockState(y, SurfaceStates.WATER);
                        }
                        else
                        {
                            context.setBlockState(y, SurfaceStates.TOP_UNDERWATER);
                        }
                        surfaceState = SurfaceStates.TOP_UNDERWATER;
                        underwaterLayer = true;
                    }
                    else
                    {
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 3, 0, -1);
                        if (surfaceDepth == -1)
                        {
                            surfaceDepth = 0;
                            context.setBlockState(y, Blocks.AIR.defaultBlockState());
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
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 4, 0.4, 0);
                                surfaceState = SurfaceStates.LOW_UNDERWATER;
                            }
                            else
                            {
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 7, 0.3, 0);
                                surfaceState = underState;
                            }
                        }
                    }
                }
            }
        }
    }
}