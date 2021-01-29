/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class TFCShieldItem extends ShieldItem
{
    private final IItemTier tier;

    public TFCShieldItem(IItemTier tier, Properties builder)
    {
        super(builder.defaultDurability(tier.getUses()));
        this.tier = tier;
    }

    public IItemTier getTier()
    {
        return this.tier;
    }

    @Override
    public int getEnchantmentValue()
    {
        return this.tier.getEnchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }
}