/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;

public enum ResetFoodModifier implements ItemStackModifier.SingleInstance<ResetFoodModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return FoodCapability.updateFoodDecayOnCreate(stack);
    }

    @Override
    public ResetFoodModifier instance()
    {
        return INSTANCE;
    }
}
