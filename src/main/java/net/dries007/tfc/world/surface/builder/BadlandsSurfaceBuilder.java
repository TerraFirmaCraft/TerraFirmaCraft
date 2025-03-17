/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import java.util.Arrays;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;
import net.dries007.tfc.world.settings.RockSettings;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;
import net.dries007.tfc.world.surface.SurfaceStates;

import static net.dries007.tfc.world.TFCChunkGenerator.*;

public class BadlandsSurfaceBuilder implements SurfaceBuilder
{
    public static final SurfaceBuilderFactory NORMAL = seed -> new BadlandsSurfaceBuilder(false, false, 0, seed);
    public static final SurfaceBuilderFactory MESAS = seed -> new BadlandsSurfaceBuilder(true, false, 3, seed);
    public static final SurfaceBuilderFactory HOODOOS = seed -> new BadlandsSurfaceBuilder(true, false, 1.6, seed);
    public static final SurfaceBuilderFactory WARPED = seed -> new BadlandsSurfaceBuilder(true, true, 0, seed);


    private static final int PRIMARY_SIZE = 8;
    private static final int SECONDARY_SIZE = 5;
    private static final int UNCOMMON_SIZE = 3;
    private static final int LAYER_SIZE = PRIMARY_SIZE + SECONDARY_SIZE + UNCOMMON_SIZE; // 16

    private static void fillBlocks(RandomSource random, BlockState[] sandLayers, BlockState[] sandstoneLayers, BlockState primaryStone, BlockState primarySand, BlockState secondaryStone, BlockState secondarySand, BlockState uncommonStone, BlockState uncommonSand)
    {
        fill(random, sandLayers, primarySand, secondarySand, uncommonSand);
        fill(random, sandstoneLayers, primaryStone, secondaryStone, uncommonStone);
    }

    private static void fillSand(RandomSource random, BlockState[] sandLayers, BlockState[] sandstoneLayers, SandBlockType primary, SandBlockType secondary, SandBlockType uncommon)
    {
        fill(random, sandLayers, sand(primary), sand(secondary), sand(uncommon));
        fill(random, sandstoneLayers, sandstone(primary), sandstone(secondary), sandstone(uncommon));
    }

    private static void fill(RandomSource random, BlockState[] layers, BlockState primary, BlockState secondary, BlockState uncommon)
    {
        // 8 - 5 - 3 of primary, secondary, and uncommon across the 16 block sequence.
        // This should make it near impossible to get completely 'undesirable' combinations
        Arrays.fill(layers, 0, PRIMARY_SIZE, primary);
        Arrays.fill(layers, PRIMARY_SIZE, PRIMARY_SIZE + SECONDARY_SIZE, secondary);
        Arrays.fill(layers, PRIMARY_SIZE + SECONDARY_SIZE, LAYER_SIZE, uncommon);

        // Random shuffle
        Helpers.shuffleArray(layers, random);
    }

    private static BlockState sand(SandBlockType color)
    {
        return TFCBlocks.SAND.get(color).get().defaultBlockState();
    }

    private static BlockState sandstone(SandBlockType color)
    {
        return TFCBlocks.SANDSTONE.get(color).get(SandstoneBlockType.RAW).get().defaultBlockState();
    }

    private static BlockState gravel(Rock rock)
    {
        return rock.getBlock(Rock.BlockType.GRAVEL).get().defaultBlockState();
    }

    private static BlockState rock(Rock rock)
    {
        return rock.getBlock(Rock.BlockType.RAW).get().defaultBlockState();
    }

    private final boolean inverted;
    private final boolean dippingStrata;
    private final double maxSoilSlope;
    private final BlockState[] sandLayers0, sandLayers1, sandLayersKarst, sandLayersVolcanic;
    private final BlockState[] sandstoneLayers0, sandstoneLayers1, sandstoneLayersKarst, sandstoneLayersVolcanic;
    private final float[] layerThresholds;

    private final Noise2D grassHeightVariationNoise;
    private final Noise2D sandHeightOffsetNoise;
    private final Noise2D sandStyleNoise;

    private BlockState[] getKarstOrVolcanicSandLayer(BlockState[] defaultLayer, boolean karst, boolean volcanic)
    {
        return karst ? sandLayersKarst : volcanic ? sandLayersVolcanic : defaultLayer;
    }

    private BlockState[] getKarstOrVolcanicSandstoneLayer(BlockState[] defaultLayer, boolean karst, boolean volcanic)
    {
        return karst ? sandstoneLayersKarst : volcanic ? sandstoneLayersVolcanic : defaultLayer;
    }

