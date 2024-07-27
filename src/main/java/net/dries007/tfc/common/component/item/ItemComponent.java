/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

import net.minecraft.world.item.ItemStack;


public final class ItemComponent
{
    /**
     * Attempt to insert a stack into this container at the given slot.
     * @param stack The stack to insert. Not modified
     * @param info Info representing the container
     * @return A pair of the resulting component, and the remainder after inserting. This may be the original stack, unmodified.
     */
    public static InsertInfo insert(ItemStack contentInSlot, ItemStack stack, ItemContainerInfo info)
    {
        // If the stack is empty, or we cannot contain the item, then return unmodified with a full remainder
        if (stack.isEmpty() || !info.canContainItem(stack))
        {
            return new InsertInfo(contentInSlot, stack);
        }

        // The slot capacity is restricted to the minimum of the slot capacity, and stack capacity. If both the content in the slot is not empty,
        // and the content and insert stack are not the same, we cannot insert
        if (!contentInSlot.isEmpty() && !ItemStack.isSameItemSameComponents(contentInSlot, stack))
        {
            return new InsertInfo(contentInSlot, stack);
        }

        // If not, we can contain this stack. Calculate the total amount that we can insert - for items, the capacity is the minimum
        // of the stack max size, and the slot capacity. Note that the content in slot may be empty, but the stack to insert is not
        final int slotCapacity = Math.min(stack.getMaxStackSize(), info.slotCapacity());
        final int slotTotal = contentInSlot.getCount() + stack.getCount();
        final ItemStack content = stack.copyWithCount(Math.min(slotCapacity, slotTotal));
        final ItemStack remainder = slotTotal > slotCapacity ? stack.copyWithCount(slotTotal - slotCapacity) : ItemStack.EMPTY;

        // N.B. We can live with reference copies of the stacks in the slots, because we make them immutable
        return new InsertInfo(content, remainder);
    }

    /**
     * Attempt to extract a stack from the container in the given slot.
     * @param amount A maximum amount to extract.
     * @return A pair of the resulting component, and the extracted stack
     */
    public static ExtractInfo extract(ItemStack content, int amount)
    {
        // If we are currently empty, we cannot extract any
        if (content.isEmpty())
        {
            return new ExtractInfo(content, ItemStack.EMPTY);
        }

        // Otherwise, compute the remainder and the extracted stack
        final ItemStack remainder = content.getCount() > amount ? content.copyWithCount(content.getCount() - amount) : ItemStack.EMPTY;
        final ItemStack extracted = content.getCount() >= amount ? content.copyWithCount(amount) : content;
        return new ExtractInfo(remainder, extracted);
    }

    public record InsertInfo(ItemStack content, ItemStack remainder) {}
    public record ExtractInfo(ItemStack content, ItemStack extract) {}
}
