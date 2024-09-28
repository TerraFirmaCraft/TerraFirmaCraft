/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

import java.util.List;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * An immutable wrapper around an {@link ItemStack} which respects proper equality and hash code functionality, as is
 * required for any data attached to components.
 * <p>
 * This class also includes utility methods for interacting with item stacks in components, including with reference
 * to item containers.
 */
public record ItemComponent(ItemStack stack)
{
    public static final Codec<ItemComponent> CODEC = ItemStack.CODEC.xmap(ItemComponent::new, ItemComponent::stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemComponent> STREAM_CODEC = ItemStack.STREAM_CODEC.map(ItemComponent::new, ItemComponent::stack);
    public static final ItemComponent EMPTY = new ItemComponent(ItemStack.EMPTY);

    public static boolean equals(ItemStack left, ItemStack right)
    {
        return ItemStack.matches(left, right);
    }

    @SuppressWarnings("deprecation")
    public static boolean equals(List<ItemStack> left, List<ItemStack> right)
    {
        return ItemStack.listMatches(left, right);
    }

    public static int hashCode(ItemStack content)
    {
        return ItemStack.hashItemAndComponents(content);
    }

    @SuppressWarnings("deprecation")
    public static int hashCode(List<ItemStack> content)
    {
        return ItemStack.hashStackList(content);
    }

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

    @Override
    public boolean equals(Object obj)
    {
        return obj == this || (obj instanceof ItemComponent that && equals(stack, that.stack));
    }

    @Override
    public int hashCode()
    {
        return hashCode(stack);
    }

    public record InsertInfo(ItemStack content, ItemStack remainder) {}
    public record ExtractInfo(ItemStack content, ItemStack extract) {}
}
