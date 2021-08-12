/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Random;

import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

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
    public static void placeTemplate(StructureTemplate template, StructurePlaceSettings placementIn, LevelAccessor worldIn, BlockPos pos)
    {
        // todo: mixin / accessor
        List<StructureTemplate.StructureBlockInfo> transformedBlockInfos = placementIn.getRandomPalette(template.palettes, pos).blocks();
        BoundingBox boundingBox = placementIn.getBoundingBox();
        for (StructureTemplate.StructureBlockInfo blockInfo : StructureTemplate.processBlockInfos(worldIn, pos, pos, placementIn, transformedBlockInfos, template))
        {
            BlockPos posAt = blockInfo.pos;
            if (boundingBox == null || boundingBox.isInside(posAt))
            {
                BlockState stateAt = worldIn.getBlockState(posAt);
                if (stateAt.isAir() || BlockTags.LEAVES.contains(stateAt.getBlock()))
                {
                    // No world, can't rotate with world context
                    @SuppressWarnings("deprecation")
                    BlockState stateReplace = blockInfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                    worldIn.setBlock(posAt, stateReplace, 2);
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
    public static int placeTrunk(WorldGenLevel world, BlockPos pos, Random random, StructurePlaceSettings settings, TrunkConfig trunk)
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
                    world.setBlock(mutablePos, trunk.state(), 3);
                }
            }
        }
        return height;
    }

    public static StructureManager getTemplateManager(WorldGenLevel worldIn)
    {
        return worldIn.getLevel().getServer().getStructureManager();
    }

    /**
     * Constructs a placement settings instance useful for tree generation
     * Applies a random rotation and mirror
     * Has a bounding box constrained by the given chunk and surrounding chunks to not cause cascading chunk loading
     */
    public static StructurePlaceSettings getPlacementSettings(ChunkPos chunkPos, Random random)
    {
        return new StructurePlaceSettings()
            .setBoundingBox(new BoundingBox(chunkPos.getMinBlockX() - 16, 0, chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, 256, chunkPos.getMaxBlockZ() + 16))
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
