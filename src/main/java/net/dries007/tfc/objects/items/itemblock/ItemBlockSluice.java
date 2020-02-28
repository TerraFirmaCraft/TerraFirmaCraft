/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.itemblock;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.blocks.devices.BlockSluice;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemBlockSluice extends ItemBlockTFC
{
    public ItemBlockSluice(Block block)
    {
        super(block);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
        else if (facing != EnumFacing.UP)
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            if (!worldIn.getBlockState(pos).getMaterial().isReplaceable() || !worldIn.getBlockState(pos.offset(player.getHorizontalFacing())).getMaterial().isReplaceable())
            {
                pos = pos.up(); //try the above
            }
            if (!worldIn.getBlockState(pos).getMaterial().isReplaceable() || !worldIn.getBlockState(pos.offset(player.getHorizontalFacing())).getMaterial().isReplaceable())
            {
                return EnumActionResult.FAIL;
            }
            ItemStack stack = player.getHeldItem(hand);
            BlockPos upperPos = pos.offset(player.getHorizontalFacing());
            //Creating a thatch bed
            if (player.canPlayerEdit(upperPos, facing, stack) && player.canPlayerEdit(pos, facing, stack))
            {
                stack.shrink(1);
                IBlockState lowerState = this.block.getDefaultState().withProperty(BlockSluice.FACING, player.getHorizontalFacing()).withProperty(BlockSluice.UPPER, false);
                IBlockState upperState = this.block.getDefaultState().withProperty(BlockSluice.FACING, player.getHorizontalFacing()).withProperty(BlockSluice.UPPER, true);
                worldIn.setBlockState(pos, lowerState);
                worldIn.setBlockState(upperPos, upperState);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }
}
