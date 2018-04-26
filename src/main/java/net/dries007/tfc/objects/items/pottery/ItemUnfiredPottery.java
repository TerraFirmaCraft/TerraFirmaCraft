package net.dries007.tfc.objects.items.pottery;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.Metal;

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
        if (input.getHasSubtypes()) output.setItemDamage(input.getMetadata());
        return output;
    }
}
