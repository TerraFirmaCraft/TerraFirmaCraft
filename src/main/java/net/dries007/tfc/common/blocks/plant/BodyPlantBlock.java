/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BodyPlantBlock extends GrowingPlantBodyBlock
{
    private final Supplier<? extends Block> headBlock;

    public BodyPlantBlock(BlockBehaviour.Properties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, direction, shape, false);
        this.headBlock = headBlock;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel worldIn, Random rand, BlockPos pos, BlockState state)
    {

    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.relative(growthDirection.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canAttachTo(blockstate))
        {
            return false;
        }
        else
        {
            return block == getHeadBlock() || block == getBodyBlock() || blockstate.is(BlockTags.LEAVES) || blockstate.isFaceSturdy(worldIn, blockpos, growthDirection);
        }
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock()
    {
        return (GrowingPlantHeadBlock) headBlock.get();
    }
}
