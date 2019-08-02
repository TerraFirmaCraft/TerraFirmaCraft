/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.item.ItemStack;

public class ItemUnfiredJug extends ItemPottery
{

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
