/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.api.util.IFruitTreeGenerator;
import net.dries007.tfc.world.classic.StructureHelper;

public class FruitTreeGen implements IFruitTreeGenerator
{
    private static final PlacementSettings SETTINGS = StructureHelper.getDefaultSettings();

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, IFruitTree tree, Random rand)
    {
        ResourceLocation base = new ResourceLocation("tfc:fruit_trees/" + tree.getName());
        Template structureBase = manager.get(world.getMinecraftServer(), base);

        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }

        BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);

        StructureHelper.addStructureToWorld(world, pos, structureBase, SETTINGS);
    }
}
