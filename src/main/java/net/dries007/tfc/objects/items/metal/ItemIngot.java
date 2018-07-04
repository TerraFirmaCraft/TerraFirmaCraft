/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.ParametersAreNonnullByDefault;

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
import net.dries007.tfc.objects.blocks.metal.BlockIngotPile;
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
        ItemIngot item = (ItemIngot) stack.getItem();
        if (!(world.getBlockState(pos).getBlock() instanceof BlockIngotPile))
        {
            if (facing == EnumFacing.UP && world.getBlockState(pos).isNormalCube() && player.isSneaking())
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
            if (facing == EnumFacing.UP)
            {
                TEIngotPile te = Helpers.getTE(world, pos, TEIngotPile.class);
                if (te != null && te.getCount() == 64 && te.getMetal() == item.metal)
                {
                    if (!world.isRemote)
                    {
                        //noinspection ConstantConditions
                        world.setBlockState(pos.up(), BlocksTFC.INGOT_PILE.getDefaultState());
                        te.setMetal(item.metal);
                        te.setCount(1);
                    }
                    world.playSound(null, pos.up(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.3F, 1.5F);
                    return true;
                }
            }
        }
        return false;
    }
}
