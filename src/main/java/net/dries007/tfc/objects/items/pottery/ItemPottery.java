package net.dries007.tfc.objects.items.pottery;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.util.IFireable;
import net.dries007.tfc.util.IPlacableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemPottery extends Item implements IPlacableItem, IFireable
{
    @Override
    public ItemStack getFiringResult(ItemStack input, Metal.Tier tier)
    {
        return input; // Already fired pottery does noting.
    }
}
