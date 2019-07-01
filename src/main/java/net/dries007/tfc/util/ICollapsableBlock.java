/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// For raw stone, because it collapses
public interface ICollapsableBlock extends IFallingBlock
{
    default void checkCollapse(World worldIn, BlockPos pos, IBlockState state)
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
                checkFalling(worldIn, pos, state);
                worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
