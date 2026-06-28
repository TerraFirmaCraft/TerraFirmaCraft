/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;

public class TuyaSurfaceBuilder implements SurfaceBuilder
{
    public static SurfaceBuilderFactory create(SurfaceBuilderFactory parent)
    {
        return seed -> new TuyaSurfaceBuilder(parent.apply(seed), seed);
    }

    private final SurfaceBuilder parent;
    private final Seed seed;

    private final Noise2D heightNoise;

    public TuyaSurfaceBuilder(SurfaceBuilder parent, Seed seed)
    {
        this.parent = parent;
        this.seed = seed;
        this.heightNoise = new OpenSimplex2D(seed.next()).octaves(2).spread(0.1f).scaled(-4, 4);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        if (context.tuyaBiome().hasTuyas())
        {
            final CenteredFeatureNoiseSampler sampler = CenteredFeatureNoise.tuya(seed);
            final float easing = sampler.calculateEasing(context.pos(), context.tuyaBiome());
            if (1 - easing < 0.16f)
            {
                buildVolcanicSurface(context, startY, endY);
                return;
            }
        }
        parent.buildSurface(context, startY, endY);
    }

    private void buildVolcanicSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final BlockState basalt = TFCBlocks.ROCK_BLOCKS.get(Rock.BASALT).get(Rock.BlockType.RAW).get().defaultBlockState();
        final int prevolcanicHeight = context.getPreVolcanicHeight();

        int surfaceDepth = -1;
        for (int y = startY; y >= endY; --y)
        {
            BlockState stateAt = context.getBlockState(y);
            if (stateAt.isAir())
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
            }
            else if (context.isDefaultBlock(stateAt))
            {
                if (y > prevolcanicHeight)
                {
                    if (surfaceDepth == -1)
                    {
                        // Reached surface. Place top state and switch to subsurface layers
                        surfaceDepth = 40;
                        context.setBlockState(y, basalt);
                    }
                    else if (surfaceDepth > 0)
                    {
                        // Subsurface layers
                        surfaceDepth--;
                        context.setBlockState(y, basalt);
                    }
                }
            }
        }
    }
}
