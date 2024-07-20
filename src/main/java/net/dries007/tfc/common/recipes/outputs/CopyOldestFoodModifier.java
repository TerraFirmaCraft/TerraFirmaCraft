/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import com.google.common.collect.Lists;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public enum CopyOldestFoodModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        return FoodCapability.updateFoodFromAllPrevious(Lists.newArrayList(RecipeHelpers.getCraftingInput()), stack);
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.COPY_OLDEST_FOOD.get();
    }
}
