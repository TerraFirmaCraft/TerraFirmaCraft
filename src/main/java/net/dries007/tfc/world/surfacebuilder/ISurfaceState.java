/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Represents a wrapper around a context-sensitive block
 * For example, 'grass', which may be at time of placement, any variant of grass or sand depending on climate
 */
@FunctionalInterface
public interface ISurfaceState
{
    BlockState state(RockData rockData, int x, int y, int z, float temperature, float rainfall, boolean salty);

    default void place(SurfaceBuilderContext context, BlockPos pos, int x, int z, RockData rockData, float temperature, float rainfall, boolean salty)
    {
        context.setBlockState(pos, state(rockData, x, pos.getY(), z, temperature, rainfall, salty));
    }
}
