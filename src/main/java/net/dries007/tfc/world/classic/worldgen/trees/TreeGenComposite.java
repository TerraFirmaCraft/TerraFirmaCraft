/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.LinkedListMultimap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.ITreeGenerator;

public class TreeGenComposite implements ITreeGenerator
{
    private final LinkedListMultimap<Float, ITreeGenerator> gens;
    private float totalWeight;

    public TreeGenComposite()
    {
        gens = LinkedListMultimap.create();
        totalWeight = 0f;
    }

    public TreeGenComposite add(float chance, ITreeGenerator gen)
    {
        gens.put(chance, gen);
        totalWeight += chance;
        return this;
    }

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand, boolean isWorldGen)
    {
        if (gens.isEmpty())
            return;
        float r = rand.nextFloat() * totalWeight;
        float countWeight = 0f;
        for (Map.Entry<Float, ITreeGenerator> entry : gens.entries())
        {
            countWeight += entry.getKey();
            if (countWeight >= r)
            {
                entry.getValue().generateTree(manager, world, pos, tree, rand, isWorldGen);
                return;
            }
        }
    }
}
