/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.List;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.Metal;

public interface IMetalObject
{
    default void addMetalInfo(ItemStack stack, List<String> text)
    {
        Metal metal = getMetal(stack);
        if (metal == null) return;
        text.add("");
        text.add("Metal: " + metal); // todo: localize
        if (isSmeltable(stack))
            text.add("Smeltable for " + getSmeltAmount(stack) + " units in a " + metal.tier.name); // todo: localize
    }

    Metal getMetal(ItemStack stack);

    default boolean isSmeltable(ItemStack stack)
    {
        return getMetal(stack) != null;
    }

    int getSmeltAmount(ItemStack stack);
}
