/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.size;

import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.util.Helpers;

/**
 * Interface for item size.
 * To implement this, you can (preferred) implement this interface on your Item / ItemBlock or
 * Expose this capability via Item#initCapabilities()
 * Otherwise, your item will be assigned a default capability on creation
 */
public interface IItemSize
{
    static int getStackSize(Size size, Weight weight, boolean canStack)
    {
        return canStack ? Math.min(size.stackSize * weight.multiplier, 64) : 1;
    }

    Size getSize(@Nonnull ItemStack stack);

    Weight getWeight(@Nonnull ItemStack stack);

    default boolean canStack(@Nonnull ItemStack stack)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    default void addSizeInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
    {
        text.add("\u2696 " + I18n.format(Helpers.getEnumName(getWeight(stack))) + " \u21F2 " + I18n.format(Helpers.getEnumName(getSize(stack))));
    }

    default int getStackSize(@Nonnull ItemStack stack)
    {
        return getStackSize(getSize(stack), getWeight(stack), canStack(stack));
    }

}
