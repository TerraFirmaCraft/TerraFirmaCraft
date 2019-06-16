/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IPlaceableItem;
import net.dries007.tfc.objects.blocks.metal.BlockMetalSheet;
import net.dries007.tfc.objects.te.TEMetalSheet;
import net.dries007.tfc.util.Helpers;

public class ItemMetalSheet extends ItemMetal implements IPlaceableItem
{
    public ItemMetalSheet(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing facing, Vec3d hitVec)
    {
        if (world.getBlockState(pos).isNormalCube() && stack.getItem() instanceof ItemMetalSheet)
        {
            ItemMetalSheet sheet = (ItemMetalSheet) stack.getItem();
            BlockPos posAt = pos.offset(facing);
            IBlockState stateAt = world.getBlockState(posAt);

            if (stateAt.getBlock() instanceof BlockMetalSheet)
            {
                // Existing sheet block
                Metal metal = ((BlockMetalSheet) stateAt.getBlock()).getMetal();
                if (metal == sheet.metal)
                {
                    return placeSheet(world, posAt, facing);
                }
            }
            else if (stateAt.getBlock().isReplaceable(world, posAt))
            {
                // Place a new block
                if (!world.isRemote)
                {
                    world.setBlockState(posAt, BlockMetalSheet.get(sheet.metal).getDefaultState().withProperty(BlockMetalSheet.FACE_PROPERTIES[facing.getIndex()], true));
                    placeSheet(world, posAt, facing);
                }
                return true;
            }
        }
        return false;
    }

    private boolean placeSheet(World world, BlockPos pos, EnumFacing facing)
    {
        TEMetalSheet tile = Helpers.getTE(world, pos, TEMetalSheet.class);
        if (tile != null && !tile.getFace(facing))
        {
            if (!world.isRemote)
            {
                tile.setFace(facing, true);
                world.playSound(null, pos.offset(facing), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return true;
        }
        return false;
    }
}