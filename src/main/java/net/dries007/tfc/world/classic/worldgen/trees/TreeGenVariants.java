/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen.trees;

import java.util.Random;
import java.util.stream.IntStream;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.world.classic.worldgen.WorldGenTrees;

public class TreeGenVariants implements ITreeGenerator
{
    private final String[] variants;
    private final boolean useRotation;

    public TreeGenVariants(boolean useRotation, String... variants)
    {
        this.variants = variants;
        this.useRotation = useRotation;
    }

    public TreeGenVariants(boolean useRotation, int numVariants)
    {
        this(useRotation, IntStream.range(0, numVariants + 1).mapToObj(String::valueOf).toArray(String[]::new));
    }

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Wood tree, Random rand)
    {
        String variant = variants[variants.length == 1 ? 0 : rand.nextInt(variants.length)];
        ResourceLocation base = new ResourceLocation(Constants.MOD_ID, tree + "/" + variant);

        Template structureBase = manager.get(world.getMinecraftServer(), base);
        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }

        PlacementSettings settings = useRotation ? WorldGenTrees.getRandomSettings(rand) : WorldGenTrees.getDefaultSettings();

        if (WorldGenTrees.canGenerateTree(world, pos, structureBase, settings, tree))
        {
            BlockPos size = structureBase.getSize().rotate(settings.getRotation());
            // Begin rotation things
            pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);
            structureBase.addBlocksToWorld(world, pos, settings);
        }
    }
}
