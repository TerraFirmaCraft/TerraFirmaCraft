/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.items;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.blocks.BlockSlabTFC;
import net.dries007.tfc.util.IItemSize;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemSlabTFC extends ItemSlab implements IItemSize
{
    public ItemSlabTFC(BlockSlabTFC.Half slab, BlockSlabTFC.Half slab1, BlockSlabTFC.Double doubleSlab)
    {
        super(slab, slab1, doubleSlab);
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
