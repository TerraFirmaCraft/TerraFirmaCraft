package net.dries007.tfc.objects.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import net.dries007.tfc.objects.te.TEQuern;

public class SlotQuernInput extends SlotItemHandler
{
    TEQuern tile;

    public SlotQuernInput(@Nonnull IItemHandler inventory, int idx, int x, int y, TEQuern tile)
    {
        super(inventory, idx, x, y);
        this.tile = tile;
    }

    @Override
    public void onSlotChanged()
    {
        tile.onInputSlotChange();
    }
}
