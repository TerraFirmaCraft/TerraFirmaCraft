/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.BowlComponent;
import net.dries007.tfc.common.component.TFCComponents;

public enum EmptyBowlModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        return stack.getOrDefault(TFCComponents.BOWL, BowlComponent.DISPLAY).bowl();
    }

    @Override
    public boolean dependsOnInput()
    {
        return true;
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.EMPTY_BOWL.get();
    }
}

