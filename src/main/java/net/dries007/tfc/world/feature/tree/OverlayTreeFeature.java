/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class OverlayTreeFeature extends Feature<OverlayTreeConfig>
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
        final var random = context.random();
        final OverlayTreeConfig config = context.config();

        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final StructureTemplateManager manager = TreeHelpers.getStructureManager(level);
        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettings(level, chunkPos, random);
        final StructureTemplate structureBase = manager.getOrCreate(config.base());
        final StructureTemplate structureOverlay = manager.getOrCreate(config.overlay());

        if (TreeHelpers.isValidLocation(level, pos, settings, config.placement()))
        {
            final boolean placeTree = config.rootSystem().map(roots -> TreeHelpers.placeRoots(level, pos.below(), roots, random) || !roots.required()).orElse(true);
            if (placeTree)
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
        return false;
    }
}