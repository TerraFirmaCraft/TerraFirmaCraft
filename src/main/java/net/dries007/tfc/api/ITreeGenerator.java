/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.api;

import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.api.types.Tree;

public interface ITreeGenerator
{

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
     * @return A default set of placement settings for tree generation
     */

    static PlacementSettings getDefaultSettings()
    {
        return new PlacementSettings()
            .setIgnoreEntities(true)
            .setIgnoreStructureBlock(false);
    }

    /**
     * @param rand For generating random settings
     * @return A set of placement settings with random rotation
     */
    static PlacementSettings getRandomSettings(Random rand)
    {
        return getDefaultSettings()
            .setRotation(Rotation.values()[rand.nextInt(Rotation.values().length)]);
    }

    static boolean checkGenerationConditions(World world, BlockPos pos, Tree treeType)
    {
        // Check if ground is flat enough
        final int radius = treeType.maxGrowthRadius;
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
        final int height = treeType.maxHeight;
        for (int y = 1; y <= height; y++)
            if (!world.getBlockState(pos.up(y)).getMaterial().isReplaceable())
                return false;

        // Check if there is a solid block beneath
        if (!world.getBlockState(pos.down()).isNormalCube())
            return false;

        // Check the position for liquids, etc.
        if (world.getBlockState(pos).getMaterial().isLiquid() || !world.getBlockState(pos).getMaterial().isReplaceable())
            if (!(world.getBlockState(pos) instanceof BlockSapling))
                return false;

        // Check if there is sufficient light level
        return world.getLightFromNeighbors(pos) >= 7;
    }

    /**
     * Checks if a tree can be generated. This implementation checks height, radius, and light level
     * Suggested use is to use within your implementation of generateTree()
     *
     * @param world    The world
     * @param pos      The pos of the tree
     * @param treeType The tree type (for checking if the tree can generate)
     * @return true if the tree can generate.
     */
    default boolean canGenerateTree(World world, BlockPos pos, Tree treeType)
    {
        return checkGenerationConditions(world, pos, treeType);
    }
}
