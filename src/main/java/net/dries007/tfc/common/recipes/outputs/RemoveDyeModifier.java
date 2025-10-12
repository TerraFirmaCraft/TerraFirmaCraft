/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public enum RemoveDyeModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        input.remove(DataComponents.DYED_COLOR);
        return input;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.REMOVE_DYE.get();
    }
}
