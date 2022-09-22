/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public enum CopyOldestFoodModifier implements ItemStackModifier.SingleInstance<CopyOldestFoodModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        final CraftingContainer container = RecipeHelpers.getCraftingContainer();
        if (container != null)
        {
            final List<ItemStack> stacks = new ArrayList<>();
            for (int slot = 0; slot < container.getContainerSize(); slot++)
            {
                stacks.add(container.getItem(slot));
            }
            return FoodCapability.updateFoodFromAllPrevious(stacks, stack);
        }
        return stack;
    }

    @Override
    public CopyOldestFoodModifier instance()
    {
        return this;
    }
}
