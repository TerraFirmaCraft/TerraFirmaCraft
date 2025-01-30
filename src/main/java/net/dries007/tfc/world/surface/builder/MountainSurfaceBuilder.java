/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class MountainSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = MountainSurfaceBuilder::new;

    private final Noise2D surfaceMaterialNoise;
    private final Noise2D heightNoise;

    public MountainSurfaceBuilder(Seed seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.02f);
        heightNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.1f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double heightNoise = this.heightNoise.noise(context.pos().getX(), context.pos().getZ()) * 4f + startY;
        if (heightNoise > 130)
        {
            final double surfaceMaterialValue = surfaceMaterialNoise.noise(context.pos().getX(), context.pos().getZ()) + 0.1f * context.random().nextFloat() - 0.05f;
            if (surfaceMaterialValue > 0.3f)
            {
                NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.COBBLE, SurfaceStates.COBBLE, SurfaceStates.RAW);
            }
            else if (surfaceMaterialValue < -0.3f)
            {
                NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL, SurfaceStates.RAW);
            }
            else
            {
                NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
            }
        }
        else
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.GRASS, SurfaceStates.DIRT, SurfaceStates.SANDSTONE_OR_GRAVEL);
        }
    }
}