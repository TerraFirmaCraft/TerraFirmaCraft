/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
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

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.ITreeGenerator;
import net.dries007.tfc.world.classic.StructureHelper;

public class TreeGenVariants implements ITreeGenerator
{
    private static final PlacementSettings settings = StructureHelper.getDefaultSettings();
    private final String[] variants;
    private final boolean useRotation;

    /**
     * A tree generator which select a random structure to place. Can choose to use a random rotation as well
     *
     * @param useRotation Should it try and randomly rotate the structures on placement
     * @param variants    The list of variants for the generator to look for. Structure files should be placed in
     *                    assets/tfc/[TREE NAME]/ This needs to be the list of file names, (i.e. "tree1.nbt" should pass in "tree1")
     */
    public TreeGenVariants(boolean useRotation, String... variants)
    {
        this.variants = variants;
        this.useRotation = useRotation;
    }

    /**
     * Alternate constructor which will auto populate the list of variants
     *
     * @param useRotation Should it try and randomly rotate the structures on placement
     * @param numVariants The number of variant files. Files need to be named 1.nbt, 2.nbt, 3.nbt ...
     */
    public TreeGenVariants(boolean useRotation, int numVariants)
    {
        this(useRotation, IntStream.range(1, numVariants + 1).mapToObj(String::valueOf).toArray(String[]::new));
    }

    @Override
    public void generateTree(TemplateManager manager, World world, BlockPos pos, Tree tree, Random rand, boolean isWorldGen)
    {
        String variant = variants[variants.length == 1 ? 0 : rand.nextInt(variants.length)];
        ResourceLocation base = new ResourceLocation(tree.getRegistryName() + "/" + variant);

        Template structureBase = manager.get(world.getMinecraftServer(), base);
        if (structureBase == null)
        {
            TerraFirmaCraft.getLog().warn("Unable to find a template for " + base.toString());
            return;
        }

        PlacementSettings settings2 = useRotation ? StructureHelper.getRandomSettings(rand) : settings;

        BlockPos size = structureBase.getSize().rotate(settings2.getRotation());
        // Begin rotation things
        pos = pos.add(-size.getX() / 2, 0, -size.getZ() / 2);
        StructureHelper.addStructureToWorld(world, pos, structureBase, settings2);
    }
}
