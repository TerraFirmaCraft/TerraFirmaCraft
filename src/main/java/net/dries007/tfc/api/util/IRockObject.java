/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;

public interface IRockObject
{
    /**
     * This is nullable because some objects don't have a rock type, just a category (like tool heads)
     *
     * @param stack The item stack to check
     * @return The rock, or null if it isn't relavant / doesn't exist
     */
    @Nullable
    Rock getRock(ItemStack stack);

    @Nonnull
    RockCategory getRockCategory(ItemStack stack);

    /**
     * Adds rock info to the item stack
     * This is only shown when advanced item tooltips is enabled
     *
     * @param stack The item stack
     * @param text  The text to be added
     */
    @SideOnly(Side.CLIENT)
    default void addRockInfo(ItemStack stack, List<String> text)
    {
        text.add("");
        Rock rock = getRock(stack);
        if (rock != null) text.add("Rock: " + rock.toString());
        text.add("Category: " + getRockCategory(stack).toString());
    }
}
