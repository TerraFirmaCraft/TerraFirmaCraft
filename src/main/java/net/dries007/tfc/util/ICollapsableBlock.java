/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariantFallable;
import net.dries007.tfc.objects.blocks.wood.BlockSupport;

// For raw stone, because it collapses
public interface ICollapsableBlock
{
    // Used to get cobblestone variant of this collapsable raw stone
    BlockRockVariantFallable getFallingVariant();

    // Check if this pos is collapsable
    default boolean canCollapse(World world, BlockPos pos)
    {
        return world.getBlockState(pos.down()).getMaterial().isReplaceable();
    }

    /**
     * Check an area of blocks for collapsing mechanics
     *
     * @param worldIn the worldObj this block is in
     * @param pos     the BlockPos this block has been mined from
     */
    default void checkCollapsingArea(World worldIn, BlockPos pos)
    {
        if (worldIn.isRemote || !worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            return; //First, let's check if this area is loaded and is on server
        if (Constants.RNG.nextDouble() < ConfigTFC.GENERAL.collapseChance) //Then, we check rng if a collapse should trigger
        {
            //Rng the radius
            int radX = (Constants.RNG.nextInt(5) + 4) / 2;
            int radY = (Constants.RNG.nextInt(3) + 2) / 2;
            int radZ = (Constants.RNG.nextInt(5) + 4) / 2;
            for (BlockPos checking : BlockSupport.getAllUnsupportedBlocksIn(worldIn, pos.add(-radX, -radY, -radZ), pos.add(radX, radY, radZ))) //9x5x9 max
            {
                //Check the area for a block collapse!
                if (worldIn.getBlockState(checking).getBlock() instanceof ICollapsableBlock)
                {
                    ICollapsableBlock block = (ICollapsableBlock) worldIn.getBlockState(checking).getBlock();
                    if (block.canCollapse(worldIn, checking)) //Still needs this to check if this can collapse without support(ie: no blocks below)
                    {
                        //Trigger collapse!
                        block.collapseArea(worldIn, checking);
                        worldIn.playSound(null, pos, TFCSounds.ROCK_SLIDE_LONG, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return; //Don't need to check other blocks
                    }
                }
            }
        }

    }

    /**
     * Collapse an area of ICollapsableBlocks!
     *
     * @param world       the worldObj
     * @param centerPoint the center of this area
     */
    default void collapseArea(World world, BlockPos centerPoint)
    {
        int radiusH = (world.rand.nextInt(31) + 5) / 2; //5-36
        for (BlockPos cavein : BlockSupport.getAllUnsupportedBlocksIn(world, centerPoint.add(-radiusH, -4, -radiusH), centerPoint.add(radiusH, 1, radiusH)))
        {
            IBlockState st = world.getBlockState(cavein);
            if (st.getBlock() instanceof ICollapsableBlock
                && ((ICollapsableBlock) st.getBlock()).canCollapse(world, cavein))
            {
                double distSqrd =
                    Math.pow(centerPoint.getX() - cavein.getX(), 2)
                        + Math.pow(centerPoint.getY() - cavein.getY(), 2)
                        + Math.pow(centerPoint.getZ() - cavein.getZ(), 2);
                double chance = ConfigTFC.GENERAL.propogateCollapseChance - 0.01 * Math.sqrt(distSqrd);
                if (Constants.RNG.nextDouble() < chance)
                {
                    BlockRockVariantFallable fallingBlock = ((ICollapsableBlock) st.getBlock()).getFallingVariant();
                    world.setBlockState(cavein, fallingBlock.getDefaultState());
                    fallingBlock.checkFalling(world, cavein, world.getBlockState(cavein));
                }
            }
        }
    }
}