package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class SaltFlatsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = SaltFlatsSurfaceBuilder::new;

    public SaltFlatsSurfaceBuilder(long seed) {}

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (context.getBlockState(66).canBeReplaced())
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.SALT_MUD, SurfaceStates.DRY_MUD, SurfaceStates.DIRT, SurfaceStates.MUD, SurfaceStates.MUD);
        }
        else
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY);
        }
    }
}
