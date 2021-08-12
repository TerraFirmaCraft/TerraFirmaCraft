package net.dries007.tfc.world;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BaseStoneSource;

import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.chunkdata.RockData;

public class RockLayerStoneSource implements BaseStoneSource
{
    private final int chunkX, chunkZ;
    private final RockData rockData;
    private final Set<BlockState> baseRockStates;

    public RockLayerStoneSource(ChunkPos chunkPos, RockData rockData)
    {
        this.chunkX = chunkPos.getMinBlockX();
        this.chunkZ = chunkPos.getMinBlockZ();
        this.rockData = rockData;
        this.baseRockStates = new HashSet<>();
        for (Rock rock : RockManager.INSTANCE.getValues())
        {
            baseRockStates.add(rock.getBlock(Rock.BlockType.RAW).defaultBlockState());
        }
    }

    public boolean isBaseStone(BlockState state)
    {
        return baseRockStates.contains(state);
    }

    @Override
    public BlockState getBaseBlock(int x, int y, int z)
    {
        return rockData.getRock(chunkX | x, y, chunkZ | z).getBlock(Rock.BlockType.RAW).defaultBlockState();
    }
}
