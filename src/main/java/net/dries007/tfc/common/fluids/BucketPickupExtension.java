/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

/**
 * An extension for {@link BucketPickup} for blocks with more complex pickup behavior, and allows simulation.
 */
public interface BucketPickupExtension extends BucketPickup
{
    FluidStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state, IFluidHandler.FluidAction action);
}
