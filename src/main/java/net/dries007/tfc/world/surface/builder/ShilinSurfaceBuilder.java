/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class ShilinSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = ShilinSurfaceBuilder::new;

    private final Noise2D ridges;

    public ShilinSurfaceBuilder(Seed seed)
    {
        this.ridges = BiomeNoise.shilinRidges(seed.seed());
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double val = ridges.noise(context.pos().getX(), context.pos().getZ());
        if (val > 0.18)
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
        }
        else if (val > 0.09)
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL);
        }
        else
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY);
        }
    }
}