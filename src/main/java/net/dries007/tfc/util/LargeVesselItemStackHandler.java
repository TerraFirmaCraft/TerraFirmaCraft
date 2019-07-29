/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class LargeVesselItemStackHandler extends ItemStackHandler
{

	public LargeVesselItemStackHandler(int slots)
	{
		super(slots);
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate)
	{
		IFood cap = getStackInSlot(slot).getCapability(CapabilityFood.CAPABILITY, null);

		if (cap != null)
		{
			if (cap.getTraits().contains(CapabilityFood.LARGE_VESSEL_PRESERVED))
			{
				cap.getTraits().remove(CapabilityFood.LARGE_VESSEL_PRESERVED);
				cap.setCreationDate(CalendarTFC.PLAYER_TIME.getTicks() - (long) ((CalendarTFC.PLAYER_TIME.getTicks() - cap.getCreationDate()) * CapabilityFood.LARGE_VESSEL_PRESERVED.getDecayModifier()));
			}
		}

		return super.extractItem(slot, amount, simulate);
	}
}
