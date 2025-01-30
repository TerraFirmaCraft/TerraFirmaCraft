/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.BiomeNoise;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

public class BurrenSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = BurrenSurfaceBuilder::new;

    private final Noise2D crevices;

    public BurrenSurfaceBuilder(Seed seed)
    {
        this.crevices = BiomeNoise.burrenCrevices(seed.seed());
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (crevices.noise(context.pos().getX(), context.pos().getZ()) + 0.3 * context.weight() <= 0.40)
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY);
        }
        else
        {
            NormalSurfaceBuilder.ROCKY.buildSurface(context, startY, endY, SurfaceStates.RAW, SurfaceStates.RAW, SurfaceStates.RAW);
        }
    }
}