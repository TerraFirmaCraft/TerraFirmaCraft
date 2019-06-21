/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.objects.items.ItemHandstone;
import net.dries007.tfc.objects.te.TEInventory;

public class SlotQuernHandstone extends SlotTEInput
{
    private static boolean isHandstone(ItemStack stack)
    {
        return stack.getItem() instanceof ItemHandstone;
    }

    public SlotQuernHandstone(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull TEInventory te)
    {
        super(inventory, idx, x, y, te);
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
}