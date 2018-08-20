/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.items;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.util.IItemSize;
import net.dries007.tfc.api.util.Size;
import net.dries007.tfc.api.util.Weight;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBlockTFC extends ItemBlock implements IItemSize
{

    public ItemBlockTFC(Block b)
    {
        super(b);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.VERY_SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getStackSize(stack);
    }
}
