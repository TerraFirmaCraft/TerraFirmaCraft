/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IInventoryNoop extends IInventory
{
    @Override
    default int getContainerSize()
    {
        return 0;
    }

    @Override
    default boolean isEmpty()
    {
        return true;
    }

    @Override
    default ItemStack getItem(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItem(int index, int count)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItemNoUpdate(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    default void setItem(int index, ItemStack stack) {}

    @Override
    default void setChanged() {}

    @Override
    default boolean stillValid(PlayerEntity player)
    {
        return true;
    }

    @Override
    default void clearContent() {}
}