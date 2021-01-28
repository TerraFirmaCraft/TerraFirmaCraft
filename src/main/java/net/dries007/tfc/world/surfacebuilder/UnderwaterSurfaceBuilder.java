/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.OpenSimplex2D;

public class UnderwaterSurfaceBuilder extends SeededSurfaceBuilder<SurfaceBuilderConfig>
{
    private INoise2D variantNoise;

    public UnderwaterSurfaceBuilder(Codec<SurfaceBuilderConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        // Use underwater config for all locations
        // Otherwise this is identical to normal surface builder
        config = getUnderwaterConfig(x, z, seed);

        BlockState topState;
        BlockState underState = config.getUnderMaterial();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int surfaceDepth = -1;
        int maxSurfaceDepth = (int) (noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
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
                    if (maxSurfaceDepth <= 0)
                    {
                        topState = Blocks.AIR.defaultBlockState();
                        underState = defaultBlock;
                    }
                    else if (y < seaLevel - 1)
                    {
                        topState = underState = config.getUnderwaterMaterial();
                    }
                    else
                    {
                        topState = config.getTopMaterial();
                        underState = config.getUnderMaterial();
                    }

                    chunkIn.setBlockState(pos, topState, false);
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

    public SurfaceBuilderConfig getUnderwaterConfig(int x, int z, long seed)
    {
        initNoise(seed);
        float variantValue = variantNoise.noise(x, z);
        return variantValue > 0 ? SurfaceBuilder.CONFIG_FULL_SAND : SurfaceBuilder.CONFIG_GRAVEL;
    }

    @Override
    public void initSeed(long seed)
    {
        variantNoise = new OpenSimplex2D(seed).octaves(2).spread(0.015f);
    }
}