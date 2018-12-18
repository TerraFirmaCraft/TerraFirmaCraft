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
    @Nullable
    Rock getRock(ItemStack stack);

    @Nonnull
    RockCategory getRockCategory(ItemStack stack);

    /**
     * Adds metal info to the item stack
     * This is only shown when advanced item tooltips is enabled
     *
     * @param stack The item stack
     * @param text  The text to be added
     */
    @SideOnly(Side.CLIENT)
    default void addRockInfo(ItemStack stack, List<String> text)
    {
        Rock rock = getRock(stack);
        if (rock == null) return;
        text.add("");
        text.add("Rock: " + rock.toString());
        text.add("Category: " + rock.getRockCategory().toString());
    }
}
