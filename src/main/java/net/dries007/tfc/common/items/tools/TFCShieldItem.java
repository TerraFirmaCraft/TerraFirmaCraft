/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

import net.minecraft.item.Item.Properties;

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