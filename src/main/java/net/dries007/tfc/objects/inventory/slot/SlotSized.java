/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class SlotSized extends SlotItemHandler
{
    private final Size size;
    private final Weight weight;

    public SlotSized(IItemHandler inv, int idx, int x, int y, Size size, Weight weight)
    {
        super(inv, idx, x, y);
        this.size = size;
        this.weight = weight;
    }

    public SlotSized(IItemHandler inv, int idx, int x, int y, Size size)
    {
        this(inv, idx, x, y, size, Weight.HEAVY);
    }

    public SlotSized(IItemHandler inv, int idx, int x, int y, Weight weight)
    {
        this(inv, idx, x, y, Size.HUGE, weight);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        // todo: check super
        IItemSize size = CapabilityItemSize.getIItemSize(stack);
        if (size == null)
            return false;
        return size.getSize(stack).isSmallerThan(this.size) && size.getWeight(stack).isSmallerThan(this.weight);
    }
}
