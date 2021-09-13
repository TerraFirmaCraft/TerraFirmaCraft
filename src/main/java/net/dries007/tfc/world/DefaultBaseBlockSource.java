package net.dries007.tfc.world;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.BaseStoneSource;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.chunkdata.RockData;

public class DefaultBaseBlockSource implements BaseBlockSource
{
    private final int chunkX, chunkZ;
    private final RockData rockData;
    private final Sampler<Biome> biomeSampler;
    private final LevelAccessor level;

    private final BlockState freshWater = Blocks.WATER.defaultBlockState(), saltWater = TFCBlocks.SALT_WATER.get().defaultBlockState();

    public DefaultBaseBlockSource(LevelAccessor level, ChunkPos pos, RockData rockData, Sampler<Biome> biomeSampler)
    {
        this.level = level;
        this.chunkX = pos.getMinBlockX();
        this.chunkZ = pos.getMinBlockZ();
        this.rockData = rockData;
        this.biomeSampler = biomeSampler;
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
            return TFCBiomes.getExtensionOrThrow(level, biomeSampler.get(x, z)).getVariants().isSalty() ? saltWater : freshWater;
        }
        return fluidOrAir;
    }
}
