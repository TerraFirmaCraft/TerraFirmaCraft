/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
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

import static net.dries007.tfc.objects.blocks.wood.BlockLogTFC.PLACED;

public class TreeGenAcacia implements ITreeGenerator
{
    private PlacementSettings settings;
    private IBlockState trunk;

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand)
    {
        settings = ITreeGenerator.getDefaultSettings();
        trunk = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false);
        final boolean smallBranch = rand.nextBoolean();
        final int branches = 2 + rand.nextInt(2);
        final int height = 5 + rand.nextInt(4);
        List<EnumFacing> sides = Arrays.stream(EnumFacing.HORIZONTALS).collect(Collectors.toList());
        EnumFacing face;

        int x1, y1 = 0, y2 = 0, y3 = 0;
        EnumFacing side = EnumFacing.UP;
        if (smallBranch)
        {
            y3 = rand.nextInt(3) + 2;
            side = sides.get(rand.nextInt(sides.size()));
            placeBranch(manager, world, pos.offset(side).add(0, y3, 0), tree.name + "/branch3");
        }
        for (int i = 0; i < branches; i++)
        {
            x1 = 2 + rand.nextInt(3);
            y1 = 4 + rand.nextInt(height - 2);
            if (y1 > y2)
                y2 = y1;
            face = sides.remove(rand.nextInt(sides.size()));
            for (int j = 1; j < x1; j++)
                placeLog(world, pos.add(0, y1 - j, 0).offset(face, x1 - j));
            int branch = 1 + rand.nextInt(2);
            placeBranch(manager, world, pos.add(0, y1, 0).offset(face, x1), tree.name + "/branch" + branch);
        }
        for (int i = 0; i < height; i++)
        {
            if (smallBranch && i == y3)
                pos = pos.offset(side.getOpposite());
            placeLog(world, pos.add(0, i, 0));
        }
        placeBranch(manager, world, pos.add(0, height, 0), tree.name + "/branch3");
    }

    private void placeBranch(TemplateManager manager, World world, BlockPos pos, String name)
    {
        ResourceLocation base = new ResourceLocation(Constants.MOD_ID, name);
        Template structureBase = manager.get(world.getMinecraftServer(), base);

        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }
        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);

        structureBase.addBlocksToWorld(world, pos, settings);
    }

    private void placeLog(World world, BlockPos pos)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.AIR || block instanceof BlockLeavesTFC)
            world.setBlockState(pos, trunk);
    }
}
