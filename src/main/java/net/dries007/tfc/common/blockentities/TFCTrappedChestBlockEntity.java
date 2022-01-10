/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TFCTrappedChestBlockEntity extends TFCChestBlockEntity
{
    public TFCTrappedChestBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.TRAPPED_CHEST.get(), pos, state);
    }

    @Override
    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int opener, int openCount)
    {
        super.signalOpenCount(level, pos, state, opener, openCount);
        if (opener != openCount)
        {
            Block block = state.getBlock();
            level.updateNeighborsAt(pos, block);
            level.updateNeighborsAt(pos.below(), block);
        }
    }
}
