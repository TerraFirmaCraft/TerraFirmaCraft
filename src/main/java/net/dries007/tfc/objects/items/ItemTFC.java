/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.IItemSize;

@ParametersAreNonnullByDefault
public abstract class ItemTFC extends Item implements IItemSize
{
    /**
     * This should NOT be overridden except for VERY SPECIAL cases
     * If an item needs to not stack, i.e. small vessels, override {@link IItemSize#canStack(ItemStack)}
     * If an item needs a variable stack size, override {@link IItemSize#getWeight(ItemStack)} / {@link IItemSize#getSize(ItemStack)} and return a different value to get a different stack size
     */
    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getStackSize(stack);
    }
}
