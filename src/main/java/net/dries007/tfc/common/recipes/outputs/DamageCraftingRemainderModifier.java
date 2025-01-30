/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;

public enum DamageCraftingRemainderModifier implements ItemStackModifier
{
    INSTANCE;

    @Override
    @SuppressWarnings("deprecation") // For damageItem(), but we don't have access to a level here
    public ItemStack apply(ItemStack stack, ItemStack input, Context context)
    {
        if (stack.isDamageableItem())
        {
            final ItemStack copy = stack.copy();
            final @Nullable Player player = RecipeHelpers.getCraftingPlayer();
            if (player != null)
            {
                Helpers.damageItem(copy, player.level());
            }
            else
            {
                Helpers.damageItem(copy);
            }
            return copy;
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
