/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import com.mojang.serialization.Codec;

public class OverlayTreeFeature extends TreeFeature<OverlayTreeConfig>
{
    public OverlayTreeFeature(Codec<OverlayTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OverlayTreeConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final OverlayTreeConfig config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final StructureManager manager = TreeHelpers.getStructureManager(level);
        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettings(level, chunkPos, random);
        final StructureTemplate structureBase = manager.getOrCreate(config.base());
        final StructureTemplate structureOverlay = manager.getOrCreate(config.overlay());

        if (TreeHelpers.isValidLocation(level, pos, settings, config.placement()))
        {
            config.trunk().ifPresent(trunk -> {
                final int height = TreeHelpers.placeTrunk(level, mutablePos, random, settings, trunk);
                mutablePos.move(0, height, 0);
            });

            TreeHelpers.placeTemplate(structureBase, settings, level, mutablePos.subtract(TreeHelpers.transformCenter(structureBase.getSize(), settings)));
            settings.addProcessor(new BlockRotProcessor(config.overlayIntegrity()));
            TreeHelpers.placeTemplate(structureOverlay, settings, level, mutablePos.subtract(TreeHelpers.transformCenter(structureOverlay.getSize(), settings)));
            return true;
        }
        return false;
    }
}