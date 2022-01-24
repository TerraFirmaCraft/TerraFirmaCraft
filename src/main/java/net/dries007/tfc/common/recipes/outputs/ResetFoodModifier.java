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
