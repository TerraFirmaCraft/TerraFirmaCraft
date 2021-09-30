/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.RockData;

public class DefaultBaseBlockSource implements BaseBlockSource
{
    private static int index(int x, int z)
    {
        return (x & 15) | ((z & 15) << 4);
    }

    private final int chunkX, chunkZ;
    private final RockData rockData;
    private final Sampler<Biome> biomeSampler;
    private final BlockState[] cachedFluidStates;
    private final LevelAccessor level;

    private final BlockState freshWater = Blocks.WATER.defaultBlockState(), saltWater = TFCBlocks.SALT_WATER.get().defaultBlockState();

    public DefaultBaseBlockSource(LevelAccessor level, ChunkPos pos, RockData rockData, Sampler<Biome> biomeSampler)
    {
        this.level = level;
        this.chunkX = pos.getMinBlockX();
        this.chunkZ = pos.getMinBlockZ();
        this.rockData = rockData;
        this.biomeSampler = biomeSampler;
        this.cachedFluidStates = new BlockState[16 * 16];
    }

    @Override
    public BlockState getBaseBlock(int x, int y, int z)
    {
        return rockData.getRock(chunkX | (x & 15), y, chunkZ | (z & 15)).raw().defaultBlockState();
    }

    @Override
    public BlockState modifyFluid(BlockState fluidOrAir, int x, int z)
    {
        if (fluidOrAir == freshWater)
        {
            final int index = index(x, z);
            BlockState state = cachedFluidStates[index];
            if (state == null)
            {
                state = TFCBiomes.getExtensionOrThrow(level, biomeSampler.get(x, z)).getVariants().isSalty() ? saltWater : freshWater;
                cachedFluidStates[index] = state;
            }
            return state;
        }
        return fluidOrAir;
    }
}
