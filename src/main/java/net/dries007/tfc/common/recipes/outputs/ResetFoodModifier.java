/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.component.food.FoodCapability;

public enum ResetFoodModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        return FoodCapability.setCreatedNow(stack);
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.RESET_FOOD.get();
    }
}
