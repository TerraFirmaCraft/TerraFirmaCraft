/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;

// For raw stone, because it collapses
public interface ICollapsableBlock extends IFallingBlock
{
    default void checkFalling(World worldIn, BlockPos pos, IBlockState state)
    {
        if (shouldFall(worldIn, pos))
        {
            int chance = 10; //TODO modifiers to make this block fall easier?
            if (worldIn.getBlockState(pos.up()).getMaterial().isReplaceable()) chance += 10;
            if (worldIn.getBlockState(pos.north()).getMaterial().isReplaceable()) chance += 10;
            if (worldIn.getBlockState(pos.south()).getMaterial().isReplaceable()) chance += 10;
            if (worldIn.getBlockState(pos.east()).getMaterial().isReplaceable()) chance += 10;
            if (worldIn.getBlockState(pos.west()).getMaterial().isReplaceable()) chance += 10;
            //Check for "silk touch effect"
            if (chance == 60)
            { //If you add a modifier to change the odds, please change this line accordingly
                worldIn.setBlockToAir(pos);
                Helpers.spawnItemStack(worldIn, pos, new ItemStack(state.getBlock(), 1));
            }


            //TODO change the falling block to cobblestone variant

            if (worldIn.rand.nextInt(100) < chance)
            {
                BlockPos pos1 = getFallablePos(worldIn, pos);
                if (pos1 != null && !isBeingSupported(worldIn, pos))
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
                    //Play sound on block falling
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }
}
