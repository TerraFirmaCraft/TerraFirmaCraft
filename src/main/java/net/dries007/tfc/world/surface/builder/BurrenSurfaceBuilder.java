/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class BurrenSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = BurrenSurfaceBuilder::new;

    private final Noise2D surfaceMaterialNoise;
    private final Noise2D heightNoise;

    public BurrenSurfaceBuilder(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.02f);
        heightNoise = new OpenSimplex2D(seed + 71829341L).octaves(2).spread(0.1f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final NormalSurfaceBuilder surfaceBuilder = NormalSurfaceBuilder.ROCKY;
        final Noise2D crevices = new OpenSimplex2D(398767567L)
            .octaves(2)
            .spread(0.08f)
            .map(
                y -> {
                    y = Math.abs(y) < 0.15 ? 1 : 0;

                    return y;
                }
            );

        if (0 == crevices.noise(context.pos().getX(), context.pos().getZ()))
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
        }
        else
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.GRASS, SurfaceStates.DIRT, SurfaceStates.SANDSTONE_OR_GRAVEL);
        }
    }
}