/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class BadlandsSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderBaseConfiguration>
{
    private BlockState[] sandLayers;
    private Noise2D heightVariationNoise;

    public BadlandsSurfaceBuilder(Codec<SurfaceBuilderBaseConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public void apply(SurfaceBuilderContext context, Biome biome, int x, int z, int startHeight, double noise, double slope, float temperature, float rainfall, boolean saltWater, SurfaceBuilderBaseConfiguration config)
    {
        float heightVariation = heightVariationNoise.noise(x, z);
        if (startHeight > heightVariation)
        {
            TFCSurfaceBuilders.NORMAL.get().apply(context, biome, x, z, startHeight, noise, slope, temperature, rainfall, saltWater, config);
        }
        else
        {
            buildSandySurface(context, x, z, startHeight, temperature, rainfall, saltWater);
        }
    }

    @Override
    protected void initSeed(long seed)
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

        heightVariationNoise = new OpenSimplex2D(seed).octaves(2).scaled(110, 114).spread(0.5f);
    }

    private void buildSandySurface(SurfaceBuilderContext context, int x, int z, int startHeight, float rainfall, float temperature, boolean saltWater)
    {
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int surfaceDepth = -1;
        int localX = x & 15;
        int localZ = z & 15;

        for (int y = startHeight; y >= 0; --y)
        {
            pos.set(localX, y, localZ);
            BlockState stateAt = context.getBlockState(pos);
            if (stateAt.isAir())
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
            }
            else if (stateAt.getBlock() == context.getDefaultBlock().getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceDepth = 0;
                    if (y < context.getSeaLevel() - 1)
                    {
                        context.setBlockState(pos, SurfaceStates.TOP_UNDERWATER, rainfall, temperature, saltWater);
                    }
                    else
                    {
                        context.setBlockState(pos, sandLayers[y % sandLayers.length]);
                    }
                }
                else
                {
                    // Underground layers
                    context.setBlockState(pos, SurfaceStates.RAW, rainfall, temperature, saltWater);
                }
            }
            else // Default fluid
            {
                context.setBlockState(pos, SurfaceStates.WATER, rainfall, temperature, saltWater);
            }
        }
    }
}