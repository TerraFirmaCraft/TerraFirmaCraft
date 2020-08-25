/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.*;
import net.minecraft.world.server.ServerWorld;

public class NormalTreeFeature extends Feature<NormalTreeConfig>
{
    public NormalTreeFeature()
    {
        super(NormalTreeConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NormalTreeConfig config)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final TemplateManager manager = ((ServerWorld) worldIn.getWorld()).getSaveHandler().getStructureTemplateManager();
        final Template structureBase = manager.getTemplateDefaulted(config.getBase());
        final Template structureOverlay = manager.getTemplateDefaulted(config.getOverlay());
        final int height = config.getHeightMin() + (config.getHeightRange() > 0 ? rand.nextInt(config.getHeightRange()) : 0);

        final BlockPos size = structureBase.getSize();
        pos = pos.add(-size.getX() / 2, height, -size.getZ() / 2);

        MutableBoundingBox box = new MutableBoundingBox(chunkPos.getXStart(), 0, chunkPos.getZStart(), chunkPos.getXEnd(), 256, chunkPos.getZEnd());
        PlacementSettings settings = new PlacementSettings()
            .setRotation(Rotation.randomRotation(rand))
            .setBoundingBox(box)
            .setRandom(rand)
            .addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);

        structureBase.addBlocksToWorld(worldIn, pos, settings);
        settings.addProcessor(new IntegrityProcessor(0.5f));
        structureOverlay.addBlocksToWorld(worldIn, pos, settings);

        final BlockState log = config.getTrunkState();
        for (int i = 0; i < height; i++)
        {
            worldIn.setBlockState(pos.add(size.getX() / 2, i - height, size.getZ() / 2), log, 3);
        }
        return true;
    }
}
