package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class AtollSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = AtollSurfaceBuilder::new;

    private final Noise2D variantNoise;

    public AtollSurfaceBuilder(long seed)
    {
        this.variantNoise = new OpenSimplex2D(seed).octaves(2).spread(0.04f).abs();
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final float variantNoiseValue = (float) variantNoise.noise(context.pos().getX(), context.pos().getZ());
        NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, variantNoiseValue > 0.9 ? SurfaceStates.SANDY_WHEN_NEAR_SEA_LEVEL : SurfaceStates.GRASS, SurfaceStates.GRAVEL, SurfaceStates.SANDSTONE_OR_GRAVEL);
    }
}
