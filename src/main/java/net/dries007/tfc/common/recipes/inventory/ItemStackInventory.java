/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * A mutable {@link RecipeInput} for a single stack
 */
public class ItemStackInventory implements RecipeInput
{
    protected ItemStack stack;

    public ItemStackInventory()
    {
        this(ItemStack.EMPTY);
    }

    public ItemStackInventory(ItemStack stack)
    {
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public ItemStack getItem(int index)
    {
        return stack;
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return stack.isEmpty();
    }
}