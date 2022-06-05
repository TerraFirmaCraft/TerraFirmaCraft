/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

/**
 * An extension for {@link net.minecraft.world.level.material.FlowingFluid}s that have custom source block creation.
 * This is used by {@link FluidHelpers#getNewFluidWithMixing(FlowingFluid, LevelReader, BlockPos, BlockState, boolean, int)} and thus also applies to all of {@link MixingFluid}.
 */
public interface FlowingFluidExtension
{
    static FluidState getSourceOrDefault(LevelReader level, BlockPos pos, FlowingFluid fluid, boolean falling)
    {
        if (fluid instanceof FlowingFluidExtension extension)
        {
            return extension.getSource(level, pos, falling);
        }
        return fluid.getSource(falling);
    }

    FluidState getSource(LevelReader level, BlockPos pos, boolean falling);
}
