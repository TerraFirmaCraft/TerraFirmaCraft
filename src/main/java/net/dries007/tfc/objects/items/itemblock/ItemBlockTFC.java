/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemBlockTFC extends ItemBlock implements IItemSize
{
    private final IItemSize size;

    public ItemBlockTFC(Block block)
    {
        this(block, block instanceof IItemSize ? (IItemSize) block : null);
    }

    public ItemBlockTFC(Block block, @Nullable IItemSize size)
    {
        super(block);

        this.size = size;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return size != null ? size.getSize(stack) : Size.SMALL; // Stored everywhere
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return size != null ? size.getWeight(stack) : Weight.LIGHT; // Stacksize = 32
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return size == null || size.canStack(stack);
    }

    /**
     * @see net.dries007.tfc.objects.items.ItemTFC#getItemStackLimit(ItemStack)
     */
    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getStackSize(stack);
    }
}
