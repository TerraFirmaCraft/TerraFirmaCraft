/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.block.BlockState;
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
        if (!isValidLocation(worldIn, pos) || !isAreaClear(worldIn, pos, config.radius, 3))
        {
            return false;
        }

        final TemplateManager manager = getTemplateManager(worldIn);
        final Template structureBase = manager.getOrCreate(config.base);
        final Template structureOverlay = manager.getOrCreate(config.overlay);
        final BlockPos centerVariation = getCenterVariation(structureBase.getSize(), random);
        final PlacementSettings settings = getPlacementSettings(new ChunkPos(pos), random);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().set(pos);

        config.trunk.ifPresent(trunk -> {
            final int height = placeTrunk(worldIn, pos, centerVariation, random, trunk);
            mutablePos.move(0, height, 0);
        });

        placeTemplateInWorld(structureBase, settings, worldIn, mutablePos.subtract(getCenteredOffset(structureBase.getSize(), centerVariation, settings)));
        settings.addProcessor(new IntegrityProcessor(config.overlayIntegrity));
        placeTemplateInWorld(structureOverlay, settings, worldIn, mutablePos.subtract(getCenteredOffset(structureOverlay.getSize(), centerVariation, settings)));
        return true;
    }
}