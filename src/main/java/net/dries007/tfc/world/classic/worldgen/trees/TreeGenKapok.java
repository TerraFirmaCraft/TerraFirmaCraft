/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;
import javax.annotation.Nullable;

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

public class TreeGenKapok implements ITreeGenerator
{
    private IBlockState trunk;
    private PlacementSettings settings;

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand)
    {
        if (!canGenerateTree(world, pos, tree))
            return;
        // todo

        trunk = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false);
        settings = ITreeGenerator.getDefaultSettings();

        int height = 12 + rand.nextInt(8);
        int branches = 2 + rand.nextInt(3);

        int x1, y1, z1, type;
        for (int i = 0; i < branches; i++)
        {
            y1 = 6 + rand.nextInt(height - 8);
            x1 = rand.nextInt(3);
            z1 = rand.nextInt(3);
            if (x1 + z1 == 0)
                x1 += 1 + rand.nextInt(2);
            if (rand.nextBoolean())
                x1 = -x1 - 1;
            if (rand.nextBoolean())
                x1 = -z1 - 1;
            type = 1 + rand.nextInt(3);
            placeBranch(manager, world, pos.add(x1, y1, z1), tree.name + "/branch" + type);
            checkAndPlace(world, pos.add(x1 - Math.abs(x1) / x1, y1 - 1, z1 - Math.abs(z1) / z1), rand, null);
        }

        for (int i = 0; i < height; i++)
            placeTrunk(world, pos.add(0, i, 0), rand);
        placeBranch(manager, world, pos.add(0, height, 0), tree.name + "/top");
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

    private void placeTrunk(World world, BlockPos pos, Random rand)
    {
        checkAndPlace(world, pos, rand, EnumFacing.SOUTH);
        checkAndPlace(world, pos.add(-1, 0, 0), rand, EnumFacing.EAST);
        checkAndPlace(world, pos.add(0, 0, -1), rand, EnumFacing.WEST);
        checkAndPlace(world, pos.add(-1, 0, -1), rand, EnumFacing.NORTH);
    }

    private void checkAndPlace(World world, BlockPos pos, Random rand, @Nullable EnumFacing vineFace)
    {
        if (world.getBlockState(pos).getBlock() == Blocks.AIR || world.getBlockState(pos).getBlock() instanceof BlockLeavesTFC)
            world.setBlockState(pos, trunk);
        // Random vines
        if (vineFace != null && rand.nextFloat() < 0.6 && world.getBlockState(pos.offset(vineFace)) == Blocks.AIR.getDefaultState())
            world.setBlockState(pos.offset(vineFace), Blocks.VINE.getDefaultState());
    }
}
