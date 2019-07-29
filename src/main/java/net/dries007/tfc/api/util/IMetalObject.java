/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.Helpers;

/*
 * Must be on Item or Block (with ItemBlock, i.e. do not implement on blocks that have a separate item block)
 */
public interface IMetalObject
{
    /**
     * Adds metal info to the item stack
     * This is only shown when advanced item tooltips is enabled
     *
     * @param stack The item stack
     * @param text  The text to be added
     */
    @SideOnly(Side.CLIENT)
    default void addMetalInfo(ItemStack stack, List<String> text)
    {
        Metal metal = getMetal(stack);
        if (metal == null) return;
        text.add("");
        text.add(I18n.format("tfc.tooltip.metal", I18n.format(Helpers.getTypeName(metal))));
        if (isSmeltable(stack))
        {
            text.add(I18n.format("tfc.tooltip.units", getSmeltAmount(stack)));
            text.add(I18n.format("tfc.tooltip.smelting", I18n.format(Helpers.getEnumName(metal.getTier()))));
        }
    }

    /**
     * @param stack the item stack. This can assume that it is of the right item type and do casts without checking
     * @return the metal of the stack
     */
    @Nullable
    Metal getMetal(ItemStack stack);

    /**
     * @param stack The item stack
     * @return true if the item is able to be melted down into liquid metal
     */
    default boolean isSmeltable(ItemStack stack)
    {
        return getMetal(stack) != null;
    }

    /**
     * @param stack The item stack
     * @return the amount of liquid metal that this item will create (in TFC units or mB: 1 unit = 1 mB)
     */
    int getSmeltAmount(ItemStack stack);
}
