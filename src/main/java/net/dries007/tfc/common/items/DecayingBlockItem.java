/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.capabilities.food.FoodCapability;

/**
 * Like {@link DecayingItem} but for {@link BlockItem}
 */
public class DecayingBlockItem extends BlockItem
{
    public DecayingBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public void fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items)
    {
        // Explicitly set the food item non-decaying.
        if (allowdedIn(category))
        {
            items.add(FoodCapability.setStackNonDecaying(new ItemStack(this)));
        }
    }
}
