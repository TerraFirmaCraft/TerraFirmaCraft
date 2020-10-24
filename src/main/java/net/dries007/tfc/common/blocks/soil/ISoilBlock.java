package net.dries007.tfc.common.blocks.soil;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

/**
 * Blocks that can be turned into their underlying soil, such as farmland, paths and grass.
 */
public interface ISoilBlock
{
    /**
     * Gets the dirt block this block will transform into given the current state
     */
    BlockState getDirt(IWorld world, BlockPos pos, BlockState state);
}
