/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import net.minecraft.util.ITickable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * A merge of {@link TEInventory} and {@link TETickableBase}
 */
public class TETickableInventory extends TEInventory implements ITickable
{
    protected boolean needsClientUpdate;

    protected TETickableInventory(int inventorySize)
    {
        super(inventorySize);
    }

    protected TETickableInventory(ItemStackHandler inventory)
    {
        super(inventory);
    }

    @Override
    public void update()
    {
        if (!world.isRemote && needsClientUpdate)
        {
            // Batch sync requests into single packets rather than sending them every time markForSync is called
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
