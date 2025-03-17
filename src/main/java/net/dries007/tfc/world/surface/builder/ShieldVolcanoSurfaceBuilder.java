/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;


import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class ShieldVolcanoSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory ACTIVE = seed -> new ShieldVolcanoSurfaceBuilder(seed, true, false);
    public static final SurfaceBuilderFactory DORMANT = seed -> new ShieldVolcanoSurfaceBuilder(seed, false, false);
    public static final SurfaceBuilderFactory SHORE = seed -> new ShieldVolcanoSurfaceBuilder(seed, false, true);

    private final boolean hasLavaFlows;
    private final boolean sandy;
    private final long seed;
    private final Noise2D smoothNoise;

    ShieldVolcanoSurfaceBuilder(Seed seed, boolean hasLavaFlows, boolean sandy)
    {
        this.hasLavaFlows = hasLavaFlows;
        this.sandy = sandy;
        this.seed = seed.seed();
        this.smoothNoise = new OpenSimplex2D(this.seed).octaves(2).spread(0.25);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final int x = context.pos().getX();
        final int z = context.pos().getZ();
        final SurfaceState top;
        final SurfaceState mid;
        final SurfaceState bot;
        final SurfaceState underwater;

        if (sandy)
        {
            top = SurfaceStates.RARE_SHORE_SAND;
            mid = SurfaceStates.RARE_SHORE_SAND;
            bot = SurfaceStates.RARE_SHORE_SANDSTONE;
            underwater = SurfaceStates.RARE_SHORE_SAND;
        }
        else
        {
            top = SurfaceStates.VOLCANIC_TOP_GRASS_TO_GRAVEL;
            mid = SurfaceStates.VOLCANIC_MID_DIRT_TO_GRAVEL;
            bot = SurfaceStates.BASALT_GRAVEL;
            underwater = SurfaceStates.BASALT_GRAVEL;
        }

        if (!hasLavaFlows)
        {
            buildSurface(context, startY, endY, top, mid, bot, underwater);
        }
        else
        {
            final double noiseValue = this.smoothNoise.noise(x, z);
            final Noise2D lavaFlows = BiomeNoise.lavaFlow(seed);
            final double flowValue = lavaFlows.noise(x, z);

            if (flowValue < 0.40)
                buildSurface(context, startY, endY, top, mid, bot, underwater);
            else if (flowValue < 0.50)
            {
                if (noiseValue > 0)
                    buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_GRAVEL, SurfaceStates.BASALT_GRAVEL, SurfaceStates.BASALT, SurfaceStates.BASALT_GRAVEL);
                else
                    buildSurface(context, startY, endY, top, mid, bot, underwater);
            }
            else if (flowValue < 0.75)
            {
                if (noiseValue > 0)
                    buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_GRAVEL, SurfaceStates.BASALT_GRAVEL, SurfaceStates.BASALT, SurfaceStates.BASALT_GRAVEL);
                else
                    buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_COBBLE, SurfaceStates.BASALT_COBBLE, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
            }
            else
            {
                if (noiseValue > -0.6)
                    buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
                else
                    buildSurface(context, startY, endY, SurfaceStates.SNOWY_BASALT_COBBLE, SurfaceStates.BASALT_COBBLE, SurfaceStates.BASALT, SurfaceStates.BASALT_COBBLE);
            }
        }
    }

    public void buildSurface(SurfaceBuilderContext context, int startY, int endY, SurfaceState topState, SurfaceState midState, SurfaceState underState, SurfaceState underWaterState)
    {
        int surfaceDepth = -1;
        int surfaceY = 0;
        boolean underwaterLayer = false, firstLayer = false;
        SurfaceState surfaceState = SurfaceStates.BASALT;

        int basaltDepth = (int) (20 * context.weight());

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
                            context.setBlockState(y, SurfaceStates.BASALT);
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
                            context.setBlockState(y, SurfaceStates.BASALT);
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
                            surfaceDepth = context.calculateAltitudeSlopeSurfaceDepth(surfaceY, 0);
                            if (underwaterLayer)
                            {
                                surfaceState = underState;
                            }
                        }
                    }
                }
                else if (basaltDepth > 0)
                {
                    context.setBlockState(y, SurfaceStates.BASALT);
                    basaltDepth--;
                }
            }
        }
    }
}
