/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.inventory;

import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.objects.te.TESidedInventory;

public class ItemStackHandlerTFC extends ItemStackHandler
{
    private final TESidedInventory tile;

    public ItemStackHandlerTFC(TESidedInventory tile, int size)
    {
        super(size);

        this.tile = tile;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return tile.getSlotLimit(slot);
    }

    @Override
    protected void onContentsChanged(int slot)
    {
        tile.setAndUpdateSlots(slot);
    }
}
