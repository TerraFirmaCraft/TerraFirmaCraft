/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

import com.mojang.datafixers.Dynamic;

public abstract class TreeFeature<C extends IFeatureConfig> extends Feature<C>
{
    private static final Mirror[] MIRROR_VALUES = Mirror.values();

    public TreeFeature(Function<Dynamic<?>, ? extends C> configFactoryIn)
    {
        super(configFactoryIn);
    }

    protected TemplateManager getTemplateManager(IWorld worldIn)
    {
        return ((ServerWorld) worldIn.getWorld()).getSaveHandler().getStructureTemplateManager();
    }

    protected PlacementSettings getRandomPlacementSettings(ChunkPos chunkPos, BlockPos size, Random random)
    {
        return getPlacementSettings(chunkPos, size, random)
            .setRotation(Rotation.randomRotation(random))
            .setMirror(randomMirror(random));
    }

    protected PlacementSettings getPlacementSettings(ChunkPos chunkPos, BlockPos size, Random random)
    {
        MutableBoundingBox box = new MutableBoundingBox(chunkPos.getXStart() - 16, 0, chunkPos.getZStart() - 16, chunkPos.getXEnd() + 16, 256, chunkPos.getZEnd() + 16);
        return new PlacementSettings()
            .setCenterOffset(new BlockPos(size.getX() / 2, 0, size.getZ() / 2))
            .setBoundingBox(box)
            .setRandom(random)
            .addProcessor(BlockIgnoreStructureProcessor.AIR_AND_STRUCTURE_BLOCK);
    }

    private Mirror randomMirror(Random random)
    {
        return MIRROR_VALUES[random.nextInt(MIRROR_VALUES.length)];
    }
}
