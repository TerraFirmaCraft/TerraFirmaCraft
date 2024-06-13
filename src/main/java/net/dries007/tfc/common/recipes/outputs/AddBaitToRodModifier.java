/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.items.TFCFishingRodItem;
import net.dries007.tfc.common.recipes.RecipeHelpers;

public enum AddBaitToRodModifier implements ItemStackModifier.SingleInstance<AddBaitToRodModifier>
{
    INSTANCE;

    @Override
    public ItemStack apply(ItemStack stack, ItemStack primaryInput)
    {
        for (ItemStack input : RecipeHelpers.getCraftingInput())
        {
            TFCFishingRodItem.BaitType baitType = TFCFishingRodItem.getBaitType(input);
            if (baitType != TFCFishingRodItem.BaitType.NONE)
            {
                stack.getOrCreateTag().put("bait", input.save(new CompoundTag()));
                return stack;
            }
        }
        return stack;
    }

    @Override
    public AddBaitToRodModifier instance()
    {
        return INSTANCE;
    }
}
