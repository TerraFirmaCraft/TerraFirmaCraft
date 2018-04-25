package net.dries007.tfc.objects.items.pottery;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemUnfiredSmallVessel extends ItemUnfiredPottery
{
    public final boolean glazed;

    public ItemUnfiredSmallVessel(ItemSmallVessel firedVersion)
    {
        super(firedVersion);
        glazed = firedVersion.glazed;
        setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (!isInCreativeTab(tab)) return;

        if (!glazed)
            items.add(new ItemStack(this));
        else
            for (EnumDyeColor color : EnumDyeColor.values())
                items.add(new ItemStack(this, 1, color.getDyeDamage()));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (!glazed)
            return super.getUnlocalizedName(stack);
        return super.getUnlocalizedName(stack) + "." + EnumDyeColor.byDyeDamage(stack.getItemDamage()).getName();
    }
}
