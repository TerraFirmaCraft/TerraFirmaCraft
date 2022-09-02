/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.items.DynamicBowlFood;

public enum EmptyBowlModifier implements ItemStackModifier.SingleInstance<EmptyBowlModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return input.getCapability(FoodCapability.CAPABILITY)
            .filter(cap -> cap instanceof DynamicBowlFood.DynamicBowlHandler)
            .map(cap -> ((DynamicBowlFood.DynamicBowlHandler) cap).getBowl())
            // Reasonable default for display in i.e. JEI for soups obtained directly from the creative menu.
            // Prevents them from displaying empty. Works as long as the bowl handler itself doesn't ever have an empty bowl (it shouldn't)
            .filter(s -> !s.isEmpty())
            .orElseGet(() -> new ItemStack(Items.BOWL));
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

