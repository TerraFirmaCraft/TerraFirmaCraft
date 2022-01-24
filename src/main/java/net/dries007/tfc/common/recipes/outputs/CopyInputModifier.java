package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

public enum CopyInputModifier implements ItemStackModifier.SingleInstance<CopyInputModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return input.copy();
    }

    @Override
    public CopyInputModifier instance()
    {
        return INSTANCE;
    }
}
