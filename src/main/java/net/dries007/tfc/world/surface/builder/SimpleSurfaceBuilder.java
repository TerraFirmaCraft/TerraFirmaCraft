/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;

import static net.dries007.tfc.world.surface.SurfaceStates.*;

public class SimpleSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory ROCKY_SHORE = seed -> new SimpleSurfaceBuilder(RAW, RAW, GRAVEL, true);
    public static final SurfaceBuilderFactory ROCKY_VOLCANIC_SOIL = seed -> new SimpleSurfaceBuilder(VOLCANIC_TOP_GRASS_TO_LOCAL_GRAVEL, VOLCANIC_MID_DIRT_TO_LOCAL_GRAVEL, GRAVEL, true);
    public static final SurfaceBuilderFactory VOLCANIC_SOIL = seed -> new SimpleSurfaceBuilder(VOLCANIC_TOP_GRASS_TO_LOCAL_GRAVEL, VOLCANIC_MID_DIRT_TO_LOCAL_GRAVEL, GRAVEL, true);

    private final SurfaceState top;
    private final SurfaceState mid;
    private final SurfaceState water;
    private final boolean rockySurfaceBuilder;

    public SimpleSurfaceBuilder(SurfaceState top, SurfaceState mid, SurfaceState water, boolean rockySurfaceBuilder)
    {
        this.top = top;
        this.mid = mid;
        this.water = water;
        this.rockySurfaceBuilder = rockySurfaceBuilder;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (rockySurfaceBuilder)
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, top, mid, mid, water, water);
        }
        else
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, top, mid, mid, water, water);
        }
    }
}
