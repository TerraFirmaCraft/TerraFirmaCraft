package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;

public class AtollSurfaceBuilder extends ShoreSurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = AtollSurfaceBuilder::new;

    public AtollSurfaceBuilder(long seed)
    {
        super(seed);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (context.getSlope() < 5)
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY);
        }
        else
        {
            super.buildSurface(context, startY, endY);
        }
    }
}
