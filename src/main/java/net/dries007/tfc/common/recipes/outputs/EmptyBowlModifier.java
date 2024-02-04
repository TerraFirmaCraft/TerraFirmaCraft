/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.dries007.tfc.common.capabilities.food.DynamicBowlHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;

public enum EmptyBowlModifier implements ItemStackModifier.SingleInstance<EmptyBowlModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        final ItemStack output = FoodCapability.get(input) instanceof DynamicBowlHandler handler ? handler.getBowl() : ItemStack.EMPTY;
        return !output.isEmpty()
            ? output
            // Reasonable default for display in i.e. JEI for soups obtained directly from the creative menu.
            // Prevents them from displaying empty. Works as long as the bowl handler itself doesn't ever have an empty bowl (it shouldn't)
            : new ItemStack(Items.BOWL);
    }

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public EmptyBowlModifier instance()
    {
        return INSTANCE;
    }
}