    public BadlandsSurfaceBuilder(boolean inverted, boolean dippingStrata, double maxSoilSlope, Seed seed)
    {
        this.inverted = inverted;
        this.dippingStrata = dippingStrata;
        this.maxSoilSlope = maxSoilSlope;

        final RandomSource random = seed.fork();

        if (dippingStrata)
        {
            sandHeightOffsetNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(-250, 250).spread(0.005);
        }
        else
        {
            sandHeightOffsetNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(0, 6).spread(0.0014f);
        }

        sandLayers0 = new BlockState[LAYER_SIZE];
        sandLayers1 = new BlockState[LAYER_SIZE];
        sandLayersKarst = new BlockState[LAYER_SIZE];
        sandLayersVolcanic = new BlockState[LAYER_SIZE];

        sandstoneLayers0 = new BlockState[LAYER_SIZE];
        sandstoneLayers1 = new BlockState[LAYER_SIZE];
        sandstoneLayersKarst = new BlockState[LAYER_SIZE];
        sandstoneLayersVolcanic = new BlockState[LAYER_SIZE];

        layerThresholds = new float[LAYER_SIZE];

        fillSand(random, sandLayers0, sandstoneLayers0, SandBlockType.RED, SandBlockType.BROWN, SandBlockType.YELLOW);
        fillSand(random, sandLayers1, sandstoneLayers1, SandBlockType.BROWN, SandBlockType.YELLOW, SandBlockType.WHITE);
        fillSand(random, sandLayersKarst, sandstoneLayersKarst, SandBlockType.RED, SandBlockType.YELLOW, SandBlockType.WHITE);
        fillBlocks(random, sandLayersVolcanic, sandstoneLayersVolcanic, sandstone(SandBlockType.BLACK), sand(SandBlockType.BLACK), rock(Rock.TUFF), gravel(Rock.TUFF), sandstone(SandBlockType.RED), sand(SandBlockType.RED));

        for (int i = 0; i < LAYER_SIZE; i++)
        {
            layerThresholds[i] = random.nextFloat();
        }

        grassHeightVariationNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(SEA_LEVEL_Y + 14, SEA_LEVEL_Y + 18).spread(0.5f);
        sandStyleNoise = new OpenSimplex2D(random.nextLong()).octaves(2).scaled(-0.3f, 1.3f).spread(0.0003f);
    }

    @Override
    public void buildSurface(SurfaceBuilderContext context, int startY, int endY)
    {
        final double heightVariation = grassHeightVariationNoise.noise(context.pos().getX(), context.pos().getZ());
        final double weightVariation = (1.0 - context.weight()) * 23.0;
        final double rainfallVariation = Mth.clampedMap(context.groundWater(), 100, 500, 0, 22);

        final RockSettings rock = context.getRock();
        final boolean karst = rock.karst().isPresent() ? rock.karst().get() : false;
        final boolean volcanic = rock.mafic().isPresent() ? rock.mafic().get() : false;

        if (inverted)
        {
            final int shift = dippingStrata ? 2 : 5;
            if (startY + shift < heightVariation + weightVariation + rainfallVariation)
            {
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.TOP_GRASS_TO_SAND, SurfaceStates.MID_DIRT_TO_SAND, SurfaceStates.UNDER_GRAVEL);
            }
            else
            {
                buildSandStoneSurface(context, startY, endY, karst, volcanic);
            }
        }
        else
        {
            if (startY - 5 > heightVariation - weightVariation - rainfallVariation)
            {
                NormalSurfaceBuilder.INSTANCE.buildSurface(context, startY, endY, SurfaceStates.TOP_GRASS_TO_SAND, SurfaceStates.MID_DIRT_TO_SAND, SurfaceStates.UNDER_GRAVEL);
            }
            else
            {
                buildSandySurface(context, startY, endY, karst, volcanic);
            }
        }
    }

    private void buildSandySurface(SurfaceBuilderContext context, int startHeight, int minSurfaceHeight, boolean karst, boolean volcanic)
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
                        context.setBlockState(y, SurfaceStates.SAND);
                    }
                    else
                    {
                        context.setBlockState(y, sampleLayer(getKarstOrVolcanicSandLayer(sandLayers0, karst, volcanic), getKarstOrVolcanicSandLayer(sandLayers1, karst, volcanic), y + height, style));
                        surfaceDepth = 3;
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(y, sampleLayer(getKarstOrVolcanicSandstoneLayer(sandstoneLayers0, karst, volcanic), getKarstOrVolcanicSandstoneLayer(sandstoneLayers1, karst, volcanic), y + height, style));
                }
            }
        }
    }

    private void buildSandStoneSurface(SurfaceBuilderContext context, int startHeight, int minSurfaceHeight, boolean karst, boolean volcanic)
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
                        context.setBlockState(y, SurfaceStates.SAND);
                    }
                    else
                    {
                        if (context.getSlope() > maxSoilSlope)
                        {
                            context.setBlockState(y, sampleLayer(getKarstOrVolcanicSandstoneLayer(sandstoneLayers0, karst, volcanic), getKarstOrVolcanicSandstoneLayer(sandstoneLayers1, karst, volcanic), y + height, style));
                        }
                        else
                        {
                            context.setBlockState(y, SurfaceStates.TOP_GRASS_TO_SAND);
                        }
                        surfaceDepth = 15;
                    }
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    context.setBlockState(y, sampleLayer(getKarstOrVolcanicSandstoneLayer(sandstoneLayers0, karst, volcanic), getKarstOrVolcanicSandstoneLayer(sandstoneLayers1, karst, volcanic), y + height, style));
                }
            }
        }
    }

    private BlockState sampleLayer(BlockState[] layers0, BlockState[] layers1, int y, float threshold)
    {
        // Make layers thicker when strata are dipping so they appear continuous
        final int height = dippingStrata ? y / 4 : y;
        final int index = Math.floorMod(height, LAYER_SIZE);
        return (layerThresholds[index] < threshold ? layers0 : layers1)[index];
    }
}