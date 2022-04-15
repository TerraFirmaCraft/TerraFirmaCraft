/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import com.mojang.serialization.Codec;

public class StackedTreeFeature extends TreeFeature<StackedTreeConfig>
{
    public StackedTreeFeature(Codec<StackedTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<StackedTreeConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final StackedTreeConfig config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final StructureManager manager = TreeHelpers.getStructureManager(level);
        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettings(level, chunkPos, random);

        if (TreeHelpers.isValidGround(level, pos, settings, config.placement()))
        {
            // Trunk first
            int trunkHeight = TreeHelpers.placeTrunk(level, mutablePos, random, settings, config.trunk());
            mutablePos.move(0, trunkHeight, 0);

            for (StackedTreeConfig.Layer layer : config.layers())
            {
                // Place each layer
                int layerCount = layer.getCount(random);
                for (int i = 0; i < layerCount; i++)
                {
                    final ResourceLocation structureId = layer.templates().get(random.nextInt(layer.templates().size()));
                    final StructureTemplate structure = manager.getOrCreate(structureId);
                    TreeHelpers.placeTemplate(structure, settings, level, mutablePos.subtract(TreeHelpers.transformCenter(structure.getSize(), settings)));
                    mutablePos.move(0, structure.getSize().getY(), 0);
                }
            }
            return true;
        }
        return false;
    }
}
