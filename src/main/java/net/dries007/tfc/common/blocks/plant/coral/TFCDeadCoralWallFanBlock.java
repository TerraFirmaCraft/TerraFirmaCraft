/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.Map;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * {  DeadCoralWallFanBlock}
 */
public class TFCDeadCoralWallFanBlock extends TFCCoralFanBlock
{
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(
        Direction.NORTH, Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D),
        Direction.SOUTH, Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D),
        Direction.WEST, Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D),
        Direction.EAST, Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));

    public TFCDeadCoralWallFanBlock(AbstractBlock.Properties builder)
    {
        super(builder);
        setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.get(state.get(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirrorIn)
    {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, FLUID);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (stateIn.get(getFluidProperty()).getFluid().isIn(FluidTags.WATER))
        {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickRate(worldIn));
        }

        return facing.getOpposite() == stateIn.get(FACING) && !stateIn.blockNeedsPostProcessing(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        Direction direction = state.get(FACING);
        BlockPos blockpos = pos.offset(direction.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        return blockstate.isSolidSide(worldIn, blockpos, direction);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = super.getStateForPlacement(context);
        IWorldReader iworldreader = context.getWorld();
        BlockPos blockpos = context.getPos();
        Direction[] directions = context.getNearestLookingDirections();

        for (Direction d : directions)
        {
            if (d.getAxis().isHorizontal())
            {
                blockstate = blockstate.with(FACING, d.getOpposite());
                if (blockstate.blockNeedsPostProcessing(iworldreader, blockpos))
                {
                    return blockstate;
                }
            }
        }
        return null;
    }
}
