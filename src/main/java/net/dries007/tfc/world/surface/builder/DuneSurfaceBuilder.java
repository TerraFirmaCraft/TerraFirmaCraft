/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;

import static net.dries007.tfc.world.surface.SurfaceStates.*;

public enum DuneSurfaceBuilder implements SurfaceBuilderFactory.Invariant
{
    INSTANCE;

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        context.setSlope(context.getSlope() * (1 - context.weight()));
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SAND, SAND, SAND, SAND, SAND);
    }
}
