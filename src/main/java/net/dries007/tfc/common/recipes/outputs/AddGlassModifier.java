/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.glass.GlassWorkData;

public enum AddGlassModifier implements ItemStackModifier.SingleInstance<AddGlassModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        GlassWorkData.createNewBatch(stack, input);
        return stack;
    }

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public AddGlassModifier instance()
    {
        return INSTANCE;
    }
}
