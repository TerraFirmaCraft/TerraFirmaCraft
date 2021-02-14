/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.block.*;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

/**
 * A generic interface for a block which is able to contain any number of predetermined fluid properties
 *
 * @see FluidProperty
 */
public interface IFluidLoggable extends IWaterLoggable, ILiquidContainer, IBucketPickupHandler
{
    default boolean canPlaceLiquid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid == Fluids.EMPTY)
        {
            return getFluidProperty().getPossibleFluids().contains(fluidIn);
        }
        return false;
    }

    default boolean placeLiquid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid == Fluids.EMPTY && getFluidProperty().getPossibleFluids().contains(fluidStateIn.getType()))
        {
            if (!worldIn.isClientSide())
            {
                worldIn.setBlockState(pos, state.with(getFluidProperty(), getFluidProperty().keyFor(fluidStateIn.getType())), 3);
                worldIn.getLiquidTicks().scheduleTick(pos, fluidStateIn.getType(), fluidStateIn.getType().getTickDelay(worldIn));
            }
            return true;
        }
        return false;
    }

    default Fluid takeLiquid(IWorld worldIn, BlockPos pos, BlockState state)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid != Fluids.EMPTY)
        {
            worldIn.setBlockState(pos, state.with(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)), 3);
        }
        return containedFluid;
    }

    /**
     * Default implementation of {  AbstractBlock#getFluidState(BlockState)} which allows arbitrary fluids based on the contained property.
     */
    @SuppressWarnings("deprecation")
    default FluidState getFluidState(BlockState state)
    {
        final Fluid containedFluid = state.get(getFluidProperty()).getFluid();
        if (containedFluid instanceof FlowingFluid)
        {
            return ((FlowingFluid) containedFluid).getSource(false);
        }
        return containedFluid.defaultFluidState();
    }

    /**
     * Modifies a state with a fluid.
     * Used to place automatic fluid logged blocks during world generation.
     *
     * @param state The original state
     * @param fluid The fluid to try and insert
     * @return The state with the fluid, if allowed, otherwise the input state.
     */
    default BlockState getStateWithFluid(BlockState state, Fluid fluid)
    {
        if (getFluidProperty().getPossibleFluids().contains(fluid))
        {
            return state.with(getFluidProperty(), getFluidProperty().keyFor(fluid));
        }
        return state;
    }

    FluidProperty getFluidProperty();
}
