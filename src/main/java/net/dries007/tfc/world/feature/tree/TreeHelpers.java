/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import net.dries007.tfc.mixin.world.gen.feature.template.TemplateAccessor;

/**
 * Helpers class for working with tree generation
 * Includes utilities for managing rotations, mirrors, and templates
 */
public final class TreeHelpers
{
    private static final Rotation[] ROTATION_VALUES = Rotation.values();
    private static final Mirror[] MIRROR_VALUES = Mirror.values();

    /**
     * A variant of {@link Template#placeInWorld(IServerWorld, BlockPos, PlacementSettings, Random)} that is much simpler and faster for use in tree generation
     * Allows replacing leaves and air blocks
     */
    @SuppressWarnings("deprecation")
    public static void placeTemplate(Template template, PlacementSettings placementIn, IWorld worldIn, BlockPos pos)
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

    /**
     * Place a trunk from a trunk config
     *
     * @param pos The center position of the trunk
     * @return The height of the trunk placed
     */
    public static int placeTrunk(ISeedReader world, BlockPos pos, Random random, PlacementSettings settings, TrunkConfig trunk)
    {
        final int height = trunk.getHeight(random);
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = (1 - trunk.width) / 2; x <= trunk.width / 2; x++)
        {
            for (int z = (1 - trunk.width) / 2; z <= trunk.width / 2; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    mutablePos.set(x, y, z);
                    transformMutable(mutablePos, settings.getMirror(), settings.getRotation());
                    mutablePos.move(pos);
                    world.setBlock(mutablePos, trunk.state, 3);
                }
            }
        }
        return height;
    }

    public static TemplateManager getTemplateManager(ISeedReader worldIn)
    {
        return worldIn.getLevel().getServer().getStructureManager();
    }

    /**
     * Constructs a placement settings instance useful for tree generation
     * Applies a random rotation and mirror
     * Has a bounding box constrained by the given chunk and surrounding chunks to not cause cascading chunk loading
     */
    public static PlacementSettings getPlacementSettings(ChunkPos chunkPos, Random random)
    {
        return new PlacementSettings()
            .setBoundingBox(new MutableBoundingBox(chunkPos.getMinBlockX() - 16, 0, chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, 256, chunkPos.getMaxBlockZ() + 16))
            .setRandom(random)
            .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR)
            .setRotation(randomRotation(random))
            .setMirror(randomMirror(random));
    }

    /**
     * Given a width of a specific parity, return the transformation of the chosen center position.
     */
    public static BlockPos transformCenter(BlockPos size, PlacementSettings settings)
    {
        return transform(new BlockPos((size.getX() - 1) / 2, 0, (size.getZ() - 1) / 2), settings.getMirror(), settings.getRotation());
    }

    /**
     * {@link Template#transform(BlockPos, Mirror, Rotation, BlockPos)} but simplified
     */
    public static BlockPos transform(BlockPos pos, Mirror mirrorIn, Rotation rotationIn)
    {
        int posX = pos.getX();
        int posZ = pos.getZ();
        boolean mirror = true;
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                posZ = -posZ;
                break;
            case FRONT_BACK:
                posX = -posX;
                break;
            default:
                mirror = false;
        }
        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
                return new BlockPos(posZ, pos.getY(), -posX);
            case CLOCKWISE_90:
                return new BlockPos(-posZ, pos.getY(), posX);
            case CLOCKWISE_180:
                return new BlockPos(-posX, pos.getY(), -posZ);
            default:
                return mirror ? new BlockPos(posX, pos.getY(), posZ) : pos;
        }
    }

    /**
     * {@link Template#transform(BlockPos, Mirror, Rotation, BlockPos)} but simplified, and works with mutable positions
     */
    public static void transformMutable(BlockPos.Mutable pos, Mirror mirrorIn, Rotation rotationIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                pos.setZ(-pos.getZ());
                break;
            case FRONT_BACK:
                pos.setX(-pos.getX());
                break;
        }
        switch (rotationIn)
        {
            case COUNTERCLOCKWISE_90:
                pos.set(pos.getZ(), pos.getY(), -pos.getX());
                break;
            case CLOCKWISE_90:
                pos.set(-pos.getZ(), pos.getY(), pos.getX());
                break;
            case CLOCKWISE_180:
                pos.set(-pos.getX(), pos.getY(), -pos.getZ());
                break;
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
