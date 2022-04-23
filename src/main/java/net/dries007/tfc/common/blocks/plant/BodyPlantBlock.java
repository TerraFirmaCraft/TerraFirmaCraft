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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
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

import net.dries007.tfc.util.Helpers;

public class BodyPlantBlock extends GrowingPlantBodyBlock
{
    private final Supplier<? extends Block> headBlock;

    public BodyPlantBlock(BlockBehaviour.Properties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, direction, shape, true);
        this.headBlock = headBlock;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, Random rand, BlockPos pos, BlockState state)
    {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, Random rand, BlockPos pos, BlockState state)
    {

    }

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos blockpos = pos.relative(growthDirection.getOpposite());
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canAttachTo(blockstate))
        {
            return false;
        }
        else
        {
            return block == getHeadBlock() || block == getBodyBlock() || Helpers.isBlock(blockstate, BlockTags.LEAVES) || blockstate.isFaceSturdy(level, blockpos, growthDirection);
        }
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock()
    {
        return (GrowingPlantHeadBlock) headBlock.get();
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return new ItemStack(getHeadBlock());
    }
}
