/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInventoryNoop extends IInventory
{
    @Override
    default int getSizeInventory()
    {
        return 0;
    }

    @Override
    default boolean isEmpty()
    {
        return true;
    }

    @Override
    default ItemStack getStackInSlot(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack decrStackSize(int index, int count)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeStackFromSlot(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default void setInventorySlotContents(int index, ItemStack stack) {}

    @Override
    default void markDirty() {}

    @Override
    default boolean isUsableByPlayer(PlayerEntity player)
    {
        return true;
    }

    @Override
    default void clear() {}
}