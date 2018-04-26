/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.EnumSet;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.Metal;

/**
 * Must be on Item or Block
 */
public interface IFireable
{
    static IFireable fromItem(Item item)
    {
        if (item instanceof IFireable) return ((IFireable) item);
        if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IFireable)
            return ((IFireable) ((ItemBlock) item).getBlock());
        return null;
    }

    default EnumSet<Metal.Tier> getFireableTiers()
    {
        return EnumSet.of(Metal.Tier.TIER_I, Metal.Tier.TIER_II);
    }

    ItemStack getFiringResult(ItemStack stack, Metal.Tier tier);
}
