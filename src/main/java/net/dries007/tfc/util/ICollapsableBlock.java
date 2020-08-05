/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.worldtracker.CapabilityWorldTracker;
import net.dries007.tfc.api.capability.worldtracker.CollapseData;
import net.dries007.tfc.api.capability.worldtracker.WorldTracker;
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
     * @return true if a collapse did occur, false otherwise
     */
    default boolean checkCollapsingArea(World worldIn, BlockPos pos)
    {
        if (worldIn.isRemote || !worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
            return false; //First, let's check if this area is loaded and is on server
        if (Constants.RNG.nextDouble() < ConfigTFC.General.FALLABLE.collapseChance) //Then, we check rng if a collapse should trigger
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
                        return true; //Don't need to check other blocks
                    }
                }
            }
        }
        return false;
    }

    /**
     * Collapse an area of ICollapsableBlocks!
     *
     * @param world       the worldObj
     * @param centerPoint the center of this area
     */
    default void collapseArea(World world, BlockPos centerPoint)
    {
        int radius = (world.rand.nextInt(31) + 5) / 2;
        int radiusSquared = radius * radius;
        List<BlockPos> secondaryPositions = new ArrayList<>();
        // Initially only scan on the bottom layer, and advance upwards
        for (BlockPos pos : BlockPos.getAllInBoxMutable(centerPoint.add(-radius, -4, -radius), centerPoint.add(radius, -4, radius)))
        {
            boolean foundEmpty = false; // If we've found a space to collapse into
            for (int y = 0; y <= 8; y++)
            {
                BlockPos posAt = pos.up(y);
                IBlockState stateAt = world.getBlockState(posAt);
                if (foundEmpty && stateAt.getBlock() instanceof ICollapsableBlock && ((ICollapsableBlock) stateAt.getBlock()).canCollapse(world, posAt) && !BlockSupport.isBeingSupported(world, posAt))
                {
                    // Check for a possible collapse
                    if (posAt.distanceSq(centerPoint) < radiusSquared && world.rand.nextFloat() < ConfigTFC.General.FALLABLE.propagateCollapseChance)
                    {
                        // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                        BlockRockVariantFallable fallingBlock = ((ICollapsableBlock) stateAt.getBlock()).getFallingVariant();
                        world.setBlockState(posAt, fallingBlock.getDefaultState());
                        fallingBlock.checkFalling(world, posAt, world.getBlockState(posAt), true);
                        secondaryPositions.add(posAt.up());
                        break;
                    }
                }
                if (IFallingBlock.canFallThrough(world, posAt, stateAt.getMaterial()))
                {
                    foundEmpty = true;
                }
            }
        }

        if (!secondaryPositions.isEmpty())
        {
            WorldTracker tracker = world.getCapability(CapabilityWorldTracker.CAPABILITY, null);
            if (tracker != null)
            {
                tracker.addCollapseData(new CollapseData(centerPoint, secondaryPositions, radiusSquared));
            }
        }
    }
}