/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEIngotPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IPlacableItem;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemIngot extends ItemMetal implements IPlacableItem
{
    public ItemIngot(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing facing, Vec3d hitVec)
    {
        if (!player.isSneaking()) return false;

        ItemIngot item = (ItemIngot) stack.getItem();
        //noinspection ConstantConditions
        if (!(world.getBlockState(pos).getBlock() == BlocksTFC.INGOT_PILE))
        {
            if (facing == EnumFacing.UP && world.getBlockState(pos).isNormalCube())
            {
                if (!world.isRemote)
                {
                    //noinspection ConstantConditions
                    world.setBlockState(pos.up(), BlocksTFC.INGOT_PILE.getDefaultState());
                    TEIngotPile te = Helpers.getTE(world, pos.up(), TEIngotPile.class);
                    if (te != null)
                    {
                        te.setMetal(item.metal);
                        te.setCount(1);
                    }
                    world.playSound(null, pos.up(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);

                }
                return true;
            }
        }
        else
        {
            // Place an ingot pile ONTOP of the existing one
            BlockPos posTop = pos.down();
            IBlockState stateTop;
            do
            {
                posTop = posTop.up();
                stateTop = world.getBlockState(posTop);
                //noinspection ConstantConditions
                if (stateTop.getBlock() == BlocksTFC.INGOT_PILE)
                {
                    TEIngotPile te = Helpers.getTE(world, posTop, TEIngotPile.class);
                    if (te.getCount() < 64)
                    {
                        te.setCount(te.getCount() + 1);
                        world.playSound(null, pos.up(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);
                        return true;
                    }
                }
                else if (stateTop.getBlock().isReplaceable(world, posTop))
                {
                    world.setBlockState(posTop, BlocksTFC.INGOT_PILE.getDefaultState());
                    TEIngotPile te = Helpers.getTE(world, posTop, TEIngotPile.class);
                    if (te != null)
                    {
                        te.setMetal(item.metal);
                        te.setCount(1);
                    }
                    world.playSound(null, posTop, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);
                    return true;
                }
                else
                {
                    return false;
                }

            } while (posTop.getY() <= 256);
        }
        return false;
    }
}
