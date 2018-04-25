package net.dries007.tfc.util;

import net.dries007.tfc.objects.Metal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.EnumSet;

/**
 * Must be on Item or Block
 */
public interface IFireable
{
    default EnumSet<Metal.Tier> getFireableTiers()
    {
        return EnumSet.of(Metal.Tier.TIER_I, Metal.Tier.TIER_II);
    }

    ItemStack getFiringResult(ItemStack stack, Metal.Tier tier);

    static IFireable fromItem(Item item)
    {
        if (item instanceof IFireable) return ((IFireable) item);
        if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IFireable) return ((IFireable) ((ItemBlock) item).getBlock());
        return null;
    }
}
