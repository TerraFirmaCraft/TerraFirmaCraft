/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;

import static net.minecraft.block.BlockLog.LOG_AXIS;

public class TreeGenWillow implements ITreeGenerator
{
    private Template structureBase;
    private Template structureOverlay;
    private PlacementSettings settingsWeak;
    private PlacementSettings settingsFull;

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand)
    {
        ResourceLocation base = new ResourceLocation(Constants.MOD_ID, tree.name + "/base");
        ResourceLocation overlay = new ResourceLocation(Constants.MOD_ID, tree.name + "/overlay");

        structureBase = manager.get(world.getMinecraftServer(), base);
        structureOverlay = manager.get(world.getMinecraftServer(), overlay);

        if (structureBase == null || structureOverlay == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString() + " or " + overlay.toString());
            return;
        }
        settingsFull = WorldGenTrees.getDefaultSettings();
        settingsWeak = WorldGenTrees.getDefaultSettings().setIntegrity(0.5F);

        if (!WorldGenTrees.canGenerateTree(world, pos, structureBase, settingsFull, tree))
            return;

        int height = 3 + rand.nextInt(3), branches = 2 + rand.nextInt(3), x1, z1, y1;
        for (int n = 0; n <= height; n++)
        {
            tryPlaceLog(world, pos.up(n), tree, BlockLog.EnumAxis.Y);
            if (n >= 3)
                createLeafGroup(world, pos.up(n));
        }

        for (int n = 0; n < branches; n++)
        {
            x1 = (rand.nextBoolean() ? 1 : -1) * (1 + rand.nextInt(3));
            z1 = (rand.nextBoolean() ? 1 : -1) * (1 + rand.nextInt(3));
            y1 = 1 + rand.nextInt(2);
            createBranch(world, pos.up(n + 1), x1, y1, z1, rand, tree);
            createLeafGroup(world, pos.add(x1, y1, z1));
        }
    }

    private void createBranch(World world, BlockPos pos1, int x, int y, int z, Random rand, Tree tree)
    {
        int x1 = x / Math.abs(x), z1 = z / Math.abs(z);
        do
        {
            if (x != 0 && rand.nextBoolean())
                x -= x1;
            if (z != 0 && rand.nextBoolean())
                z -= z1;
            tryPlaceLog(world, pos1.add(x, y, z), tree, rand.nextBoolean() ? BlockLog.EnumAxis.X : BlockLog.EnumAxis.Z);
            if (rand.nextBoolean())
                createLeafGroup(world, pos1.add(x, y, z));
        }
        while (Math.abs(x) + Math.abs(z) > 1);
    }

    private void createLeafGroup(World world, BlockPos pos)
    {

        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);

        structureBase.addBlocksToWorld(world, pos, settingsFull);
        structureOverlay.addBlocksToWorld(world, pos, settingsWeak);
    }

    private void tryPlaceLog(World world, BlockPos pos, Tree tree, BlockLog.EnumAxis axis)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.AIR || block instanceof BlockLeavesTFC)
            world.setBlockState(pos, BlockLogTFC.get(tree).getDefaultState().withProperty(LOG_AXIS, axis));
    }
}
