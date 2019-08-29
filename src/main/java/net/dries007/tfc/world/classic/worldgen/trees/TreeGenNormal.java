/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.ITreeGenerator;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.world.classic.StructureHelper;

import static net.dries007.tfc.objects.blocks.wood.BlockLogTFC.PLACED;

public class TreeGenNormal implements ITreeGenerator
{
    private static final PlacementSettings settingsFull = StructureHelper.getDefaultSettings();
    private static final PlacementSettings settingsWeak = StructureHelper.getDefaultSettings().setIntegrity(0.5f);
    private final int heightMin;
    private final int heightRange;

    /**
     * A basic tree generator. It will generate a structure found in /assets/tfc/[TREE NAME]/base.nbt
     * Additionally, it will try and apply an overlay with the name /overlay.nbt at 50% integrity.
     *
     * @param heightMin   The minimum amount of logs to add to the bottom of the trunk Set to 0 for no extra height
     * @param heightRange The maximum amount of logs to add to the bottom of the trunk. Set to 0 for no extra height
     */
    public TreeGenNormal(int heightMin, int heightRange)
    {
        this.heightMin = heightMin;
        this.heightRange = heightRange;
    }

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand, boolean isWorldGen)
    {
        ResourceLocation base = new ResourceLocation(tree.getRegistryName() + "/base");
        ResourceLocation overlay = new ResourceLocation(tree.getRegistryName() + "/overlay");

        Template structureBase = manager.get(world.getMinecraftServer(), base);
        Template structureOverlay = manager.get(world.getMinecraftServer(), overlay);

        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }

        int height = heightMin + (heightRange > 0 ? rand.nextInt(heightRange) : 0);

        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, height, -size.getZ() / 2);

        StructureHelper.addStructureToWorld(world, pos, structureBase, settingsFull);
        if (structureOverlay != null)
        {
            StructureHelper.addStructureToWorld(world, pos, structureOverlay, settingsWeak);
        }

        final IBlockState log = BlockLogTFC.get(tree).getDefaultState().withProperty(PLACED, false);
        for (int i = 0; i < height; i++)
            world.setBlockState(pos.add(size.getX() / 2, i - height, size.getZ() / 2), log);
    }

}
