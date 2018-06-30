/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.inventory;

import java.util.function.Function;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import net.dries007.tfc.objects.te.TESidedInventory;

public class SlotTEInput extends SlotItemHandler
{

    private final TESidedInventory te;
    private final Function<ItemStack, Boolean> f;

    public SlotTEInput(@Nonnull IItemHandler inventory, int idx, int x, int y, @Nonnull TESidedInventory te, @Nonnull Function<ItemStack, Boolean> f)
    {
        super(inventory, idx, x, y);
        this.te = te;
        this.f = f;
    }

    @Override
    public void onSlotChanged()
    {
        te.setAndUpdateSlots(getSlotIndex());
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return f.apply(stack);
    }

    @Override
    public int getSlotStackLimit()
    {
        return te.getSlotLimit(getSlotIndex());
    }
}
