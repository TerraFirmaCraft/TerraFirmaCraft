/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemSmallVessel extends ItemFiredPottery
{
    public final boolean glazed;

    public ItemSmallVessel(boolean glazed)
    {
        this.glazed = glazed;
        setHasSubtypes(glazed);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        if (!glazed)
            return super.getTranslationKey(stack);
        return super.getTranslationKey(stack) + "." + EnumDyeColor.byDyeDamage(stack.getItemDamage()).getName();
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
}
