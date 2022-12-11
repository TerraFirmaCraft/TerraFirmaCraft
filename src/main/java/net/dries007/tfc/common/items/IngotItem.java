/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class IngotItem extends Item
{
    public IngotItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean isPiglinCurrency(ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.PIGLIN_BARTERING_INGOTS);
    }
}
