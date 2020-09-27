/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.ItemStack;

/**
 * A {@link net.minecraftforge.items.wrapper.RecipeWrapper} for single item stacks.
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