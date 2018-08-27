/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.MetalEnum;

public interface IMetalObject
{
    @SideOnly(Side.CLIENT)
    default void addMetalInfo(ItemStack stack, List<String> text)
    {
        MetalEnum metal = getMetal(stack);
        if (metal == null) return;
        text.add("");
        text.add(I18n.format("tfc.tooltip.metal", I18n.format(Helpers.getEnumName(metal))));
        if (isSmeltable(stack))
        {
            text.add(I18n.format("tfc.tooltip.units", getSmeltAmount(stack)));
            text.add(I18n.format("tfc.tooltip.smelting", I18n.format(Helpers.getEnumName(metal.tier))));
        }
    }

    MetalEnum getMetal(ItemStack stack);

    default boolean isSmeltable(ItemStack stack)
    {
        return getMetal(stack) != null;
    }

    int getSmeltAmount(ItemStack stack);
}
