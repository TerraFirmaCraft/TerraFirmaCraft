/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import net.dries007.tfc.util.Helpers;

public class OverlayTreeFeature extends TreeFeature<OverlayTreeConfig>
{
    public OverlayTreeFeature()
    {
        super(OverlayTreeConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, OverlayTreeConfig config)
    {
        if (!isValidLocation(worldIn, pos) || !isAreaClear(worldIn, pos, config.radius, 3))
        {
            return false;
        }

        final ChunkPos chunkPos = new ChunkPos(pos);
        final TemplateManager manager = getTemplateManager(worldIn);
        final Template structureBase = manager.getOrCreate(config.base);
        final Template structureOverlay = manager.getOrCreate(config.overlay);
        final int height = config.heightMin + (config.heightRange > 0 ? rand.nextInt(config.heightRange) : 0);

        final BlockPos baseStructurePos = pos.offset(-structureBase.getSize().getX() / 2, height, -structureBase.getSize().getZ() / 2);
        final BlockPos overlayStructurePos = pos.offset(-structureOverlay.getSize().getX() / 2, height, -structureOverlay.getSize().getZ() / 2);

        final PlacementSettings settings = getPlacementSettings(chunkPos, structureBase.getSize(), rand);

        Helpers.addTemplateToWorldForTreeGen(structureBase, settings, worldIn, baseStructurePos);
        settings.addProcessor(new IntegrityProcessor(0.5f))
            .setRotationPivot(new BlockPos(structureOverlay.getSize().getX() / 2, 0, structureOverlay.getSize().getZ() / 2));
        Helpers.addTemplateToWorldForTreeGen(structureOverlay, settings, worldIn, overlayStructurePos);

        final BlockState log = config.trunkState;
        for (int i = 0; i < height; i++)
        {
            worldIn.setBlock(pos.offset(0, i, 0), log, 2);
        }
        return true;
    }
}