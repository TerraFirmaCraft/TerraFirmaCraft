/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.util;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.util.Helpers;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IItemSize
{
    static int getStackSize(Size size, Weight weight, boolean canStack)
    {
        return canStack ? Math.min(size.stackSize * weight.multiplier, 64) : 1;
    }

    Size getSize(ItemStack stack);

    Weight getWeight(ItemStack stack);

    default boolean canStack(ItemStack stack)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    default void addSizeInfo(ItemStack stack, List<String> text)
    {
        text.add("\u2696 " + I18n.format(Helpers.getEnumName(getWeight(stack))) + " \u21F2 " + I18n.format(Helpers.getEnumName(getSize(stack))));
    }

    default int getStackSize(ItemStack stack)
    {
        return getStackSize(getSize(stack), getWeight(stack), canStack(stack));
    }

}
