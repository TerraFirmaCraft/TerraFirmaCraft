/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.MetalEnum;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.IFireable;
import net.dries007.tfc.util.IPlacableItem;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemPottery extends ItemTFC implements IPlacableItem, IFireable
{
    @Override
    public ItemStack getFiringResult(ItemStack input, MetalEnum.Tier tier)
    {
        return input; // Already fired pottery does nothing.
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }
}
