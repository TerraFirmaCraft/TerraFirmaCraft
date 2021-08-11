/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Block that can be converted into a mossy variant
 * Mossy blocks will call this on nearby blocks on random tick.
 * The destination block is responsible for checking if it can be converted to moss.
 */
public interface IMossGrowingBlock
{
    void convertToMossy(Level worldIn, BlockPos pos, BlockState state, boolean needsWater);
}
