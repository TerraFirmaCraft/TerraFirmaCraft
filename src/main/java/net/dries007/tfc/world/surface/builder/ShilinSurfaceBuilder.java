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

public class ShilinSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = ShilinSurfaceBuilder::new;

    private final Noise2D surfaceMaterialNoise;
    private final Noise2D heightNoise;

    public ShilinSurfaceBuilder(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.02f);
        heightNoise = new OpenSimplex2D(seed + 71829341L).octaves(2).spread(0.1f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final NormalSurfaceBuilder surfaceBuilder = NormalSurfaceBuilder.ROCKY;

        final double widthTop = 0.1;
        final double widthBot = 0.25;
        final double scale = 15;

        //TODO: This function should actually be moved somewhere that both this and ShilinSurfaceBuilder can use it
        //TODO: Also maybe should use world seed if that can be accessed from both places?
        final Noise2D ridges = new OpenSimplex2D(398767567L)
            .octaves(2)
            .spread(0.08f)
            .map(
                y -> {
                    y = Math.abs(y);
                    y = y < widthTop ? 1 : y < widthBot ? 1 + (0.67 * (y - widthTop) / (widthTop - widthBot)) : 0;
                    y = y * scale;

                    return y;
                }
            );

        final Noise2D cuts = new OpenSimplex2D(45764379L)
            .octaves(2)
            .spread(0.04f)
            .map(
                y -> {
                    y = Math.abs(y);
                    y = y < widthTop * 0.65 ? 1 : y < widthBot * 1.2 ? 1 + ((y - widthTop * 0.65) / (widthTop * 0.65 - widthBot * 1.2)) : 0;

                    return 1 - y;
                }
            );

        final double val = ridges.lazyProduct(cuts).noise(context.pos().getX(), context.pos().getZ());
        if (val > 0.18)
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
        }
        else if (val > 0.09)
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL, SurfaceStates.GRAVEL);
        }
        else
        {
            surfaceBuilder.buildSurface(context, startY, endY, SurfaceStates.GRASS, SurfaceStates.DIRT, SurfaceStates.SANDSTONE_OR_GRAVEL);
        }
    }
}