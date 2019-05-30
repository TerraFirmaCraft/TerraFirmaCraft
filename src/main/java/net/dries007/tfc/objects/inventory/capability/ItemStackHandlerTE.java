/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.capability;

import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.objects.te.TEInventory;

public class ItemStackHandlerTE extends ItemStackHandler
{
    private final TEInventory tile;

    public ItemStackHandlerTE(TEInventory tile, int size)
    {
        super(size);
        this.tile = tile;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return tile.getSlotLimit(slot);
    }
}
