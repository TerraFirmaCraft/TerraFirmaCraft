/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;


import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;

public class DuneSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = DuneSurfaceBuilder::new;

    protected DuneSurfaceBuilder(long seed) {}

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        context.setSlope(context.getSlope() * (1 - context.weight()));
        SurfaceState sand = SoilSurfaceState.buildSand(false);
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, sand, sand, sand, sand, sand);
    }
}
