/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class LowlandsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = seed -> new LowlandsSurfaceBuilder();

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.MUD, SurfaceStates.GRAVEL, SurfaceStates.LOW_SOIL);
    }
}
