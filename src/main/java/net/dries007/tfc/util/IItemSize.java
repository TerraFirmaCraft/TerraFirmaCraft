/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.util;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface IItemSize
{
    Size getSize(ItemStack stack);

    Weight getWeight(ItemStack stack);

    default boolean canStack(ItemStack stack)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    default void addSizeInfo(ItemStack stack, List<String> text)
    {
        text.add(I18n.format("\u2696 " + Helpers.getEnumName(getWeight(stack)) + " \u21F2 " + Helpers.getEnumName(getSize(stack))));
    }

}
