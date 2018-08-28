/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Metal;

public class ItemUnfiredPottery extends ItemPottery
{
    public final ItemFiredPottery firedVersion;

    public ItemUnfiredPottery(ItemFiredPottery firedVersion)
    {
        this.firedVersion = firedVersion;
    }

    @Override
    public ItemStack getFiringResult(ItemStack input, Metal.Tier tier)
    {
        ItemStack output = new ItemStack(firedVersion);
        if (input.getHasSubtypes() && output.getHasSubtypes()) output.setItemDamage(input.getMetadata());
        return output;
    }
}
