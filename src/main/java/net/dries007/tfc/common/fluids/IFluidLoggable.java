/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A generic interface for a block which is able to contain any number of predetermined fluid properties
 *
 * @see FluidProperty
 * @see FluidHelpers
 */
public interface IFluidLoggable extends SimpleWaterloggedBlock, LiquidBlockContainer, BucketPickup
{
    @Override
    default boolean canPlaceLiquid(BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        final Fluid containedFluid = state.getValue(getFluidProperty()).getFluid();
        if (containedFluid == Fluids.EMPTY)
        {
            return getFluidProperty().getPossibleFluids().contains(fluidIn);
        }
        return false;
    }

    @Override
    default boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        final Fluid containedFluid = state.getValue(getFluidProperty()).getFluid();
        if (containedFluid == Fluids.EMPTY && getFluidProperty().getPossibleFluids().contains(fluidStateIn.getType()))
        {
            if (!worldIn.isClientSide())
            {
                worldIn.setBlock(pos, state.setValue(getFluidProperty(), getFluidProperty().keyFor(fluidStateIn.getType())), 3);
                worldIn.getLiquidTicks().scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
            }
            return true;
        }
        return false;
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor worldIn, BlockPos pos, BlockState state)
    {
        final Fluid containedFluid = state.getValue(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            worldIn.setBlock(pos, state.setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)), 3);
        }
        return ItemStack.EMPTY; // todo: what? return value changed containedFluid;
    }

    /**
     * Default implementation of {@link net.minecraft.world.level.block.state.BlockBehaviour#getFluidState(BlockState)} which allows arbitrary fluids based on the contained property.
     */
    default FluidState getFluidState(BlockState state)
    {
        final Fluid containedFluid = state.getValue(getFluidProperty()).getFluid();
        if (containedFluid instanceof FlowingFluid)
        {
            return ((FlowingFluid) containedFluid).getSource(false);
        }
        return containedFluid.defaultFluidState();
    }

    /**
     * Gets the correct fluid property for this block, which determines what fluids it can contain.
     */
    FluidProperty getFluidProperty();
}
