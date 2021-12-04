/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.SEA_LEVEL_Y;

public class BadlandsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory INSTANCE = BadlandsSurfaceBuilder::new;

    private final BlockState[] sandLayers;
    private final Noise2D heightVariationNoise;

    public BadlandsSurfaceBuilder(long seed)
    {
        sandLayers = new BlockState[32];

        // Alternating red + brown sand layers
        final Random random = new Random(seed);
        final BlockState redSand = TFCBlocks.SAND.get(SandBlockType.RED).get().defaultBlockState();
        final BlockState brownSand = TFCBlocks.SAND.get(SandBlockType.BROWN).get().defaultBlockState();

        boolean state = random.nextBoolean();
        for (int i = 0; i < sandLayers.length; i++)
        {
            if (random.nextInt(3) != 0)
            {
                state = !state;
            }
            sandLayers[i] = state ? redSand : brownSand;
        }

        heightVariationNoise = new OpenSimplex2D(seed).octaves(2).scaled(SEA_LEVEL_Y + 14, SEA_LEVEL_Y + 18).spread(0.5f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        float heightVariation = heightVariationNoise.noise(context.pos().getX(), context.pos().getZ());
        if (startY > heightVariation)
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.TOP_SOIL, SurfaceStates.MID_SOIL, SurfaceStates.LOW_SOIL);
        }
        else
        {
            buildSandySurface(context, startY, endY);
        }
    }

    private void buildSandySurface(SurfaceBuilderContext context, int startHeight, int minSurfaceHeight)
    {
        int surfaceDepth = -1;
        for (int y = startHeight; y >= minSurfaceHeight; --y)
        {
            BlockState stateAt = context.getBlockState(y);
            if (stateAt.isAir())
            {
                surfaceDepth = -1; // Reached air, reset surface depth
            }
            else if (context.isDefaultBlock(stateAt))
            {
                if (surfaceDepth == -1)
                {
                    surfaceDepth = 0; // Reached surface. Place top state and switch to subsurface layers
                    if (y < context.getSeaLevel() - 1)
                    {
                        context.setBlockState(y, SurfaceStates.TOP_UNDERWATER);
                    }
                    else
                    {
                        context.setBlockState(y, sandLayers[y % sandLayers.length]);
                    }
                }
            }
        }
    }
}