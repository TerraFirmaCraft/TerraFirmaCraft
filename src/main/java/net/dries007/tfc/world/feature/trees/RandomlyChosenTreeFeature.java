/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import net.dries007.tfc.util.Helpers;

public class RandomlyChosenTreeFeature extends TreeFeature<RandomlyChosenTreeConfig>
{
    public RandomlyChosenTreeFeature()
    {
        super(RandomlyChosenTreeConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, RandomlyChosenTreeConfig config)
    {
        if (!isValidLocation(worldIn, pos))
        {
            return false;
        }

        final ChunkPos chunkPos = new ChunkPos(pos);
        final TemplateManager manager = getTemplateManager(worldIn);
        final List<ResourceLocation> structureIds = config.getStructureNames();
        final ResourceLocation structureId = structureIds.get(rand.nextInt(structureIds.size()));
        final Template structure = manager.getTemplateDefaulted(structureId);

        final BlockPos size = structure.getSize();
        final BlockPos offset = new BlockPos(-size.getX() / 2, 0, -size.getZ() / 2);
        final BlockPos structurePos = pos.add(offset);

        final PlacementSettings settings = getRandomPlacementSettings(chunkPos, size, rand);
        Helpers.addTemplateToWorldForTreeGen(structure, settings, worldIn, structurePos);

        return true;
    }
}
