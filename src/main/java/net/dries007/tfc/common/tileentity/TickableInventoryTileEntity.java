/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class TickableInventoryTileEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends InventoryTileEntity<C> implements TickableBlockEntity
{
    protected boolean needsClientUpdate;

    public TickableInventoryTileEntity(BlockEntityType<?> type, InventoryFactory<C> inventory, Component defaultName)
    {
        super(type, inventory, defaultName);
    }

    @Override
    public void tick()
    {
        if (level != null && !level.isClientSide() && needsClientUpdate)
        {
            // only sync further down when we actually request it to be synced
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
