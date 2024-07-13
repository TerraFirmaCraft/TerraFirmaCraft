/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

/**
 * {@link net.minecraft.world.level.block.SeaPickleBlock}
 */
public class TFCSeaPickleBlock extends Block implements IFluidLoggable
{
    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
    public static final FluidProperty FLUID = TFCBlockStateProperties.SALT_WATER;

    protected static final VoxelShape ONE_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    protected static final VoxelShape TWO_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    protected static final VoxelShape THREE_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    protected static final VoxelShape FOUR_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    public static boolean isDead(BlockState state)
    {
        FluidProperty property = ((TFCSeaPickleBlock) state.getBlock()).getFluidProperty();
        return state.getValue(property) == property.keyFor(Fluids.EMPTY);
    }

    public TFCSeaPickleBlock(BlockBehaviour.Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(PICKLES, 1));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (Helpers.isBlock(blockstate, this))
        {
            return blockstate.setValue(PICKLES, Math.min(4, blockstate.getValue(PICKLES) + 1));
        }
        else
        {
            FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
            boolean flag = fluidstate.getType() == TFCFluids.SALT_WATER.getSource();
            return defaultBlockState().setValue(getFluidProperty(), flag ? getFluidProperty().keyFor(TFCFluids.SALT_WATER.getSource()) : getFluidProperty().keyFor(Fluids.EMPTY));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PICKLES, getFluidProperty());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext)
    {
        return useContext.getItemInHand().getItem() == this.asItem() && state.getValue(PICKLES) < 4 || super.canBeReplaced(state, useContext);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos blockpos = pos.below();
        return mayPlaceOn(level.getBlockState(blockpos), level, blockpos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(PICKLES))
            {
                case 2 -> TWO_AABB;
                case 3 -> THREE_AABB;
                case 4 -> FOUR_AABB;
                default -> ONE_AABB;
            };
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return !state.getCollisionShape(level, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(level, pos, Direction.UP);
    }
}
