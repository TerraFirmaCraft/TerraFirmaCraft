/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.trees;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;

public class TreeGenTall implements ITreeGenerator
{
    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Wood tree, Random rand)
    {
        ResourceLocation base = new ResourceLocation(Constants.MOD_ID, tree + "/base");
        ResourceLocation overlay = new ResourceLocation(Constants.MOD_ID, tree + "/overlay");

        Template structureBase = manager.getTemplate(world.getMinecraftServer(), base);
        Template structureOverlay = manager.getTemplate(world.getMinecraftServer(), overlay);

        PlacementSettings settings = WorldGenTrees.getDefaultSettings();
        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);

        if (WorldGenTrees.canGenerateTree(world, pos, structureBase, settings, tree))
        {
            structureBase.addBlocksToWorld(world, pos, settings);
            structureOverlay.addBlocksToWorld(world, pos, settings.setIntegrity(0.5f));
        }
    }
}
