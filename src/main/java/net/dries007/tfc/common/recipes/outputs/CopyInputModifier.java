/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public CopyInputModifier instance()
    {
        return INSTANCE;
    }
}
