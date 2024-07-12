/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public abstract class TickableInventoryBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends InventoryBlockEntity<C>
{
    protected boolean needsClientUpdate;
    protected boolean isDirty;

    protected TickableInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventory, Component defaultName)
    {
        super(type, pos, state, inventory, defaultName);
    }

    public void checkForLastTickSync()
    {
        if (needsClientUpdate)
        {
            // only sync further down when we actually request it to be synced
            needsClientUpdate = false;
            super.markForSync();
        }
        if (isDirty)
        {
            isDirty = false;
            super.markDirty();
        }
    }

    @Override
    public void markForSync()
    {
        needsClientUpdate = true;
    }

    @Override
    public void markDirty()
    {
        isDirty = true;
    }
}
