/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.forge.ForgingBonus;

public enum CopyForgingBonusModifier implements ItemStackModifier.SingleInstance<CopyForgingBonusModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        ForgingBonus.set(stack, ForgingBonus.get(input));
        return stack;
    }

    @Override
    public CopyForgingBonusModifier instance()
    {
        return INSTANCE;
    }
}
