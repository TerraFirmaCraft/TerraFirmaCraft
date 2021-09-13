package net.dries007.tfc.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BaseStoneSource;

public interface BaseBlockSource extends BaseStoneSource
{
    BlockState modifyFluid(BlockState fluidOrAir, int x, int z);
}
