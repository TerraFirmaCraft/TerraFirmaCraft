/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public abstract class TFCKelpBlock extends BodyPlantBlock implements IFluidLoggable
{
    public static TFCKelpBlock create(Properties properties, Supplier<? extends Block> headBlock, Direction direction, VoxelShape shape, FluidProperty fluid)
    {
        return new TFCKelpBlock(ExtendedProperties.of(properties.lootFrom(headBlock)), headBlock, shape, direction)
        {
            @Override
            public FluidProperty getFluidProperty()
            {
                return fluid;
            }
        };
    }

    private final Supplier<? extends Block> headBlock;

    protected TFCKelpBlock(ExtendedProperties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, headBlock, shape, direction);
        this.headBlock = headBlock;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == this.growthDirection.getOpposite() && !state.canSurvive(level, currentPos))
        {
            level.scheduleTick(currentPos, this, 1);
        }
        //This is where vanilla assumes (wrongly) that the abstract block has correct waterlogged handling
        GrowingPlantHeadBlock topBlock = this.getHeadBlock();
        if (facing == this.growthDirection)
        {
            Block block = facingState.getBlock();
            if (block != this && block != topBlock)
            {
                return topBlock.getStateForPlacement(level).setValue(getFluidProperty(), state.getValue(getFluidProperty()));
            }
        }
        if (scheduleFluidTicks)
        {
            FluidHelpers.tickFluid(level, currentPos, state);
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState)
    {
        return false;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        VoxelShape voxelshape = super.getShape(state, level, pos, context);
        Vec3 vector3d = state.getOffset(level, pos);
        return voxelshape.move(vector3d.x, vector3d.y, vector3d.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(getFluidProperty());
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state)
    {
        return ItemStack.EMPTY; // Don't allow taking the fluid
    }

    protected GrowingPlantHeadBlock getHeadBlock()
    {
        return (GrowingPlantHeadBlock) headBlock.get();
    }
}
