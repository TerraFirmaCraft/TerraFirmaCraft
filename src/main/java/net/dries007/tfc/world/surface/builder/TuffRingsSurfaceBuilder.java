/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.TuffRingNoise;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class TuffRingsSurfaceBuilder implements SurfaceBuilder
{
    public static SurfaceBuilderFactory create(SurfaceBuilderFactory parent)
    {
        return seed -> new TuffRingsSurfaceBuilder(parent.apply(seed), seed);
    }

    private final SurfaceBuilder parent;
    private final TuffRingNoise tuffRingNoise;

    public TuffRingsSurfaceBuilder(SurfaceBuilder parent, Seed seed)
    {
        this.parent = parent;
        this.tuffRingNoise = new TuffRingNoise(seed);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (context.biome().hasTuffRings())
        {
            final float easing = tuffRingNoise.calculateEasing(context.pos().getX(), context.pos().getZ(), context.biome().getTuffRingRarity());
            if (easing > 0.6f)
            {
                if (startY < context.getSeaLevel() + 3)
                {
                    buildTuffSurface(context, startY, endY, SurfaceStates.VOLCANIC_SHORE_SAND, SurfaceStates.VOLCANIC_SHORE_SAND, SurfaceStates.TUFF, SurfaceStates.TUFF_GRAVEL);
                }
                else
                {
                    buildTuffSurface(context, startY, endY, SurfaceStates.VOLCANIC_TOP_GRASS_TO_LOCAL_GRAVEL, SurfaceStates.VOLCANIC_MID_DIRT_TO_LOCAL_GRAVEL, SurfaceStates.TUFF, SurfaceStates.TUFF_GRAVEL);
                }
                return;
            }
        }
        parent.buildSurface(context, startY, endY);
    }

    private void buildTuffSurface(SurfaceBuilderContext context, int startY, int endY, SurfaceState topState, SurfaceState midState, SurfaceState underState, SurfaceState underWaterState)
    {
        int surfaceDepth = -1;
        int surfaceY = 0;
        boolean underwaterLayer = false, firstLayer = false;
        SurfaceState surfaceState = underState;

        int tuffDepth = (int) (20 * context.weight());

        for (int y = startY; y >= endY; --y)
        {
            final BlockState stateAt = context.getBlockState(y);
            if (stateAt.isAir())
            {
                surfaceDepth = -1; // Reached air, reset surface depth
            }
            else if (context.isDefaultBlock(stateAt))
            {
                // All in this if statement only occurs on the first cycle/when air resets the cycle
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
                            context.setBlockState(y, SurfaceStates.TUFF);
                        }
                        else if (surfaceDepth == -1)
                        {
                            // Place one subsurface layer, skipping the top layer entirely
                            surfaceDepth = 0;
                            context.setBlockState(y, underWaterState);
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
                            context.setBlockState(y, SurfaceStates.TUFF);
                            surfaceDepth = 0;
                        }
                        else if (surfaceDepth == -1)
                        {
                            // Place one subsurface layer, skipping the top layer entirely
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
                            }
                            else
                            {
                                surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 0);
                                surfaceState = underState;
                            }
                        }
                    }
                }
                else if (tuffDepth > 0)
                {
                    context.setBlockState(y, SurfaceStates.TUFF);
                    tuffDepth--;
                }
            }
        }
    }
}
