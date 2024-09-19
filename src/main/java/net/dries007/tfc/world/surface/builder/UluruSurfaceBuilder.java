/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class UluruSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = UluruSurfaceBuilder::new;

    private final Noise2D surfaceMaterialNoise;
    private final Noise2D heightNoise;

    public UluruSurfaceBuilder(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.02f);
        heightNoise = new OpenSimplex2D(seed + 71829341L).octaves(2).spread(0.1f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {

        final NormalSurfaceBuilder surfaceBuilder = NormalSurfaceBuilder.ROCKY;
        final long seed = 898763258L;

        final Noise2D rockLocationNoise = new OpenSimplex2D(seed)
            .octaves(2)
            .spread(0.012)
            //The min(), and dividing by a smaller number is intended to reduce the number of formations where the center doesn't reach at least one
            .map(x -> Math.min((Math.max(x, 0.2) - 0.2) / 0.4, 1));

        final Noise2D rockNoise = rockLocationNoise
            .map(Math::sqrt);

        final Noise2D erosionNoise = new OpenSimplex2D(seed)
            .octaves(4)
            .spread(0.18)
            .map(x -> 1 - Math.sqrt(Math.abs(x)))
            // Not safe against x < 0 but rockLocation noise should be non-negative
            .lazyProduct(rockLocationNoise.map(x -> Math.max(1 - x, 0)));

        final Noise2D combined = rockNoise.add(erosionNoise.map(x -> -x));

        final double val = combined.map(x -> x * 0.8 + 0.2).noise(context.pos().getX(), context.pos().getZ());
        if ( val > 0 )
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
        }
        else
        {
            SurfaceState sand = SoilSurfaceState.buildSand(false);
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, sand, sand, sand, sand, sand);
        }
    }
}