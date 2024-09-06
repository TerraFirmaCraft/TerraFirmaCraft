/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SoilSurfaceState;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceState;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class GrassyDunesSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = seed -> new GrassyDunesSurfaceBuilder(seed);

    private final Noise2D grassHeightVariationNoise;

    public GrassyDunesSurfaceBuilder(long seed)
    {
        final Random random = new Random(seed);

        grassHeightVariationNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(SEA_LEVEL_Y + 8, SEA_LEVEL_Y + 12).spread(0.3f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double heightVariation = grassHeightVariationNoise.noise(context.pos().getX(), context.pos().getZ());

        context.setSlope(0);
        SurfaceState sand = SoilSurfaceState.buildSand(false);

        if (startY > heightVariation)
        {
            SurfaceState grass = SoilSurfaceState.buildDryDirt(SoilBlockType.GRASS);
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, grass, sand, sand, sand, sand);
        }
        else
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, sand, sand, sand, sand, sand);
        }
    }
}