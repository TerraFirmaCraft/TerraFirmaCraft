/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.BlocksTFC;

public class TreeGenBushes implements ITreeGenerator
{

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand)
    {

    }

    @Override
    public boolean canGenerateTree(World world, BlockPos pos, Tree treeType)
    {
        // Check if there is soil beneath
        if (!BlocksTFC.isSoil(world.getBlockState(pos.down())))
            return false;

        // Check the position for liquids, etc.
        if (world.getBlockState(pos).getMaterial().isLiquid() || !world.getBlockState(pos).getMaterial().isReplaceable())
            if (!(world.getBlockState(pos) instanceof BlockSapling))
                return false;

        // Check if there is sufficient light level
        return world.getLightFromNeighbors(pos) >= 7;
    }
}
