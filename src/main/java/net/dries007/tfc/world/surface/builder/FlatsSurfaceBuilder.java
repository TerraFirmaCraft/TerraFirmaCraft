/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class FlatsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory MUDDY = seed -> new FlatsSurfaceBuilder(seed, SurfaceStates.DRY_MUD, SurfaceStates.DRY_MUD, SurfaceStates.MUD);
    public static final SurfaceBuilderFactory SALTY = seed -> new FlatsSurfaceBuilder(seed, SurfaceStates.SALT_MUD, SurfaceStates.DRY_MUD, SurfaceStates.MUD);

    private final SurfaceState top;
    private final SurfaceState mid;
    private final SurfaceState water;

    public FlatsSurfaceBuilder(long seed, SurfaceState top, SurfaceState mid, SurfaceState water)
    {
        this.top = top;
        this.mid = mid;
        this.water = water;
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (startY < 66)
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, top, mid, mid, water, water);
        }
        else
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY);
        }
    }
}
