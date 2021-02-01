package net.dries007.tfc.api.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.util.IFallingBlock;

public interface ICollapsableBlock extends IFallingBlock
{

    /**
     * Override to determine what IBlockState this current Block will turn into when collapsing.
     *
     * @param world current World, this will only be called on the server side (expect ServerWorld!)
     * @param pos current BlockPos of where this Block is falling from
     * @return The collapsing IBlockState of this Block
     */
    IBlockState getCollapsingState(World world, BlockPos pos);

    default boolean canCollapse(World world, BlockPos pos)
    {
        return canCollapseAt(world, pos.down());
    }

    default boolean canCollapseAt(World world, BlockPos downPos)
    {
        return world.getBlockState(downPos).getMaterial().isReplaceable();
    }

    /**
     * Collapse a given area
     *
     * @param world       the worldObj
     * @param centerPoint the center of this area
     */
    default void collapseArea(World world, BlockPos centerPoint, int radius)
    {

    }

}
