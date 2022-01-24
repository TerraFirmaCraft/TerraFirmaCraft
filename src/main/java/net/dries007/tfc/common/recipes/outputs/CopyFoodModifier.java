package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;

public enum CopyFoodModifier implements ItemStackModifier.SingleInstance<CopyFoodModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return FoodCapability.updateFoodFromPrevious(input, stack);
    }

    @Override
    public CopyFoodModifier instance()
    {
        return INSTANCE;
    }
}
