/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.wood.BlockSupport;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;

public interface IFallingBlock
{
    /**
     * In general, falling blocks will destroy all non solid blocks, EXCEPT, soft falling blocks won't destroy hard materials
     */
    Set<Material> SOFT_MATERIALS = new HashSet<>(Arrays.asList(Material.GROUND, Material.SAND, Material.GRASS, Material.CLAY));
    Set<Material> HARD_MATERIALS = new HashSet<>(Arrays.asList(Material.IRON, BlockCharcoalPile.CHARCOAL_MATERIAL));

    static boolean canFallThrough(World world, BlockPos pos, Material fallingBlockMaterial)
    {
        IBlockState targetState = world.getBlockState(pos);
        if (SOFT_MATERIALS.contains(fallingBlockMaterial) && HARD_MATERIALS.contains(targetState.getMaterial()))
        {
            return false;
        }
        if (!world.isSideSolid(pos, EnumFacing.UP))
        {
            return true;
        }
        return !targetState.isFullBlock();
    }

    default boolean shouldFall(World world, BlockPos posToFallAt, BlockPos originalPos)
    {
        return shouldFall(world, posToFallAt, originalPos, false);
    }

    default boolean shouldFall(World world, BlockPos posToFallAt, BlockPos originalPos, boolean ignoreSupportChecks)
    {
        // Can the block fall at a particular position; ignore horizontal falling
        return ConfigTFC.General.FALLABLE.enable && canFallThrough(world, posToFallAt.down(), world.getBlockState(originalPos).getMaterial()) && (ignoreSupportChecks || !BlockSupport.isBeingSupported(world, originalPos));
    }

    // Get the position that the block will fall from (allows for horizontal falling)
    @Nullable
    BlockPos getFallablePos(World world, BlockPos pos, boolean ignoreSupportChecks);

    default BlockPos getFallablePos(World world, BlockPos pos)
    {
        return getFallablePos(world, pos, false);
    }

    default boolean checkFalling(World worldIn, BlockPos pos, IBlockState state)
    {
        return checkFalling(worldIn, pos, state, false);
    }

    /**
     * Check if this block gonna fall.
     *
     * @param worldIn the world
     * @param pos     the position of the original block
     * @param state   the state of the original block
     * @return true if this block has fallen, false otherwise
     */
    default boolean checkFalling(World worldIn, BlockPos pos, IBlockState state, boolean ignoreSupportChecks)
    {
        // Initial check for loaded area to fix stack overflow crash from endless falling / liquid block updates
        if (worldIn.isAreaLoaded(pos.add(-2, -2, -2), pos.add(2, 2, 2)))
        {
            BlockPos pos1 = getFallablePos(worldIn, pos, ignoreSupportChecks);
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
                    while (canFallThrough(worldIn, pos1, state.getMaterial()) && pos1.getY() > 0)
                    {
                        pos1 = pos1.down();
                    }
                    if (pos1.getY() > 0) worldIn.setBlockState(pos1.up(), state); // Includes Forge's fix for data loss.
                }
                return true;
            }
        }
        return false;
    }

    default Iterable<ItemStack> getDropsFromFall(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound teData, int fallTime, float fallDistance)
    {
        return ImmutableList.of(new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state)));
    }
}
