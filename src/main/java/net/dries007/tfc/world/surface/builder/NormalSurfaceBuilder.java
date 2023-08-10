/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public enum NormalSurfaceBuilder implements SurfaceBuilderFactory.Invariant
{
    INSTANCE(-1),
    ROCKY(-3);

    private final int subsurfaceMinDepth;

    NormalSurfaceBuilder(int subsurfaceMinDepth)
    {
        this.subsurfaceMinDepth = subsurfaceMinDepth;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        buildSurface(context, startY, endY, SurfaceStates.GRASS, SurfaceStates.DIRT, SurfaceStates.SANDSTONE_OR_GRAVEL);
    }

    public void buildSurface(SurfaceBuilderContext context, int startY, int endY, SurfaceState topState, SurfaceState midState, SurfaceState underState)
    {
        buildSurface(context, startY, endY, topState, midState, underState, SurfaceStates.SAND_OR_GRAVEL, SurfaceStates.SANDSTONE_OR_GRAVEL);
    }

    public void buildSurface(SurfaceBuilderContext context, int startY, int endY, SurfaceState topState, SurfaceState midState, SurfaceState underState, SurfaceState underWaterState, SurfaceState thinUnderWaterState)
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
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 2, -1);
                        if (surfaceDepth < -1)
                        {
                            // No surface layers
                            surfaceDepth = 0;
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
                        surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 3, subsurfaceMinDepth);
                        if (surfaceDepth < -1)
                        {
                            // No surface layers
                            surfaceDepth = 0;
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
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 4, 0);
                                surfaceState = thinUnderWaterState;
                            }
                            else
                            {
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 7, 0);
                                surfaceState = underState;
                            }
                        }
                    }
                }
            }
        }
    }
}