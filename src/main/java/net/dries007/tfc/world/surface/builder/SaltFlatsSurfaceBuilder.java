package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class SaltFlatsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = SaltFlatsSurfaceBuilder::new;

    private final Noise2D surfaceMaterialNoise;

    public SaltFlatsSurfaceBuilder(long seed)
    {
        surfaceMaterialNoise = new OpenSimplex2D(seed).octaves(2).spread(0.04f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final float noise = (float) surfaceMaterialNoise.noise(context.pos().getX(), context.pos().getZ()) * 0.9f + context.random().nextFloat() * 0.1f;
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SALT_MUD, SurfaceStates.DRY_MUD, SurfaceStates.DIRT, SurfaceStates.MUD, SurfaceStates.MUD);
    }
}
