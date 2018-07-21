/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
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
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;

import static net.dries007.tfc.objects.blocks.wood.BlockLogTFC.PLACED;

// todo: sequoia leaves are decaying because the tree is too big >:( Do something about this.
public class TreeGenSequoia implements ITreeGenerator
{
    private final PlacementSettings settings = ITreeGenerator.getDefaultSettings();
    private IBlockState trunk;

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand)
    {
        //todo: better generation checks
        if (!canGenerateTree(world, pos, tree))
            return;

        final int baseVariant = 1 + rand.nextInt(3);
        final int topVariant = 1 + rand.nextInt(3);
        final int layers = 4 + rand.nextInt(3);
        final int height = 3 + rand.nextInt(4);

        trunk = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false);

        for (int i = 0; i < height; i++)
            placeTrunk(world, pos.add(0, i, 0));

        int k = height;
        for (int j = 0; j < layers; j++)
        {
            if (j == layers - 1 || (j == layers - 2 && rand.nextBoolean()))
                k += placeLayer(manager, world, pos.up(k), tree.name + "/mid" + baseVariant);
            else
                k += placeLayer(manager, world, pos.up(k), tree.name + "/base" + baseVariant);
        }
        placeLayer(manager, world, pos.up(k), tree.name + "/top" + topVariant);

    }

    private int placeLayer(TemplateManager manager, World world, BlockPos pos, String name)
    {
        ResourceLocation base = new ResourceLocation(Constants.MOD_ID, name);
        Template structureBase = manager.get(world.getMinecraftServer(), base);

        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return -1;
        }
        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);

        structureBase.addBlocksToWorld(world, pos, settings);
        return size.getY();
    }

    private void placeTrunk(World world, BlockPos pos)
    {
        world.setBlockState(pos, trunk);
        world.setBlockState(pos.add(-1, 0, 0), trunk);
        world.setBlockState(pos.add(0, 0, -1), trunk);
        world.setBlockState(pos.add(-1, 0, -1), trunk);

    }

}
