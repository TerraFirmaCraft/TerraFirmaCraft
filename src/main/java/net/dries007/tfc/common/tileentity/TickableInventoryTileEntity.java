/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class TickableInventoryTileEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundNBT>> extends InventoryTileEntity<C> implements ITickableTileEntity
{
    protected boolean needsClientUpdate;

    public TickableInventoryTileEntity(TileEntityType<?> type, InventoryFactory<C> inventory, ITextComponent defaultName)
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
