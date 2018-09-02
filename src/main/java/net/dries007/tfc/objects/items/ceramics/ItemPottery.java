/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.IFireable;
import net.dries007.tfc.util.IPlacableItem;

public class ItemPottery extends ItemTFC implements IPlacableItem, IFireable
{
    @Override
    public ItemStack getFiringResult(ItemStack input, Metal.Tier tier)
    {
        return input; // Already fired pottery does nothing.
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }
}
