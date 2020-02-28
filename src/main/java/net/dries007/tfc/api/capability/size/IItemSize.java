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
 * To implement this, you can (preferred) implement this interface on your Item / Block and return the size or
 * Expose this capability via Item#initCapabilities()
 * Note: if you implement this via an interface, you must also change the stack-size of the item to agree with {@link IItemSize#getStackSize}
 * If you implement the capability, TFC will try and auto-adjust the max stacksize of the item for you
 * Otherwise, your item will be assigned a default capability on creation
 *
 * @see net.dries007.tfc.objects.items.ItemTFC
 * @see net.dries007.tfc.objects.items.itemblock.ItemBlockTFC
 */
public interface IItemSize
{
    static int getStackSize(Size size, Weight weight, boolean canStack)
    {
        return canStack ? Math.min(size.stackSize * weight.multiplier, 64) : 1;
    }

    @Nonnull
    Size getSize(@Nonnull ItemStack stack);

    @Nonnull
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
