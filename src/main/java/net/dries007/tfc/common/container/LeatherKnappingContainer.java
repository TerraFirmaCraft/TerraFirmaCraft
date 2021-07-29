package net.dries007.tfc.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.recipes.KnappingRecipe;

public class LeatherKnappingContainer extends KnappingContainer
{
    public LeatherKnappingContainer(ContainerType<?> containerType, IRecipeType<? extends KnappingRecipe> recipeType, int windowId, PlayerInventory playerInv, int amountToConsume, boolean consumeAfterComplete, boolean usesDisabledTex, SoundEvent sound)
    {
        super(containerType, recipeType, windowId, playerInv, amountToConsume, consumeAfterComplete, usesDisabledTex, sound);
    }

    @Override
    protected void consumeIngredientStackAfterComplete()
    {
        super.consumeIngredientStackAfterComplete();
        // offhand is not included in 'items'
        if (player.getOffhandItem().getItem().is(TFCTags.Items.KNIVES))
        {
            player.getOffhandItem().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(Hand.OFF_HAND));
        }
        for (ItemStack invItem : player.inventory.items)
        {
            if (invItem.getItem().is(TFCTags.Items.KNIVES))
            {
                // safe to do nothing as broadcasting break handles item use (which you can't do in the inventory)
                invItem.hurtAndBreak(1, player, p -> {});
                break;
            }
        }
    }
}
