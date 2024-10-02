/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;


import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

public class LowlandsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = LowlandsSurfaceBuilder::new;

    private final Noise2D surfaceMaterialNoise;

    public LowlandsSurfaceBuilder(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.04f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final float noise = (float) surfaceMaterialNoise.noise(context.pos().getX(), context.pos().getZ()) * 0.9f + context.random().nextFloat() * 0.1f;
        final SurfaceState mud = context.groundwater() < 130f ? SurfaceStates.DRY_MUD : SurfaceStates.MUD;
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, noise < 0f ? SurfaceStates.GRASS : mud, mud, SurfaceStates.DIRT, noise > 0 ? SurfaceStates.DIRT : mud, mud);
    }
}
