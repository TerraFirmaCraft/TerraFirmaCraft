/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public interface IItemHandlerSidedCallback
{
    boolean canInsert(int slot, ItemStack stack, EnumFacing side);

    boolean canExtract(int slot, EnumFacing side);
}
