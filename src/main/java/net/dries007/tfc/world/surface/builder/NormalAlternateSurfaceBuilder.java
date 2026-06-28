/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.surface.SurfaceStates.*;

public class NormalAlternateSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory SANDY = seed -> new NormalAlternateSurfaceBuilder(SurfaceStates.TOP_GRASS_TO_GRAVEL, SurfaceStates.MID_DIRT_TO_GRAVEL, SurfaceStates.UNDER_GRAVEL, false);
    public static final SurfaceBuilderFactory SANDY_ROCKY = seed -> new NormalAlternateSurfaceBuilder(SurfaceStates.TOP_GRASS_TO_GRAVEL, SurfaceStates.MID_DIRT_TO_GRAVEL, SurfaceStates.UNDER_GRAVEL, true);

    private final SurfaceState top;
    private final SurfaceState mid;
    private final SurfaceState water;
    private final boolean rocky;

    public NormalAlternateSurfaceBuilder(SurfaceState top, SurfaceState mid, SurfaceState water, boolean rocky)
    {
        this.top = top;
        this.mid = mid;
        this.water = water;
        this.rocky = rocky;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (rocky)
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, TOP_GRASS_TO_SAND, MID_DIRT_TO_SAND, UNDER_GRAVEL);
        }
        else
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, TOP_GRASS_TO_SAND, MID_DIRT_TO_SAND, UNDER_GRAVEL);
        }
    }
}
