/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public interface ItemContainer extends IItemHandlerInteractable
{
    /**
     * @return An immutable, unmodifiable view of the content item stacks.
     */
    List<ItemStack> contents();

    ItemContainerInfo containerInfo();

    @Override
    default int getSlots()
    {
        return contents().size();
    }

    @Override
    default ItemStack getStackInSlot(int slot)
    {
        return contents().get(slot);
    }

    @Override
    default int getSlotLimit(int slot)
    {
        return containerInfo().slotCapacity();
    }

    @Override
    default boolean isItemValid(int slot, ItemStack stack)
    {
        return containerInfo().canContainItem(stack);
    }
}
