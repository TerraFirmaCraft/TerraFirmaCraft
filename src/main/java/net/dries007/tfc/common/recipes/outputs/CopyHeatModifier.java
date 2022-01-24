package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;

public enum CopyHeatModifier implements ItemStackModifier.SingleInstance<CopyHeatModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        stack.getCapability(HeatCapability.CAPABILITY).ifPresent(outputCap -> input.getCapability(HeatCapability.CAPABILITY).ifPresent(inputCap -> outputCap.setTemperature(inputCap.getTemperature())));
        return stack;
    }

    @Override
    public CopyHeatModifier instance()
    {
        return INSTANCE;
    }
}
