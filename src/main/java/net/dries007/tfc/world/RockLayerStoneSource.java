package net.dries007.tfc.world;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BaseStoneSource;

import net.dries007.tfc.world.chunkdata.RockData;

public class RockLayerStoneSource implements BaseStoneSource
{
    private final int chunkX, chunkZ;
    private final RockData rockData;

    public RockLayerStoneSource(ChunkPos chunkPos, RockData rockData)
    {
        this.chunkX = chunkPos.getMinBlockX();
        this.chunkZ = chunkPos.getMinBlockZ();
        this.rockData = rockData;
    }

    @Override
    public BlockState getBaseBlock(int x, int y, int z)
    {
        return rockData.getRock(chunkX | (x & 15), y, chunkZ | (z & 15)).raw().defaultBlockState();
    }
}
