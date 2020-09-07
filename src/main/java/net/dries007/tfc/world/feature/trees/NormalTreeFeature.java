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

public class NormalTreeFeature extends TreeFeature<NormalTreeConfig>
{
    public NormalTreeFeature()
    {
        super(NormalTreeConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NormalTreeConfig config)
    {
        if (!isValidLocation(worldIn, pos))
        {
            return false;
        }

        final ChunkPos chunkPos = new ChunkPos(pos);
        final TemplateManager manager = getTemplateManager(worldIn);
        final Template structureBase = manager.getTemplateDefaulted(config.getBase());
        final Template structureOverlay = manager.getTemplateDefaulted(config.getOverlay());
        final int height = config.getHeightMin() + (config.getHeightRange() > 0 ? rand.nextInt(config.getHeightRange()) : 0);

        final BlockPos baseStructurePos = pos.add(-structureBase.getSize().getX() / 2, height, -structureBase.getSize().getZ() / 2);
        final BlockPos overlayStructurePos = pos.add(-structureOverlay.getSize().getX() / 2, height, -structureOverlay.getSize().getZ() / 2);

        final PlacementSettings settings = getPlacementSettings(chunkPos, structureBase.getSize(), rand);

        structureBase.addBlocksToWorld(worldIn, baseStructurePos, settings);
        settings.addProcessor(new IntegrityProcessor(0.5f))
            .setCenterOffset(new BlockPos(structureOverlay.getSize().getX() / 2, 0, structureOverlay.getSize().getZ() / 2));
        structureOverlay.addBlocksToWorld(worldIn, overlayStructurePos, settings);

        final BlockState log = config.getTrunkState();
        for (int i = 0; i < height; i++)
        {
            worldIn.setBlockState(pos.add(0, i, 0), log, 3);
        }
        return true;
    }
}
