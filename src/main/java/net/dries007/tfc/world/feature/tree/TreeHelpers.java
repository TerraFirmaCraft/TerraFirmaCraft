/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import net.dries007.tfc.mixin.accessor.StructureTemplateAccessor;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;

/**
 * Helpers class for working with tree generation
 * Includes utilities for managing rotations, mirrors, and templates
 */
public final class TreeHelpers
{
    private static final Rotation[] ROTATION_VALUES = Rotation.values();
    private static final Mirror[] MIRROR_VALUES = Mirror.values();

    /**
     * A variant of {@link StructureTemplate#placeInWorld(ServerLevelAccessor, BlockPos, BlockPos, StructurePlaceSettings, Random, int)} that is much simpler and faster for use in tree generation
     * Allows replacing leaves and air blocks
     */
    public static void placeTemplate(StructureTemplate template, StructurePlaceSettings placementIn, LevelAccessor level, BlockPos pos)
    {
        final List<StructureTemplate.StructureBlockInfo> transformedBlockInfos = placementIn.getRandomPalette(((StructureTemplateAccessor) template).accessor$getPalettes(), pos).blocks();
        BoundingBox boundingBox = placementIn.getBoundingBox();
        for (StructureTemplate.StructureBlockInfo blockInfo : StructureTemplate.processBlockInfos(level, pos, pos, placementIn, transformedBlockInfos, template))
        {
            BlockPos posAt = blockInfo.pos;
            if (boundingBox == null || boundingBox.isInside(posAt))
            {
                BlockState stateAt = level.getBlockState(posAt);
                if (EnvironmentHelpers.isWorldgenReplaceable(stateAt) || Helpers.isBlock(stateAt.getBlock(), BlockTags.LEAVES))
                {
                    // No world, can't rotate with world context
                    @SuppressWarnings("deprecation")
                    BlockState stateReplace = blockInfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                    level.setBlock(posAt, stateReplace, 2);
                }
            }
        }
    }

    /**
     * Place a trunk from a trunk config
     *
     * @param pos The center position of the trunk
     * @return The height of the trunk placed
     */
    public static int placeTrunk(WorldGenLevel level, BlockPos pos, Random random, StructurePlaceSettings settings, TrunkConfig trunk)
    {
        final int height = trunk.getHeight(random);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = (1 - trunk.width()) / 2; x <= trunk.width() / 2; x++)
        {
            for (int z = (1 - trunk.width()) / 2; z <= trunk.width() / 2; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    mutablePos.set(x, y, z);
                    transformMutable(mutablePos, settings.getMirror(), settings.getRotation());
                    mutablePos.move(pos);
                    level.setBlock(mutablePos, trunk.state(), 3);
                }
            }
        }
        return height;
    }

    public static StructureManager getStructureManager(WorldGenLevel level)
    {
        return level.getLevel().getServer().getStructureManager();
    }

    /**
     * Constructs a placement settings instance useful for tree generation
     * Applies a random rotation and mirror
     * Has a bounding box constrained by the given chunk and surrounding chunks to not cause cascading chunk loading
     */
    public static StructurePlaceSettings getPlacementSettings(LevelHeightAccessor level, ChunkPos chunkPos, Random random)
    {
        return new StructurePlaceSettings()
            .setBoundingBox(new BoundingBox(chunkPos.getMinBlockX() - 16, level.getMinBuildHeight(), chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, level.getMaxBuildHeight(), chunkPos.getMaxBlockZ() + 16))
            .setRandom(random)
            .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
            .setRotation(randomRotation(random))
            .setMirror(randomMirror(random));
    }

    /**
     * Given a width of a specific parity, return the transformation of the chosen center position.
     */
    public static BlockPos transformCenter(Vec3i size, StructurePlaceSettings settings)
    {
        return transform(new BlockPos((size.getX() - 1) / 2, 0, (size.getZ() - 1) / 2), settings.getMirror(), settings.getRotation());
    }

    /**
     * {@link StructureTemplate#transform(BlockPos, Mirror, Rotation, BlockPos)} but simplified
     */
    public static BlockPos transform(BlockPos pos, Mirror mirrorIn, Rotation rotationIn)
    {
        int posX = pos.getX();
        int posZ = pos.getZ();
        boolean mirror = true;
        switch (mirrorIn)
        {
            case LEFT_RIGHT -> posZ = -posZ;
            case FRONT_BACK -> posX = -posX;
            default -> mirror = false;
        }
        return switch (rotationIn)
            {
                case COUNTERCLOCKWISE_90 -> new BlockPos(posZ, pos.getY(), -posX);
                case CLOCKWISE_90 -> new BlockPos(-posZ, pos.getY(), posX);
                case CLOCKWISE_180 -> new BlockPos(-posX, pos.getY(), -posZ);
                default -> mirror ? new BlockPos(posX, pos.getY(), posZ) : pos;
            };
    }

    /**
     * {@link StructureTemplate#transform(BlockPos, Mirror, Rotation, BlockPos)} but simplified, and works with mutable positions
     */
    public static void transformMutable(BlockPos.MutableBlockPos pos, Mirror mirrorIn, Rotation rotationIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT -> pos.setZ(-pos.getZ());
            case FRONT_BACK -> pos.setX(-pos.getX());
        }
        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90 -> pos.set(pos.getZ(), pos.getY(), -pos.getX());
            case CLOCKWISE_90 -> pos.set(-pos.getZ(), pos.getY(), pos.getX());
            case CLOCKWISE_180 -> pos.set(-pos.getX(), pos.getY(), -pos.getZ());
        }
    }

    private static Rotation randomRotation(Random random)
    {
        return ROTATION_VALUES[random.nextInt(ROTATION_VALUES.length)];
    }

    private static Mirror randomMirror(Random random)
    {
        return MIRROR_VALUES[random.nextInt(MIRROR_VALUES.length)];
    }
}
