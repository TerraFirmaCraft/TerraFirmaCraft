/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SlotFluidTransfer extends SlotItemHandler
{
    public SlotFluidTransfer(IItemHandler inv, int idx, int x, int y)
    {
        super(inv, idx, x, y);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack)
    {
        return stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
    }
}
