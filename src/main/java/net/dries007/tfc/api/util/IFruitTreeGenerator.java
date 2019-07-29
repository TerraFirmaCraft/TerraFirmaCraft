/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.worldgen.trees.FruitTreeGen;

public interface IFruitTreeGenerator
{
    IFruitTreeGenerator DEFAULT = new FruitTreeGen();

    /**
     * Called to generate a tree. Each Tree must have one of these. Used for world gen and sapling growth
     *
     * @param manager an instance of the world's template manager. Used for getting structures.
     * @param world   The world
     * @param pos     The position where the sapling was / would've been
     * @param tree    The tree type to spawn
     * @param rand    A random to use in generation
     */
    void generateTree(TemplateManager manager, World world, BlockPos pos, IFruitTree tree, Random rand);

    /**
     * Checks if a tree can be generated. This implementation checks height, radius, and light level
     *
     * @param world    The world
     * @param pos      The pos of the tree
     * @param treeType The tree type (for checking if the tree can generate)
     * @return true if the tree can generate.
     */
    default boolean canGenerateTree(World world, BlockPos pos, IFruitTree treeType)
    {
        // Check if there is room directly upwards
        for (int y = 1; y <= 5; y++)
            if (!world.getBlockState(pos.up(y)).getMaterial().isReplaceable())
                return false;

        // Check if there is a solid block beneath
        if (!BlocksTFC.isSoil(world.getBlockState(pos.down())))
            return false;

        // Check the position for liquids, etc.
        if (world.getBlockState(pos).getMaterial().isLiquid() || !world.getBlockState(pos).getMaterial().isReplaceable())
        {
            return false;
        }

        // Check if there is sufficient light level
        return world.getLightFromNeighbors(pos) >= 7;
    }
}
