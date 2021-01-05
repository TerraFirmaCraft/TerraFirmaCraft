package net.dries007.tfc.common.tileentity;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.ItemStackHandler;

public abstract class TickableInventoryTileEntity extends InventoryTileEntity implements ITickableTileEntity
{
    protected boolean needsClientUpdate;

    public TickableInventoryTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);
    }

    public TickableInventoryTileEntity(TileEntityType<?> type, ItemStackHandler inventory, ITextComponent defaultName)
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
