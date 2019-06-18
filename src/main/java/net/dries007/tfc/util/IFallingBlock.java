/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;

public interface IFallingBlock
{
    default boolean canFallThrough(IBlockState state)
    {
        return state.getMaterial().isReplaceable();
    }

    // Can the block fall at a particular position; ignore horizontal falling
    default boolean shouldFall(World world, BlockPos pos)
    {
        return canFallThrough(world.getBlockState(pos.down()));
    }

    // Get the position that the block will fall from (allows for horizontal falling)
    @Nullable
    BlockPos getFallablePos(World world, BlockPos pos);

    default void checkFalling(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos pos1 = getFallablePos(worldIn, pos);
        if (pos1 != null)
        {
            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            {
                if (!pos1.equals(pos))
                {
                    worldIn.setBlockToAir(pos);
                    worldIn.setBlockState(pos1, state);
                }
                worldIn.spawnEntity(new EntityFallingBlockTFC(worldIn, pos1, this, worldIn.getBlockState(pos1)));
            }
            else
            {
                worldIn.setBlockToAir(pos);
                pos1 = pos1.down();
                while (canFallThrough(worldIn.getBlockState(pos1)) && pos1.getY() > 0)
                    pos1 = pos1.down();
                if (pos1.getY() > 0) worldIn.setBlockState(pos1.up(), state); // Includes Forge's fix for data loss.
            }
        }
    }

    default Iterable<ItemStack> getDropsFromFall(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound teData, int fallTime, float fallDistance)
    {
        return ImmutableList.of(new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state)));
    }

    default void onEndFalling(World world, BlockPos pos, IBlockState state, IBlockState current)
    {
    }
}
