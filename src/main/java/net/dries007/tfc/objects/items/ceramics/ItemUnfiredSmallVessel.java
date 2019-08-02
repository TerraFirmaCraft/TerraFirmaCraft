/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import javax.annotation.Nonnull;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemUnfiredSmallVessel extends ItemPottery
{
    public final boolean glazed;

    public ItemUnfiredSmallVessel(boolean glazed)
    {
        this.glazed = glazed;
        setHasSubtypes(glazed);
    }

    @Override
    @Nonnull
    public String getTranslationKey(ItemStack stack)
    {
        if (!glazed)
        {
            return super.getTranslationKey(stack);
        }
        return super.getTranslationKey(stack) + "." + EnumDyeColor.byDyeDamage(stack.getItemDamage()).getName();
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            if (!glazed)
            {
                items.add(new ItemStack(this));
            }
            else
            {
                for (EnumDyeColor color : EnumDyeColor.values())
                {
                    items.add(new ItemStack(this, 1, color.getDyeDamage()));
                }
            }
        }
    }
}
