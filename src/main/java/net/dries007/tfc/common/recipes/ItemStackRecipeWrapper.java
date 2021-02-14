/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.ItemStack;

/**
 * A {  net.minecraftforge.items.wrapper.RecipeWrapper} for single item stacks.
 */
public class ItemStackRecipeWrapper implements IInventoryNoop
{
    protected ItemStack stack;

    public ItemStackRecipeWrapper(ItemStack stack)
    {
        this.stack = stack;
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public int getContainerSize()
    {
        return 1;
    }

    @Override
    public boolean isEmpty()
    {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getItem(int index)
    {
        return stack;
    }

    @Override
    public ItemStack removeItem(int index, int count)
    {
        return stack.split(count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index)
    {
        return stack.split(stack.getCount());
    }

    @Override
    public void setItem(int index, ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public void clearContent()
    {
        stack = ItemStack.EMPTY;
    }
}