package net.dries007.tfc.common.blocks.rock;

import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Marker interface for blocks with special collapsing behavior
 */
public interface IFallableBlock
{
    /**
     * Called after the block is placed once finished falling.
     * The block will be placed at the given location.
     */
    void onceFinishedFalling(World worldIn, BlockPos pos, FallingBlockEntity fallingBlock);
}
