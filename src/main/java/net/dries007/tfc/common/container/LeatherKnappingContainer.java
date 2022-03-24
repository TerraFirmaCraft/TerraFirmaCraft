/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.Helpers;

public class LeatherKnappingContainer extends KnappingContainer
{
    public LeatherKnappingContainer(MenuType<?> containerType, RecipeType<? extends KnappingRecipe> recipeType, int windowId, Inventory playerInv, ItemStack stack, InteractionHand hand, int amountToConsume, boolean consumeAfterComplete, boolean usesDisabledTex, SoundEvent sound)
    {
        super(containerType, recipeType, windowId, playerInv, stack, hand, amountToConsume, consumeAfterComplete, usesDisabledTex, sound);
    }

    @Override
    protected void consumeIngredientStackAfterComplete()
    {
        super.consumeIngredientStackAfterComplete();

        // offhand is not included in 'items'
        if (Helpers.isItem(player.getOffhandItem().getItem(), TFCTags.Items.KNIVES))
        {
            player.getOffhandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
        }
        else
        {
            for (ItemStack invItem : player.getInventory().items)
            {
                if (Helpers.isItem(invItem.getItem(), TFCTags.Items.KNIVES))
                {
                    // safe to do nothing as broadcasting break handles item use (which you can't do in the inventory)
                    invItem.hurtAndBreak(1, player, p -> {});
                    break;
                }
            }
        }
    }
}
