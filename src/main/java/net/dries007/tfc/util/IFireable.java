/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPitKiln;

/**
 * Must be on Item or Block
 */
public interface IFireable extends IPlacableItem
{
    static IFireable fromItem(Item item)
    {
        if (item instanceof IFireable) return ((IFireable) item);
        if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof IFireable)
            return ((IFireable) ((ItemBlock) item).getBlock());
        return null;
    }

    default EnumSet<Metal.Tier> getFireableTiers()
    {
        return EnumSet.of(Metal.Tier.TIER_I, Metal.Tier.TIER_II);
    }

    ItemStack getFiringResult(ItemStack stack, Metal.Tier tier);

    @Override
    default boolean placeItemInWorld(World world, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing facing, Vec3d hitVec)
    {
        IFireable fireable = fromItem(stack.getItem());
        if (fireable != null && player.isSneaking() && facing == EnumFacing.UP)
        {

            if (fireable.getFireableTiers().contains(Metal.Tier.TIER_I))
            {
                //noinspection ConstantConditions
                if (world.getBlockState(pos).getBlock() != BlocksTFC.PIT_KILN)
                {
                    if (!world.isSideSolid(pos, EnumFacing.UP)) return false;
                    pos = pos.add(0, 1, 0); // also important for TE fetch
                    if (!world.getBlockState(pos).getMaterial().isReplaceable())
                        return false; // can't put down the block
                    //noinspection ConstantConditions
                    world.setBlockState(pos, BlocksTFC.PIT_KILN.getDefaultState());
                }

                TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
                if (te != null)
                {
                    // If you change this, make sure it works in both +x/z and -x/z
                    te.onRightClick(player, stack, Math.round(hitVec.x) < hitVec.x, Math.round(hitVec.z) < hitVec.z);
                    return true;
                }
            }
        }
        return false;
    }
}
