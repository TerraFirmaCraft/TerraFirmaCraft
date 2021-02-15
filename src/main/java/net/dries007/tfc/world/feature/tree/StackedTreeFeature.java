/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

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

public class StackedTreeFeature extends TreeFeature<StackedTreeConfig>
{
    public StackedTreeFeature(Codec<StackedTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator chunkGenerator, Random random, BlockPos pos, StackedTreeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().setPos(pos);
        final TemplateManager manager = TreeHelpers.getTemplateManager(worldIn);
        final PlacementSettings settings = TreeHelpers.getPlacementSettings(chunkPos, random);

        if (!isValidLocation(worldIn, mutablePos) || !isAreaClear(worldIn, mutablePos, config.radius, 2))
        {
            return false;
        }

        // Trunk first
        int trunkHeight = TreeHelpers.placeTrunk(worldIn, mutablePos, random, settings, config.trunk);
        mutablePos.move(0, trunkHeight, 0);

        for (StackedTreeConfig.Layer layer : config.layers)
        {
            // Place each layer
            int layerCount = layer.getCount(random);
            for (int i = 0; i < layerCount; i++)
            {
                final ResourceLocation structureId = layer.templates.get(random.nextInt(layer.templates.size()));
                final Template structure = manager.getTemplate(structureId);
                TreeHelpers.placeTemplate(structure, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structure.getSize(), settings)));
                mutablePos.move(0, structure.getSize().getY(), 0);
            }
        }
        return true;
    }
}
