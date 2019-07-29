/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemHandstone extends ItemCraftingTool
{
	ItemHandstone()
	{
		super(250, Size.NORMAL, Weight.HEAVY, "handstone");
	}

	@Override
	public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player)
	{
		return true;
	}
}
