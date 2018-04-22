package net.dries007.tfc.util;

import net.dries007.tfc.objects.Metal;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;

public interface IFireable
{
    default EnumSet<Metal.Tier> getFireableTiers()
    {
        return EnumSet.of(Metal.Tier.TIER_I, Metal.Tier.TIER_II);
    }

    ItemStack getFiringResult(ItemStack stack, Metal.Tier tier);
}
