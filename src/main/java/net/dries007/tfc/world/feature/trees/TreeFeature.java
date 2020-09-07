/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
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
import net.dries007.tfc.common.TFCTags;

public abstract class TreeFeature<C extends IFeatureConfig> extends Feature<C>
{
    private static final Mirror[] MIRROR_VALUES = Mirror.values();

    public TreeFeature(Function<Dynamic<?>, ? extends C> configFactoryIn)
    {
        super(configFactoryIn);
    }

    protected boolean isValidLocation(IWorld worldIn, BlockPos pos)
    {
        BlockState stateDown = worldIn.getBlockState(pos.down());
        if (!TFCTags.Blocks.GRASS.contains(stateDown.getBlock()))
        {
            return false;
        }

        BlockState stateAt = worldIn.getBlockState(pos);
        return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir(worldIn, pos);
    }

    protected TemplateManager getTemplateManager(IWorld worldIn)
    {
        return ((ServerWorld) worldIn.getWorld()).getSaveHandler().getStructureTemplateManager();
    }

    protected PlacementSettings getRandomPlacementSettings(ChunkPos chunkPos, BlockPos size, Random random)
    {
        // todo: figure out how to handle mirrors
        // Templates correctly rotate the template around the center when transforming each individual block position
        // They do NOT do this for mirrors (for whatever reason)
        // As a result, the center position of the template gets shifted with the mirror.
        // In order to do random mirrors, the center offset needs to be adjusted correctly for each mirror setting, and checked against any possible rotation.
        return getPlacementSettings(chunkPos, size, random).setRotation(Rotation.randomRotation(random));
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
