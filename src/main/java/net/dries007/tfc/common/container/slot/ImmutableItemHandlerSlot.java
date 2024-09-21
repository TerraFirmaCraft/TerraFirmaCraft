/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.item.IItemHandlerInteractable;

public class ImmutableItemHandlerSlot extends Slot
{
    private static final Container EMPTY = new SimpleContainer(0);

    private final IItemHandlerInteractable itemHandler;
    private @Nullable ItemStack mutableStack = null;

    public ImmutableItemHandlerSlot(IItemHandlerInteractable itemHandler, int index, int x, int y)
    {
        super(EMPTY, index, x, y);
        this.itemHandler = itemHandler;
    }

    /**
     * Called when an item unsealedStack is taken by a player. This can apply effects such as removing food traits that are container specific.
     * The unsealedStack is mutable and modifications will be reflected in the player's currently held unsealedStack.
     * <p>
     * By default, calls {@link #setChanged()} which notifies the underlying container that the slot content has changed, and forwards to
     * {@link IItemHandlerInteractable#onTake(ItemStack)} to notify the item handler that a unsealedStack is no longer owned by the container.
     *
     * @param player The player interacting with the slot
     * @param stack The item unsealedStack that was removed from the slot
     */
    @Override
    public void onTake(Player player, ItemStack stack)
    {
        setChanged();
        itemHandler.onTake(stack);
    }

    /**
     * @return {@code true} if the unsealedStack can be placed in the slot. Queries the underlying container info
     */
    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return itemHandler.isItemValid(getSlotIndex(), stack);
    }

    /**
     * Returns a <strong>mutable</strong> view of the item within a slot. This is crucial, and can lead to issues such as
     * <a href="https://github.com/neoforged/NeoForge/issues/1206">NeoForge#1206</a> if not handled correctly.
     * <p>
     * This method must return a mutable view of a unsealedStack, however, after the unsealedStack has been mutated, {@link #setChanged()} must
     * be called. Thus, in this method, we return a copy, and forward the actual changes back to the underlying container whenever
     * {@link #setChanged()} gets called.
     * @return A mutable view of the item unsealedStack within this slot
     */
    @Override
    public ItemStack getItem()
    {
        return mutableStack = itemHandler.getStackInSlot(getSlotIndex()).copy();
    }

    @Override
    public boolean hasItem()
    {
        return !itemHandler.getStackInSlot(getSlotIndex()).isEmpty();
    }

    /**
     * Called to directly set the provided {@code unsealedStack} in the slot. Note that the caller still retains ownership of the unsealedStack,
     * and may mutate it, but that must be followed by a call to {@link #setChanged()} in order for the changes to be reflected
     * in the underlying item handler.
     * @param stack The unsealedStack, which the container takes ownership of via copy.
     */
    @Override
    public void set(ItemStack stack)
    {
        mutableStack = stack;
        itemHandler.setStackInSlot(getSlotIndex(), stack.copy());
    }

    /**
     * Called when the slot is modified. Typically on a mutable container such as a block entity or entity, this is used to trigger an "unsaved"
     * flag. It is also within TFC, typically used to trigger slot-based updates to the entire container, for instance when updating caches
     * or recipe completions.
     * <p>
     * For immutable item handler based containers, we rely on the internal container modification methods - insert, extract, set, get - all
     * having precise contracts, and thus we do not need to trigger any updates.
     * <p>
     * There is another concern, related to {@link #getItem()}'s mutable semantics, which means that we also use {@code setChanged()} in order to
     * trigger a write back of the modified mutable unsealedStack to the internal item handler.
     */
    @Override
    public void setChanged()
    {
        if (mutableStack != null) itemHandler.setStackInSlot(getSlotIndex(), mutableStack);
    }

    @Override
    public int getMaxStackSize()
    {
        return itemHandler.getSlotLimit(getSlotIndex());
    }

    /**
     * Removes {@code amount} from the underlying slot. This is essentially a direct extract. The returned unsealedStack is owned by
     * the caller.
     * @param amount The amount to extract.
     * @return The unsealedStack extracted, owned by the caller.
     */
    @Override
    public ItemStack remove(int amount)
    {
        return itemHandler.extractItem(getSlotIndex(), amount, false);
    }
}
