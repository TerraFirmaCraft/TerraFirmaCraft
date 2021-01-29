/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.util.Lazy;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class BadlandsSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig>
{
    private BlockState[] sandLayers;
    private INoise2D heightVariationNoise;

    public BadlandsSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        float heightVariation = heightVariationNoise.noise(x, z);
        if (startHeight > heightVariation)
        {
            TFCSurfaceBuilders.NORMAL.get().apply(random, chunkIn, biomeIn, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, config);
        }
        else
        {
            buildSandySurface(random, chunkIn, x, z, startHeight, noise, defaultBlock, seaLevel, seed, config);
        }
    }

    @Override
    protected void initSeed(long seed)
    {
        sandLayers = new BlockState[32];

        // Alternating red + brown sand layers
        Random random = new Random(seed);
        BlockState redSand = TFCBlocks.SAND.get(SandBlockType.RED).get().defaultBlockState();
        BlockState brownSand = TFCBlocks.SAND.get(SandBlockType.BROWN).get().defaultBlockState();
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

    @SuppressWarnings("deprecation")
    private void buildSandySurface(Random random, IChunk chunkIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        // Lazy because this queries a noise layer
        Lazy<SurfaceBuilderConfig> underWaterConfig = Lazy.of(() -> TFCSurfaceBuilders.UNDERWATER.get().getUnderwaterConfig(x, z, seed));

        BlockState underState = config.getUnderMaterial();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int surfaceDepth = -1;
        int maxSurfaceDepth = (int) (noise / 3.0D + random.nextDouble() * 0.25D);
        if (maxSurfaceDepth < 0)
        {
            maxSurfaceDepth = 0;
        }
        int localX = x & 15;
        int localZ = z & 15;

        for (int y = startHeight; y >= 0; --y)
        {
            pos.set(localX, y, localZ);
            BlockState stateAt = chunkIn.getBlockState(pos);
            if (stateAt.isAir())
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
            }
            else if (stateAt.getBlock() == defaultBlock.getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceDepth = maxSurfaceDepth;
                    if (y < seaLevel - 1)
                    {
                        underState = underWaterConfig.get().getUnderwaterMaterial();
                    }
                    else
                    {
                        underState = sandLayers[y % sandLayers.length];
                    }

                    chunkIn.setBlockState(pos, underState, false);
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    chunkIn.setBlockState(pos, underState, false);
                }
            }
        }
    }
}