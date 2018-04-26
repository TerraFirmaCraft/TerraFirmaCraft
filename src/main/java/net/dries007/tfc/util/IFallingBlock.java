package net.dries007.tfc.util;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

public interface IFallingBlock
{
    default boolean canFallThrough(IBlockState state)
    {
        return BlockFalling.canFallThrough(state) || state.getBlock() instanceof BlockFluidBase;
    }

    default boolean shouldFall(IBlockState state, World world, BlockPos pos)
    {
        return canFallThrough(world.getBlockState(pos.add(0, -1, 0)));
    }

    default Iterable<ItemStack> getDropsFromFall(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound teData, int fallTime, float fallDistance)
    {
        return ImmutableList.of(new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state)));
    }

    default void onEndFalling(World world, BlockPos pos, IBlockState state, IBlockState current) {}
}
