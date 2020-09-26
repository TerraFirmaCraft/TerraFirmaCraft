/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.mixin.world.gen.feature.template.TemplateAccessor;

public abstract class TreeFeature<C extends IFeatureConfig> extends Feature<C>
{
    private static final Mirror[] MIRROR_VALUES = Mirror.values();

    /**
     * A variant of {@link Template#placeInWorld(IServerWorld, BlockPos, PlacementSettings, Random)} that is much simpler and faster for use in tree generation
     * Allows replacing leaves and air blocks
     */
    protected void placeTemplateInWorld(Template template, PlacementSettings placementIn, IWorld worldIn, BlockPos pos)
    {
        List<Template.BlockInfo> transformedBlockInfos = placementIn.getRandomPalette(((TemplateAccessor) template).accessor$getPalettes(), pos).blocks();
        MutableBoundingBox boundingBox = placementIn.getBoundingBox();
        for (Template.BlockInfo blockInfo : Template.processBlockInfos(worldIn, pos, pos, placementIn, transformedBlockInfos, template))
        {
            BlockPos posAt = blockInfo.pos;
            if (boundingBox == null || boundingBox.isInside(posAt))
            {
                BlockState stateAt = worldIn.getBlockState(posAt);
                if (stateAt.isAir(worldIn, posAt) || BlockTags.LEAVES.contains(stateAt.getBlock()))
                {
                    // No world, can't rotate with world context
                    @SuppressWarnings("deprecation")
                    BlockState stateReplace = blockInfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                    worldIn.setBlock(posAt, stateReplace, 2);
                }
            }
        }
    }

    protected int placeTrunk(ISeedReader world, BlockPos pos, BlockPos centerVariation, Random random, TrunkConfig trunk)
    {
        final int height = trunk.getHeight(random);
        final BlockPos center = pos.offset(centerVariation);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = (1 - trunk.width) / 2; x <= trunk.width / 2; x++)
        {
            for (int z = (1 - trunk.width) / 2; z < trunk.width / 2; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    mutablePos.set(center).move(x, y, z);
                    setBlock(world, mutablePos, trunk.state);
                }
            }
        }
        return height;
    }

    protected TreeFeature(Codec<C> codec)
    {
        super(codec);
    }

    protected boolean isValidLocation(IWorld worldIn, BlockPos pos)
    {
        BlockState stateDown = worldIn.getBlockState(pos.below());
        if (!TFCTags.Blocks.TREE_GROWS_ON.contains(stateDown.getBlock()))
        {
            return false;
        }

        BlockState stateAt = worldIn.getBlockState(pos);
        return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir(worldIn, pos);
    }

    protected boolean isAreaClear(IWorld world, BlockPos pos, int radius, int height)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int y = 0; y < height; y++)
        {
            boolean passed = true;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos);
                    mutablePos.move(x, y, z);
                    BlockState stateAt = world.getBlockState(mutablePos);
                    if (!stateAt.isAir(world, mutablePos))
                    {
                        passed = false;
                        break;
                    }
                }
                if (!passed)
                {
                    break;
                }
            }
            if (passed)
            {
                return true;
            }
        }
        return false;
    }

    protected TemplateManager getTemplateManager(ISeedReader worldIn)
    {
        return worldIn.getLevel().getServer().getStructureManager();
    }

    protected PlacementSettings getRandomPlacementSettings(ChunkPos chunkPos, Random random)
    {
        return getPlacementSettings(chunkPos, random).setRotation(Rotation.getRandom(random)).setMirror(randomMirror(random));
    }

    protected PlacementSettings getPlacementSettings(ChunkPos chunkPos, Random random)
    {
        MutableBoundingBox box = new MutableBoundingBox(chunkPos.getMinBlockX() - 16, 0, chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, 256, chunkPos.getMaxBlockZ() + 16);
        return new PlacementSettings()
            .setBoundingBox(box)
            .setRandom(random)
            .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
    }

    /**
     * Returns a possible variation value for even parity templates. This allows the "center" to be randomly chosen if it lies on an exact block boundary
     * This is separate from {@link TreeFeature#getCenteredOffset(BlockPos, BlockPos, PlacementSettings)} as in the case where multiple structures are being placed on the same center, the variation must be constant for each structure
     */
    protected BlockPos getCenterVariation(BlockPos size, Random random)
    {
        return new BlockPos(size.getX() % 2 == 0 && random.nextBoolean() ? -1 : 0, 0, size.getZ() % 2 == 0 && random.nextBoolean() ? -1 : 0);
    }

    /**
     * Gets the offset for a template such that under the transformation applied by settings, the origin is set to the center of the template.
     */
    protected BlockPos getCenteredOffset(BlockPos size, PlacementSettings settings, Random random)
    {
        return getCenteredOffset(size, getCenterVariation(size, random), settings);
    }

    /**
     * Gets the offset for a template such that under the transformation applied by settings, the origin is set to the center of the template.
     * This should be subtracted from the desired position when called to generate.
     */
    protected BlockPos getCenteredOffset(BlockPos size, BlockPos centerVariation, PlacementSettings settings)
    {
        final BlockPos center = new BlockPos(size.getX() / 2 + centerVariation.getX(), 0, size.getZ() / 2 + centerVariation.getZ());
        return Template.transform(center, settings.getMirror(), settings.getRotation(), settings.getRotationPivot());
    }

    private Mirror randomMirror(Random random)
    {
        return MIRROR_VALUES[random.nextInt(MIRROR_VALUES.length)];
    }
}