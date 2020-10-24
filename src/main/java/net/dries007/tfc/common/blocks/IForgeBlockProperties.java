package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlock;

/**
 * This implements some of the more annoying methods in {@link IForgeBlock} which would otherwise require implementing across all manner of vanilla subclasses.
 * Since forge has made the decision that blocks should have behavioral control rather than add entries to {@link net.minecraft.block.AbstractBlock.Properties}, we mimic the same structure here.
 */
public interface IForgeBlockProperties extends IForgeBlock
{
    ForgeBlockProperties getForgeProperties();

    @Override
    default boolean hasTileEntity(BlockState state)
    {
        return getForgeProperties().hasTileEntity();
    }

    @Nullable
    @Override
    default TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return getForgeProperties().createTileEntity();
    }

    @Override
    default int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        return getForgeProperties().getFlammability();
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        return getForgeProperties().getFireSpreadSpeed();
    }
}
