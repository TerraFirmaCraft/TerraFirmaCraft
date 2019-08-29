/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;

import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.ITreeGenerator;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;
import net.dries007.tfc.world.classic.StructureHelper;

import static net.dries007.tfc.objects.blocks.wood.BlockLogTFC.PLACED;
import static net.minecraft.block.BlockLog.LOG_AXIS;
import static net.minecraft.block.BlockVine.*;

public class TreeGenKapok implements ITreeGenerator
{
    private static final PlacementSettings settings = StructureHelper.getDefaultSettings();
    private static final BlockPos[] trunkPos = new BlockPos[] {
        new BlockPos(0, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(-1, 0, -1)
    };
    private IBlockState trunk;
    private IBlockState bark;

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand, boolean isWorldGen)
    {
        trunk = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false);
        bark = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false).withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);

        int height = 12 + rand.nextInt(8);
        int branches = 2 + rand.nextInt(3);

        int x1, y1, z1, type;
        for (int i = 0; i < branches; i++)
        {
            y1 = 6 + rand.nextInt(height - 8);
            x1 = rand.nextInt(3);
            z1 = rand.nextInt(3);
            if (rand.nextBoolean())
                x1 = -x1 - 1;
            if (rand.nextBoolean())
                x1 = -z1 - 1;
            if (x1 == 0 || x1 == -1)
                x1 = x1 * 3 + 1;
            if (z1 == 0 || z1 == -1)
                z1 = z1 * 3 + 1;
            type = 1 + rand.nextInt(3);
            placeBranch(manager, world, pos.add(x1, y1, z1), tree.getRegistryName() + "/branch" + type);
            checkAndPlace(world, pos.add(x1 - Math.abs(x1) / x1, y1 - 1, z1 - Math.abs(z1) / z1), true);
        }

        for (int i = -1; i < height; i++)
            placeTrunk(world, pos.add(0, i, 0));
        placeBranch(manager, world, pos.add(0, height, 0), tree.getRegistryName() + "/top");

    }

    @Override
    public boolean canGenerateTree(World world, BlockPos pos, Tree treeType)
    {
        for (BlockPos p1 : trunkPos)
        {
            if (BlocksTFC.isSoil(world.getBlockState(pos.add(p1))))
                continue;
            if (world.getBlockState(pos.add(p1)).getMaterial().isReplaceable())
            {
                if (BlocksTFC.isSoil(world.getBlockState(pos.add(p1).down(1))))
                    continue;
                if (BlocksTFC.isSoil(world.getBlockState(pos.add(p1).down(2))) && world.getBlockState(pos.add(p1.down(1))).getMaterial().isReplaceable())
                    continue;
            }
            return false;
        }

        return ITreeGenerator.super.canGenerateTree(world, pos, treeType);
    }

    private void placeBranch(TemplateManager manager, World world, BlockPos pos, String name)
    {
        ResourceLocation base = new ResourceLocation(name);
        Template structureBase = manager.get(world.getMinecraftServer(), base);

        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }
        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);

        StructureHelper.addStructureToWorld(world, pos, structureBase, settings);
    }

    private void placeTrunk(World world, BlockPos pos)
    {
        for (BlockPos p1 : trunkPos)
            checkAndPlace(world, pos.add(p1), false);

        placeVine(world, pos.add(1, 0, 0), WEST);
        placeVine(world, pos.add(1, 0, -1), WEST);
        placeVine(world, pos.add(0, 0, 1), NORTH);
        placeVine(world, pos.add(-1, 0, 1), NORTH);
        placeVine(world, pos.add(-2, 0, 0), EAST);
        placeVine(world, pos.add(-2, 0, -1), EAST);
        placeVine(world, pos.add(-1, 0, -2), SOUTH);
        placeVine(world, pos.add(0, 0, -2), SOUTH);
    }

    private void checkAndPlace(World world, BlockPos pos, boolean useBark)
    {
        if (world.getBlockState(pos).getMaterial().isReplaceable() || world.getBlockState(pos).getBlock() instanceof BlockSaplingTFC || world.getBlockState(pos).getBlock() instanceof BlockLeavesTFC)
            world.setBlockState(pos, useBark ? bark : trunk);
    }

    private void placeVine(World world, BlockPos pos, PropertyBool prop)
    {
        if (Math.random() < 0.8f && world.getBlockState(pos).getBlock() == Blocks.AIR)
            world.setBlockState(pos, Blocks.VINE.getDefaultState().withProperty(prop, true));

    }
}
