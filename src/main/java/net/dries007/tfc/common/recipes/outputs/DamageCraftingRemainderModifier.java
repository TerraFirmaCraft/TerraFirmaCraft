/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;

public enum DamageCraftingRemainderModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        if (stack.isDamageableItem())
        {
            return Helpers.damageCraftingItem(stack, 1).copy();
        }
        else if (stack.has(DataComponents.UNBREAKABLE)) // unbreakable items are not damageable, but should still be able to be used in crafting
        {
            return stack.copy();
        }
        else if (stack.hasCraftingRemainingItem())
        {
            return stack.getCraftingRemainingItem();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ItemStackModifierType<?> type()
    {
        return ItemStackModifiers.DAMAGE_CRAFTING_REMAINDER.get();
    }
}
