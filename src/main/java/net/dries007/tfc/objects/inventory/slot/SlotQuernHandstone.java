/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import net.dries007.tfc.objects.items.ItemHandstone;
import net.dries007.tfc.objects.te.TEQuern;

public class SlotQuernHandstone extends SlotItemHandler
{
    private static boolean isHandstone(ItemStack stack)
    {
        return stack.getItem() instanceof ItemHandstone;
    }

    TEQuern tile;

    public SlotQuernHandstone(@Nonnull IItemHandler inventory, int idx, int x, int y, TEQuern tile)
    {
        super(inventory, idx, x, y);
        this.tile = tile;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return isHandstone(stack);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack)
    {
        return 1;
    }

    @Override
    public void onSlotChanged()
    {
        tile.onHandstoneSlotChange();
    }
}