/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api.util;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.*;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;

public interface ITreeGenerator
{

    /**
     * This is a copy of the method included in the Template class, with some key differences.
     * This will ignore TEs / Entities, and does less checks for bad usage, since it will only be used for tree worldgen
     * It will do an additional check that the block is replaceable; important for tree growth; as to not replace other blocks
     *
     * @param worldIn     the world
     * @param pos         the position
     * @param template    the template
     * @param placementIn the placement settings
     */
    static void addStructureToWorld(World worldIn, BlockPos pos, Template template, PlacementSettings placementIn)
    {
        int flags = 2;
        ITemplateProcessor templateProcessor = new BlockRotationProcessor(pos, placementIn);
        StructureBoundingBox structureboundingbox = placementIn.getBoundingBox();

        for (Template.BlockInfo template$blockinfo : template.blocks)
        {
            BlockPos blockpos = Template.transformedBlockPos(placementIn, template$blockinfo.pos).add(pos);
            Template.BlockInfo template$blockinfo1 = templateProcessor.processBlock(worldIn, blockpos, template$blockinfo);

            if (template$blockinfo1 != null)
            {
                Block block1 = template$blockinfo1.blockState.getBlock();

                if ((!placementIn.getIgnoreStructureBlock() || block1 != Blocks.STRUCTURE_BLOCK) && (structureboundingbox == null || structureboundingbox.isVecInside(blockpos)))
                {
                    IBlockState iblockstate = template$blockinfo1.blockState.withMirror(placementIn.getMirror());
                    IBlockState iblockstate1 = iblockstate.withRotation(placementIn.getRotation());

                    if (worldIn.getBlockState(blockpos).getMaterial().isReplaceable() || worldIn.getBlockState(blockpos).getBlock() instanceof BlockLeavesTFC)
                        worldIn.setBlockState(blockpos, iblockstate1, flags);

                }
            }
        }

        for (Template.BlockInfo template$blockinfo2 : template.blocks)
        {
            BlockPos blockpos1 = Template.transformedBlockPos(placementIn, template$blockinfo2.pos).add(pos);

            if (structureboundingbox == null || structureboundingbox.isVecInside(blockpos1))
            {
                worldIn.notifyNeighborsRespectDebug(blockpos1, template$blockinfo2.blockState.getBlock(), false);
            }

        }
    }

    /**
     * This only sets the properties used by ITreeGenerator.addStructureToWorld
     *
     * @return A default set of placement settings for tree generation
     */

    static PlacementSettings getDefaultSettings()
    {
        return new PlacementSettings().setIgnoreStructureBlock(false);
    }

    /**
     * This only sets the properties used by ITreeGenerator.addStructureToWorld
     *
     * @param rand For generating random settings
     * @return A set of placement settings with random rotation
     */
    static PlacementSettings getRandomSettings(Random rand)
    {
        return getDefaultSettings().setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);
    }

    /**
     * Called to generate a tree. Each Tree must have one of these. Used for world gen and sapling growth
     *
     * @param manager an instance of the world's template manager. Used for getting structures.
     * @param world   The world
     * @param pos     The position where the sapling was / would've been
     * @param tree    The tree type to spawn
     * @param rand    A random to use in generation
     */
    void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand);

    /**
     * Checks if a tree can be generated. This implementation checks height, radius, and light level
     *
     * @param world    The world
     * @param pos      The pos of the tree
     * @param treeType The tree type (for checking if the tree can generate)
     * @return true if the tree can generate.
     */
    default boolean canGenerateTree(World world, BlockPos pos, Tree treeType)
    {
        // Check if ground is flat enough
        final int radius = treeType.getMaxGrowthRadius();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                if ((x == 0 && z == 0) ||
                    world.getBlockState(pos.add(x, 0, z)).getMaterial().isReplaceable() ||
                    ((x > 1 || z > 1) && world.getBlockState(pos.add(x, 1, z)).getMaterial().isReplaceable()))
                    continue;
                return false;
            }
        }
        // Check if there is room directly upwards
        final int height = treeType.getMaxHeight();
        for (int y = 1; y <= height; y++)
            if (!world.getBlockState(pos.up(y)).getMaterial().isReplaceable())
                return false;

        // Check if there is a solid block beneath
        if (!BlocksTFC.isSoil(world.getBlockState(pos.down())))
            return false;

        // Check the position for liquids, etc.
        if (world.getBlockState(pos).getMaterial().isLiquid() || !world.getBlockState(pos).getMaterial().isReplaceable())
            if (!(world.getBlockState(pos).getBlock() instanceof BlockSapling))
                return false;

        // Check if there is sufficient light level
        return world.getLightFromNeighbors(pos) >= 7;
    }
}
