/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * No-op implementation of {@link Container}, as the generic for recipes requires this implementation but using it is never required.
 * Methods are deprecated because we really shouldn't be calling them in lieu of specific methods on the subclasses
 */
public interface EmptyInventory extends Container
{
    @Override
    @Deprecated
    default int getContainerSize()
    {
        return 0;
    }

    @Override
    @Deprecated
    default boolean isEmpty()
    {
        return true;
    }

    @Override
    @Deprecated
    default ItemStack getItem(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    @Deprecated
    default ItemStack removeItem(int index, int count)
    {
        return ItemStack.EMPTY;
    }

    @Override
    @Deprecated
    default ItemStack removeItemNoUpdate(int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    @Deprecated
    default void setItem(int index, ItemStack stack) {}

    @Override
    @Deprecated
    default void setChanged() {}

    @Override // Not deprecated, because it's implemented by containers
    default boolean stillValid(Player player)
    {
        return true;
    }

    @Override
    @Deprecated
    default void clearContent() {}
}