/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;

/**
 * {  net.minecraft.block.SeaPickleBlock}
 */
public class TFCSeaPickleBlock extends Block implements IFluidLoggable
{
    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;
    public static final FluidProperty FLUID = TFCBlockStateProperties.SALT_WATER;
    protected static final VoxelShape ONE_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    protected static final VoxelShape TWO_AABB = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    protected static final VoxelShape THREE_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    protected static final VoxelShape FOUR_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    public TFCSeaPickleBlock(AbstractBlock.Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().with(PICKLES, 1));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = context.getWorld().getBlockState(context.getPos());
        if (blockstate.isIn(this))
        {
            return blockstate.with(PICKLES, Math.min(4, blockstate.get(PICKLES) + 1));
        }
        else
        {
            FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
            boolean flag = fluidstate.getType() == TFCFluids.SALT_WATER.getSource();
            return defaultBlockState().with(getFluidProperty(), flag ? getFluidProperty().keyFor(TFCFluids.SALT_WATER.getSource()) : getFluidProperty().keyFor(Fluids.EMPTY));
        }
    }

    public static boolean isDead(BlockState state)
    {
        FluidProperty property = ((TFCSeaPickleBlock) state.getBlock()).getFluidProperty();
        return state.get(property) == property.keyFor(Fluids.EMPTY);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext)
    {
        return useContext.getItemInHand().getItem() == this.asItem() && state.get(PICKLES) < 4 || super.canBeReplaced(state, useContext);
    }

    protected boolean mayPlaceOn(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return !state.getCollisionShape(worldIn, pos).getFaceShape(Direction.UP).isEmpty() || state.isFaceSturdy(worldIn, pos, Direction.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        return mayPlaceOn(worldIn.getBlockState(blockpos), worldIn, blockpos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.get(PICKLES))
        {
            case 1:
            default:
                return ONE_AABB;
            case 2:
                return TWO_AABB;
            case 3:
                return THREE_AABB;
            case 4:
                return FOUR_AABB;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!stateIn.canBeReplacedByLeaves(worldIn, currentPos))
        {
            return Blocks.AIR.getDefaultState();
        }
        else
        {
            if (stateIn.get(getFluidProperty()).getFluid() != Fluids.EMPTY)
            {
                worldIn.getLiquidTicks().scheduleTick(currentPos, TFCFluids.SALT_WATER.getSource(), TFCFluids.SALT_WATER.getSource().getTickDelay(worldIn));
            }

            return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PICKLES, getFluidProperty());
    }
}
