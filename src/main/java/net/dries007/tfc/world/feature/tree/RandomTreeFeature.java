/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import com.mojang.serialization.Codec;

public class RandomTreeFeature extends TreeFeature<RandomTreeConfig>
{
    public RandomTreeFeature(Codec<RandomTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator generator, Random random, BlockPos pos, RandomTreeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final StructureManager manager = TreeHelpers.getTemplateManager(worldIn);
        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettings(chunkPos, random);
        final ResourceLocation structureId = config.structureNames.get(random.nextInt(config.structureNames.size()));
        final StructureTemplate structure = manager.getOrCreate(structureId);

        if (!isValidLocation(worldIn, mutablePos) || !isAreaClear(worldIn, mutablePos, config.radius, 2))
        {
            return false;
        }

        config.trunk.ifPresent(trunk -> {
            final int height = TreeHelpers.placeTrunk(worldIn, mutablePos, random, settings, trunk);
            mutablePos.move(0, height, 0);
        });

        TreeHelpers.placeTemplate(structure, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structure.getSize(), settings)));
        return true;
    }
}