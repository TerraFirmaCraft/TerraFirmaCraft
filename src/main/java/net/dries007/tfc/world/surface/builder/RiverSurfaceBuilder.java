/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeExtension;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class RiverSurfaceBuilder extends ShoreSurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = RiverSurfaceBuilder::new;

    protected RiverSurfaceBuilder(Seed seed)
    {
        super(seed);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BiomeExtension biome = context.originalBiome();
        if (biome.isShore())
        {
            super.buildSurface(context, startY, endY);
        }
        else if (!biome.hasSandyRiverShores())
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY);
        }
        else
        {
            SurfaceState state = SurfaceStates.GRAVEL;
            if (context.getSlope() < 2)
            {
                state = SurfaceStates.GRASS;
            }
            else if (context.getSlope() < 5)
            {
                state = SurfaceStates.RIVER_SAND;
            }
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, state, SurfaceStates.GRAVEL, SurfaceStates.SANDSTONE_OR_GRAVEL);
        }
    }
}
