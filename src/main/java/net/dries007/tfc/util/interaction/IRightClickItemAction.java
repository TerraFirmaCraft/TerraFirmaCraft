/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Used as an intercept point for {@link net.minecraft.item.Item#onItemRightClick(World, EntityPlayer, EnumHand)}
 *
 * @see InteractionManager
 */
@FunctionalInterface
public interface IRightClickItemAction extends IRightClickBlockAction
{
    EnumActionResult onRightClickItem(World worldIn, EntityPlayer playerIn, EnumHand handIn);

    @Override
    default EnumActionResult onRightClickBlock(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing direction, float hitX, float hitY, float hitZ)
    {
        return onRightClickItem(worldIn, player, hand);
    }
}
