/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import com.mojang.serialization.Codec;

public class OverlayTreeFeature extends TreeFeature<OverlayTreeConfig>
{
    public OverlayTreeFeature(Codec<OverlayTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos pos, OverlayTreeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(pos);
        final TemplateManager manager = TreeHelpers.getTemplateManager(worldIn);
        final PlacementSettings settings = TreeHelpers.getPlacementSettings(chunkPos, random);
        final Template structureBase = manager.getOrCreate(config.base);
        final Template structureOverlay = manager.getOrCreate(config.overlay);

        if (!isValidLocation(worldIn, mutablePos) || !isAreaClear(worldIn, mutablePos, config.radius, 3))
        {
            return false;
        }

        config.trunk.ifPresent(trunk -> {
            final int height = TreeHelpers.placeTrunk(worldIn, mutablePos, random, settings, trunk);
            mutablePos.move(0, height, 0);
        });

        TreeHelpers.placeTemplate(structureBase, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structureBase.getSize(), settings)));
        settings.addProcessor(new IntegrityProcessor(config.overlayIntegrity));
        TreeHelpers.placeTemplate(structureOverlay, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structureOverlay.getSize(), settings)));
        return true;
    }
}