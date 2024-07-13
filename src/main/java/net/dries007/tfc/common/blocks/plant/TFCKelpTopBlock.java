/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.util.Helpers;

public abstract class TFCKelpTopBlock extends TopPlantBlock implements IFluidLoggable
{
    public static TFCKelpTopBlock create(Properties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpTopBlock(ExtendedProperties.of(properties), bodyBlock, direction, shape)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    private final Supplier<? extends Block> bodyBlock;

    protected TFCKelpTopBlock(ExtendedProperties properties, Supplier<? extends Block> bodyBlock, Direction direction, VoxelShape shape)
    {
        super(properties, bodyBlock, direction, shape);
        this.bodyBlock = bodyBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockState state = super.getStateForPlacement(context);
        if (state != null)
        {
            FluidState fluidState = level.getFluidState(context.getClickedPos());
            if (!fluidState.isEmpty() && getFluidProperty().canContain(fluidState.getType()))
            {
                return state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidState.getType()));
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        if (state.getFluidState().isEmpty())
        {
            return false; // Requires water to survive.
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    protected boolean canAttachTo(BlockState state)
    {
        return state.getBlock() != Blocks.MAGMA_BLOCK;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        VoxelShape voxelshape = super.getShape(state, level, pos, context);
        Vec3 vector3d = state.getOffset(level, pos);
        return voxelshape.move(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == growthDirection.getOpposite() && !state.canSurvive(level, currentPos))
        {
            level.scheduleTick(currentPos, this, 1);
        }
        if (facing != growthDirection || !Helpers.isBlock(facingState, this) && !Helpers.isBlock(facingState, getBodyBlock()))
        {
            FluidHelpers.tickFluid(level, currentPos, state);
            return state;
        }
        else// this is where it converts the top block to a body block when it gets placed on top of another top block
        {
            return getBodyBlock().defaultBlockState().setValue(getFluidProperty(), state.getValue(getFluidProperty()));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(getFluidProperty());
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected boolean canGrowInto(BlockState state)
    {
        Fluid fluid = state.getFluidState().getType();
        return getFluidProperty().canContain(fluid) && fluid != Fluids.EMPTY;
    }

    @Override
    protected GrowingPlantBodyBlock getBodyBlock()
    {
        return (GrowingPlantBodyBlock) bodyBlock.get();
    }
}
