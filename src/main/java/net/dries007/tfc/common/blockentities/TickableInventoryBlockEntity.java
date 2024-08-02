/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * An extension of {@link InventoryBlockEntity} for block entities that are ticking. This batches sync updates so that they
 * only occur at most once per tick.
 */
public abstract class TickableInventoryBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends InventoryBlockEntity<C>
{
    private boolean needsClientUpdate;

    protected TickableInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventory)
    {
        super(type, pos, state, inventory);
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
