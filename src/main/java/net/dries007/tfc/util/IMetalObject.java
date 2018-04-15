package net.dries007.tfc.util;

import net.dries007.tfc.objects.Metal;
import net.minecraft.item.ItemStack;

import java.util.List;

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
