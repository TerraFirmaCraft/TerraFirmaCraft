/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


/**
 * Like {@link TickableInventoryBlockEntity} for blocks without an inventory. Batches sync updates to at most happen once per
 * tick, because the block entity should be ticking naturally anyway.
 */
public abstract class TickableBlockEntity extends TFCBlockEntity
{
    private boolean needsClientUpdate;

    protected TickableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void checkForLastTickSync()
    {
        if (needsClientUpdate)
        {
            needsClientUpdate = false;
            super.markForSync();
        }
    }

    @Override
    public void markForSync()
    {
        needsClientUpdate = true;
    }
}
