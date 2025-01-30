/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;

import static net.dries007.tfc.world.TFCChunkGenerator.*;
import static net.dries007.tfc.world.surface.SurfaceStates.*;

public class GrassyDunesSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = GrassyDunesSurfaceBuilder::new;

    private final Noise2D grassHeightVariationNoise;

    public GrassyDunesSurfaceBuilder(Seed seed)
    {
        grassHeightVariationNoise = new OpenSimplex2D(seed.next()).octaves(2).scaled(SEA_LEVEL_Y + 8, SEA_LEVEL_Y + 14).spread(0.08f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double heightVariation = grassHeightVariationNoise.noise(context.pos().getX(), context.pos().getZ());
        final double trueSlope = context.getSlope();

        context.setSlope(trueSlope * (1 - context.weight()));
        if (startY > heightVariation && trueSlope < 5)
        {
            SurfaceState grass = SoilSurfaceState.buildDryDirt(SoilBlockType.GRASS);
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, grass, SAND, SAND, SAND, SAND);
        }
        else
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SAND, SAND, SAND, SAND, SAND);
        }
    }
}