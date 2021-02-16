/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BodyPlantBlock extends AbstractBodyPlantBlock
{
    private final Supplier<? extends Block> headBlock;

    public BodyPlantBlock(AbstractBlock.Properties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, direction, shape, false);
        this.headBlock = headBlock;
    }
    @Override
    protected AbstractTopPlantBlock getTopPlantBlock()
    {
        return (AbstractTopPlantBlock) headBlock.get();
    }

    @Override
    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return false;
    }
    //TODO
    @Override
    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state)
    {

    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.offset(growthDirection.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canGrowOn(block))
        {
            return false;
        }
        else
        {
            return block == getTopPlantBlock() || block == getBodyPlantBlock() || blockstate.isIn(BlockTags.LEAVES) || blockstate.isSolidSide(worldIn, blockpos, growthDirection);
        }
    }
}
