/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import net.minecraft.item.ItemStack;

public class ItemFiredPottery extends ItemPottery
{
    @Override
    public ItemStack getFiringResult(ItemStack input)
    {
        return input; // Already fired pottery does nothing.
    }
}
