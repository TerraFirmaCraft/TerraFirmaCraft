/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.item.ItemStack;

/**
 * A {@link net.minecraftforge.items.wrapper.RecipeWrapper} for single item stacks.
 */
public class ItemStackInventory implements EmptyInventory
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
}