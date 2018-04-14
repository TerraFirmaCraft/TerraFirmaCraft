package net.dries007.tfc.objects.items.metal;

import net.dries007.tfc.objects.Metal;
import net.minecraft.item.ItemStack;

public class ItemUnshaped extends ItemMetal
{
    public ItemUnshaped(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
        setMaxDamage(100);
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return 100 - stack.getItemDamage();
    }
}
