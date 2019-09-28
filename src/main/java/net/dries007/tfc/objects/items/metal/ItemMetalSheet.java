/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.metal.BlockMetalSheet;
import net.dries007.tfc.objects.te.TEMetalSheet;
import net.dries007.tfc.util.Helpers;

public class ItemMetalSheet extends ItemMetal
{
    public ItemMetalSheet(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (worldIn.getBlockState(pos).isNormalCube() && stack.getItem() instanceof ItemMetalSheet)
        {
            ItemMetalSheet sheet = (ItemMetalSheet) stack.getItem();
            BlockPos posAt = pos.offset(facing);
            IBlockState stateAt = worldIn.getBlockState(posAt);

            if (stateAt.getBlock() instanceof BlockMetalSheet)
            {
                // Existing sheet block
                Metal metal = ((BlockMetalSheet) stateAt.getBlock()).getMetal();
                if (metal == sheet.metal)
                {
                    stack.shrink(1);
                    player.setHeldItem(hand, stack);
                    return placeSheet(worldIn, posAt, facing);
                }
            }
            else if (stateAt.getBlock().isReplaceable(worldIn, posAt))
            {
                // Place a new block
                if (!worldIn.isRemote)
                {
                    worldIn.setBlockState(posAt, BlockMetalSheet.get(sheet.metal).getDefaultState().withProperty(BlockMetalSheet.FACE_PROPERTIES[facing.getIndex()], true));
                    stack.shrink(1);
                    player.setHeldItem(hand, stack);
                    placeSheet(worldIn, posAt, facing);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    private EnumActionResult placeSheet(World world, BlockPos pos, EnumFacing facing)
    {
        TEMetalSheet tile = Helpers.getTE(world, pos, TEMetalSheet.class);
        if (tile != null && !tile.getFace(facing))
        {
            if (!world.isRemote)
            {
                tile.setFace(facing, true);
                world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}