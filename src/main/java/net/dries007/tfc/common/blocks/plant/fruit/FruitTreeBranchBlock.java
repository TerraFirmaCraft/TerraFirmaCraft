/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class FruitTreeBranchBlock extends PipeBlock implements IForgeBlockExtension
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_3;
    private final ForgeBlockProperties properties;

    public FruitTreeBranchBlock(ForgeBlockProperties properties)
    {
        super(0.25F, properties.properties());
        this.properties = properties;
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, STAGE);
    }

    public BlockState getStateForPlacement(BlockGetter world, BlockPos pos)
    {
        Block downBlock = world.getBlockState(pos.below()).getBlock();
        Block upBlock = world.getBlockState(pos.above()).getBlock();
        Block northBlock = world.getBlockState(pos.north()).getBlock();
        Block eastBlock = world.getBlockState(pos.east()).getBlock();
        Block southBlock = world.getBlockState(pos.south()).getBlock();
        Block westBlock = world.getBlockState(pos.west()).getBlock();
        return defaultBlockState()
            .setValue(DOWN, TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(downBlock) || TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(downBlock))
            .setValue(UP, TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(upBlock) || TFCTags.Blocks.FRUIT_TREE_SAPLING.contains(upBlock))
            .setValue(NORTH, TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(northBlock))
            .setValue(EAST, TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(eastBlock))
            .setValue(SOUTH, TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(southBlock))
            .setValue(WEST, TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(westBlock));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canSurvive(worldIn, currentPos))
        {
            worldIn.getBlockTicks().scheduleTick(currentPos, this, 1);
            return stateIn;
        }
        else
        {
            boolean flag = facingState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH) || (facing == Direction.DOWN && facingState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || (facing == Direction.UP && facingState.is(TFCTags.Blocks.FRUIT_TREE_SAPLING)));
            return stateIn.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = pos.relative(direction);
            if (TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(worldIn.getBlockState(relativePos).getBlock()))
            {
                Block below = worldIn.getBlockState(relativePos.below()).getBlock();
                if (TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(below) || TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(below))
                {
                    return true;
                }
            }
        }
        Block blockIn = belowState.getBlock();
        return TFCTags.Blocks.FRUIT_TREE_BRANCH.contains(blockIn) || TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(blockIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand)
    {
        if (!state.canSurvive(worldIn, pos) && !worldIn.isClientSide())
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
