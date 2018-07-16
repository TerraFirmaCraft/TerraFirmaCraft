/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.trees;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.objects.Wood;

public interface ITreeGenerator
{
    void generateTree(TemplateManager manager, World world, BlockPos pos, Wood tree, Random rand);
}
