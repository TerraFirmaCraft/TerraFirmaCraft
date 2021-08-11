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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import com.mojang.serialization.Codec;

public class OverlayTreeFeature extends TreeFeature<OverlayTreeConfig>
{
    public OverlayTreeFeature(Codec<OverlayTreeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator generator, Random random, BlockPos pos, OverlayTreeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final StructureManager manager = TreeHelpers.getTemplateManager(worldIn);
        final StructurePlaceSettings settings = TreeHelpers.getPlacementSettings(chunkPos, random);
        final StructureTemplate structureBase = manager.getOrCreate(config.base);
        final StructureTemplate structureOverlay = manager.getOrCreate(config.overlay);

        if (!isValidLocation(worldIn, mutablePos) || !isAreaClear(worldIn, mutablePos, config.radius, 3))
        {
            return false;
        }

        config.trunk.ifPresent(trunk -> {
            final int height = TreeHelpers.placeTrunk(worldIn, mutablePos, random, settings, trunk);
            mutablePos.move(0, height, 0);
        });

        TreeHelpers.placeTemplate(structureBase, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structureBase.getSize(), settings)));
        settings.addProcessor(new BlockRotProcessor(config.overlayIntegrity));
        TreeHelpers.placeTemplate(structureOverlay, settings, worldIn, mutablePos.subtract(TreeHelpers.transformCenter(structureOverlay.getSize(), settings)));
        return true;
    }
}