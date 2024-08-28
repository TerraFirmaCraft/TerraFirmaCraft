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
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class BadlandsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory NORMAL = seed -> new BadlandsSurfaceBuilder(false, seed);
    public static final SurfaceBuilderFactory INVERTED = seed -> new BadlandsSurfaceBuilder(true, seed);

    private static final int PRIMARY_SIZE = 8;
    private static final int SECONDARY_SIZE = 5;
    private static final int UNCOMMON_SIZE = 3;
    private static final int LAYER_SIZE = PRIMARY_SIZE + SECONDARY_SIZE + UNCOMMON_SIZE; // 16

    private static void fill(Random random, BlockState[] sandLayers, BlockState[] sandstoneLayers, SandBlockType primary, SandBlockType secondary, SandBlockType uncommon)
    {
        fill(random, sandLayers, primary, secondary, uncommon, BadlandsSurfaceBuilder::sand);
        fill(random, sandstoneLayers, primary, secondary, uncommon, BadlandsSurfaceBuilder::sandstone);
    }

    private static void fill(Random random, BlockState[] layers, SandBlockType primary, SandBlockType secondary, SandBlockType uncommon, Function<SandBlockType, BlockState> block)
    {
        // 8 - 5 - 3 of primary, secondary, and uncommon across the 16 block sequence.
        // This should make it near impossible to get completely 'undesirable' combinations
        Arrays.fill(layers, 0, PRIMARY_SIZE, block.apply(primary));
        Arrays.fill(layers, PRIMARY_SIZE, PRIMARY_SIZE + SECONDARY_SIZE, block.apply(secondary));
        Arrays.fill(layers, PRIMARY_SIZE + SECONDARY_SIZE, LAYER_SIZE, block.apply(uncommon));
        Collections.shuffle(Arrays.asList(layers), random);
    }

    private static BlockState sand(SandBlockType color)
    {
        return TFCBlocks.SAND.get(color).get().defaultBlockState();
    }

    private static BlockState sandstone(SandBlockType color)
    {
        return TFCBlocks.SANDSTONE.get(color).get(SandstoneBlockType.RAW).get().defaultBlockState();
    }

    private final boolean inverted;
    private final BlockState[] sandLayers0, sandLayers1;
    private final BlockState[] sandstoneLayers0, sandstoneLayers1;
    private final float[] layerThresholds;

    private final Noise2D grassHeightVariationNoise;
    private final Noise2D sandHeightOffsetNoise;
    private final Noise2D sandStyleNoise;

    public BadlandsSurfaceBuilder(boolean inverted, long seed)
    {
        this.inverted = inverted;

        final Random random = new Random(seed);

        sandLayers0 = new BlockState[LAYER_SIZE];
        sandLayers1 = new BlockState[LAYER_SIZE];

        sandstoneLayers0 = new BlockState[LAYER_SIZE];
        sandstoneLayers1 = new BlockState[LAYER_SIZE];

        layerThresholds = new float[LAYER_SIZE];

        fill(random, sandLayers0, sandstoneLayers0, SandBlockType.RED, SandBlockType.BROWN, SandBlockType.YELLOW);
        fill(random, sandLayers1, sandstoneLayers1, SandBlockType.BROWN, SandBlockType.YELLOW, SandBlockType.WHITE);

        for (int i = 0; i < LAYER_SIZE; i++)
        {
            layerThresholds[i] = random.nextFloat();
        }

        grassHeightVariationNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(SEA_LEVEL_Y + 14, SEA_LEVEL_Y + 18).spread(0.5f);
        sandHeightOffsetNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(0, 6).spread(0.0014f);
        sandStyleNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(-0.3f, 1.3f).spread(0.0003f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double heightVariation = grassHeightVariationNoise.noise(context.pos().getX(), context.pos().getZ());
        final double weightVariation = (1.0 - context.weight()) * 23.0;
        final double rainfallVariation = Mth.clampedMap(context.groundwater(), 100, 500, 0, 22);

        if (inverted
            ? startY + 5 < heightVariation + weightVariation + rainfallVariation
            : startY - 5 > heightVariation - weightVariation - rainfallVariation)
        {
            NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.GRASS, SurfaceStates.DIRT, SurfaceStates.SANDSTONE_OR_GRAVEL);
        }
        else
        {
            buildSandySurface(context, startY, endY);
        }
    }

    private void buildSandySurface(SurfaceBuilderContext context, int startHeight, int minSurfaceHeight)
    {
        final float style = (float) sandStyleNoise.noise(context.pos().getX(), context.pos().getZ());
        final int height = (int) sandHeightOffsetNoise.noise(context.pos().getX(), context.pos().getZ());

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
                    // Reached surface. Place top state and switch to subsurface layers
                    if (y < context.getSeaLevel() - 1)
                    {
                        context.setBlockState(y, SurfaceStates.SAND_OR_GRAVEL);
                    }
                    else
                    {
                        context.setBlockState(y, sampleLayer(sandLayers0, sandLayers1, y + height, style));
                        surfaceDepth = inverted ? 9 : 3;
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(y, sampleLayer(sandstoneLayers0, sandstoneLayers1, y + height, style));
                }
            }
        }
    }

    private BlockState sampleLayer(BlockState[] layers0, BlockState[] layers1, int y, float threshold)
    {
        final int index = Math.floorMod(y, LAYER_SIZE);
        return (layerThresholds[index] < threshold ? layers0 : layers1)[index];
    }
}