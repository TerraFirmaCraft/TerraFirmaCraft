/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import com.mojang.serialization.Codec;

public class RandomTreeFeature extends TreeFeature<RandomTreeConfig>
{
    public RandomTreeFeature(Codec<RandomTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos pos, RandomTreeConfig config)
    {
        if (!isValidLocation(worldIn, pos) || !isAreaClear(worldIn, pos, config.radius, 2))
        {
            return false;
        }

        final TemplateManager manager = getTemplateManager(worldIn);
        final ResourceLocation structureId = config.structureNames.get(random.nextInt(config.structureNames.size()));
        final Template structure = manager.getOrCreate(structureId);
        final BlockPos centerVariation = getCenterVariation(structure.getSize(), random);
        final PlacementSettings settings = getRandomPlacementSettings(new ChunkPos(pos), random);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(pos);

        config.trunk.ifPresent(trunk -> {
            final int height = placeTrunk(worldIn, pos, centerVariation, random, trunk);
            mutablePos.move(0, height, 0);
        });

        placeTemplateInWorld(structure, settings, worldIn, mutablePos.subtract(getCenteredOffset(structure.getSize(), centerVariation, settings)));
        return true;
    }
}