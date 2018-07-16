/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.trees;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;

public class TreeGenNormal implements ITreeGenerator
{

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Wood tree, Random rand)
    {
        ResourceLocation base = new ResourceLocation(Constants.MOD_ID, tree + "/base");
        ResourceLocation overlay = new ResourceLocation(Constants.MOD_ID, tree + "/overlay");

        Template structureBase = manager.getTemplate(world.getMinecraftServer(), base);
        Template structureOverlay = manager.getTemplate(world.getMinecraftServer(), overlay);

        PlacementSettings settings = WorldGenTrees.getDefaultSettings();

        int height = 1 + rand.nextInt(4);

        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, height, -size.getZ() / 2);

        if (WorldGenTrees.canGenerateTree(world, pos.down(height), structureBase, settings, tree))
        {
            structureBase.addBlocksToWorld(world, pos, settings);
            structureOverlay.addBlocksToWorld(world, pos, settings.setIntegrity(0.5f));

            final IBlockState log = BlockLogTFC.get(tree).getDefaultState();
            for (int i = 0; i < height; i++)
                world.setBlockState(pos.add(size.getX() / 2, i - height, size.getZ() / 2), log);
        }
    }

}
