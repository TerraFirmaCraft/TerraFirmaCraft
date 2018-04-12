package net.dries007.tfc.util;

import net.dries007.tfc.objects.Metal;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IMetalObject
{
    default void addMetalInfo(ItemStack stack, List<String> text)
    {
        text.add("Metal: " + getMetal(stack)); // todo: localize
        if (isSmeltable(stack))
            text.add("Smeltable for " + getSmeltAmount(stack) + " units in a " + getMetal(stack).tier.name); // todo: localize
    }

    Metal getMetal(ItemStack stack);

    boolean isSmeltable(ItemStack stack);

    int getSmeltAmount(ItemStack stack);
}
