/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.tools;

import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

public class TFCShieldItem extends ShieldItem
{
    private final IItemTier tier;

    public TFCShieldItem(IItemTier tier, Properties builder)
    {
        super(builder.defaultMaxDamage(tier.getMaxUses()));
        this.tier = tier;
    }

    public IItemTier getTier()
    {
        return this.tier;
    }

    @Override
    public int getItemEnchantability()
    {
        return this.tier.getEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }
}
