/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.RiverWaterBlock;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.BranchDirection;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.mixin.accessor.StructureTemplateAccessor;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IWeighted;

/**
 * Helpers class for working with tree generation
 * Includes utilities for managing rotations, mirrors, and templates
 */
public final class TreeHelpers
{
    private static final Rotation[] ROTATION_VALUES = Rotation.values();
    private static final Mirror[] MIRROR_VALUES = Mirror.values();

    public static boolean isValidLocation(LevelAccessor level, BlockPos pos, StructurePlaceSettings settings, TreePlacementConfig config)
    {
        return isValidGround(level, pos, settings, config) && isValidTrunk(level, pos, settings, config);
    }

    /**
     * Checks if there is valid ground for a tree placement (at y = 0 and y = -1)
     * @return {@code true} if the tree is legal to grow here.
     */
    public static boolean isValidGround(LevelAccessor level, BlockPos pos, StructurePlaceSettings settings, TreePlacementConfig config)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = (1 - config.width()) / 2; x <= config.width() / 2; x++)
        {
            for (int z = (1 - config.width()) / 2; z <= config.width() / 2; z++)
            {
                mutablePos.set(x, 0, z);
                transformMutable(mutablePos, settings.getMirror(), settings.getRotation());
                mutablePos.move(pos);

                if (config.groundType() == TreePlacementConfig.GroundType.FLOATING)
                {
                    return isValidFloatingPosition(level, mutablePos);
                }
                else if (!(config.mayPlaceUnderwater() ? isValidPositionPossiblyUnderwater(level, mutablePos, config) : isValidPosition(level, mutablePos, config)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return {@code false} if the position is invalid
     */
    private static boolean isValidPosition(LevelAccessor level, BlockPos.MutableBlockPos mutablePos, TreePlacementConfig config)
    {
        final BlockState stateAt = level.getBlockState(mutablePos);
        final boolean isInWater = stateAt.getFluidState().getType() == Fluids.WATER;
        if (!(config.mayPlaceInWater() && FluidHelpers.isAirOrEmptyFluid(stateAt) && isInWater)
            && !stateAt.isAir()
            && !(stateAt.getBlock() instanceof SaplingBlock))
        {
            return false;
        }

        mutablePos.move(0, -1, 0);

        final BlockState stateBelow = level.getBlockState(mutablePos);
        boolean treeGrowsOn = Helpers.isBlock(stateBelow, TFCTags.Blocks.TREE_GROWS_ON);
        if (!treeGrowsOn && config.groundType() == TreePlacementConfig.GroundType.SAND)
        {
            treeGrowsOn = Helpers.isBlock(stateBelow, BlockTags.SAND);
        }
        if (!treeGrowsOn && config.mayPlaceInWater() && isInWater)
        {
            treeGrowsOn = Helpers.isBlock(stateBelow, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
        }
        return treeGrowsOn;
    }

    private static boolean isValidFloatingPosition(LevelAccessor level, BlockPos.MutableBlockPos mutablePos)
    {
        final BlockState stateAt = level.getBlockState(mutablePos);
        if (!EnvironmentHelpers.isWorldgenReplaceable(stateAt) || !stateAt.getFluidState().isEmpty())
            return false;
        mutablePos.move(0, -1, 0);
        final BlockState stateBelow = level.getBlockState(mutablePos);
        return Helpers.isBlock(stateBelow, TFCTags.Blocks.BUSH_PLANTABLE_ON) || Helpers.isBlock(stateBelow, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON) || Helpers.isFluid(stateBelow.getFluidState(), TFCTags.Fluids.ANY_INFINITE_WATER);
    }

    private static boolean isValidPositionPossiblyUnderwater(LevelAccessor level, BlockPos.MutableBlockPos mutablePos, TreePlacementConfig config)
    {
        final BlockState stateAt = level.getBlockState(mutablePos);
        final FluidState fluid = stateAt.getFluidState();

        boolean water;

        if (fluid.getType() == TFCFluids.RIVER_WATER.get())
        {
            return false; // No river water
        }
        else
        {
            water = fluid.getType() == Fluids.WATER ||
                (!config.requiresFreshwater() && fluid.getType() == TFCFluids.SALT_WATER.getSource());
        }

        mutablePos.move(0, -1, 0);
        final BlockState stateBelow = level.getBlockState(mutablePos);
        if (water)
        {
            return Helpers.isBlock(stateBelow, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON);
        }
        if (fluid.isEmpty())
        {
            boolean treeGrowsOn = Helpers.isBlock(stateBelow, TFCTags.Blocks.TREE_GROWS_ON);
            if (!treeGrowsOn && config.groundType() == TreePlacementConfig.GroundType.SAND)
            {
                treeGrowsOn = Helpers.isBlock(stateBelow, BlockTags.SAND);
            }
            return treeGrowsOn;
        }
        return false;
    }

    /**
     * Checks if there is enough free space above the tree, for a tree placement (at y > 0), for a given height and radius around the trunk
     * @return {@code true} if the tree is legal to grow here.
     */
    public static boolean isValidTrunk(LevelAccessor level, BlockPos pos, StructurePlaceSettings settings, TreePlacementConfig config)
    {
        final Predicate<BlockState> trunkTest = config.mayPlaceUnderwater() ? FluidHelpers::isAirOrEmptyFluid : BlockBehaviour.BlockStateBase::isAir;
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = (1 - config.width()) / 2; x <= config.width() / 2; x++)
        {
            for (int z = (1 - config.width()) / 2; z <= config.width() / 2; z++)
            {
                for (int y = 1; y < config.height(); y++)
                {
                    mutablePos.set(x, y, z);
                    transformMutable(mutablePos, settings.getMirror(), settings.getRotation());
                    mutablePos.move(pos);

                    final BlockState stateAt = level.getBlockState(mutablePos);
                    if (!trunkTest.test(stateAt))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * A variant of {@link StructureTemplate#placeInWorld(ServerLevelAccessor, BlockPos, BlockPos, StructurePlaceSettings, RandomSource, int)} that is much simpler and faster for use in tree generation
     * Allows replacing leaves and air blocks
     */
    public static void placeTemplate(StructureTemplate template, StructurePlaceSettings placementIn, ServerLevelAccessor level, BlockPos pos)
    {
        final List<StructureTemplate.StructureBlockInfo> transformedBlockInfos = placementIn.getRandomPalette(((StructureTemplateAccessor) template).accessor$getPalettes(), pos).blocks();
        BoundingBox boundingBox = placementIn.getBoundingBox();
        for (StructureTemplate.StructureBlockInfo blockInfo : StructureTemplate.processBlockInfos(level, pos, pos, placementIn, transformedBlockInfos, template))
        {
            BlockPos posAt = blockInfo.pos();
            if (boundingBox == null || boundingBox.isInside(posAt))
            {
                BlockState stateAt = level.getBlockState(posAt);
                if (EnvironmentHelpers.isWorldgenReplaceable(stateAt) || Helpers.isBlock(stateAt.getBlock(), BlockTags.LEAVES))
                {
                    // No world, can't rotate with world context
                    @SuppressWarnings("deprecation")
                    BlockState stateReplace = blockInfo.state().mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
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
    public static int placeTrunk(WorldGenLevel level, BlockPos pos, RandomSource random, StructurePlaceSettings settings, TrunkConfig trunk)
    {
        final int height = trunk.getHeight(random);
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int y = 0; y < height; y++)
        {
            if (trunk.wide())
            {
                placeTrunk(settings, trunk, level, pos, cursor, 0, y, 0, BranchDirection.TRUNK_SOUTH_EAST);
                placeTrunk(settings, trunk, level, pos, cursor, 0, y, 1, BranchDirection.TRUNK_NORTH_EAST);
                placeTrunk(settings, trunk, level, pos, cursor, 1, y, 0, BranchDirection.TRUNK_SOUTH_WEST);
                placeTrunk(settings, trunk, level, pos, cursor, 1, y, 1, BranchDirection.TRUNK_NORTH_WEST);
            }
            else
            {
                placeTrunk(settings, trunk, level, pos, cursor, 0, y, 0, BranchDirection.DOWN);
            }
        }
        return height;
    }

    private static void placeTrunk(StructurePlaceSettings settings, TrunkConfig trunk, WorldGenLevel level, BlockPos pos, BlockPos.MutableBlockPos cursor, int x, int y, int z, BranchDirection branch)
    {
        cursor.set(x, y, z);
        transformMutable(cursor, settings.getMirror(), settings.getRotation());
        cursor.move(pos);

        final BranchDirection direction = branch.mirror(settings.getMirror()).rotate(settings.getRotation());
        final BlockState state = trunk.state().setValue(TFCBlockStateProperties.BRANCH_DIRECTION, direction);

        level.setBlock(cursor, state, 3);
    }

    /**
     * @param origin The position below the trunk center
     */
    public static boolean placeRoots(WorldGenLevel level, BlockPos origin, RootConfig config, RandomSource random)
    {
        if (config.specialPlacer().isPresent())
        {
            return config.specialPlacer().get().placeRoots(level, random, origin, origin.above(), config);
        }
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        final Map<Block, IWeighted<BlockState>> blocks = config.blocks();
        for (int i = 0; i < config.tries(); i++)
        {
            final int dx = Helpers.triangle(random, config.width());
            final int dz = Helpers.triangle(random, config.width());
            final int dy = -random.nextInt(config.height());
            cursor.setWithOffset(origin, dx, dy, dz);
            final IWeighted<BlockState> weighted = blocks.get(level.getBlockState(cursor).getBlock());
            if (weighted != null)
            {
                level.setBlock(cursor, weighted.get(random), 3);
            }
        }
        return true;
    }

    public static StructureTemplateManager getStructureManager(WorldGenLevel level)
    {
        return level.getLevel().getServer().getStructureManager();
    }

    /**
     * Constructs a placement settings instance useful for tree generation
     * Applies a random rotation and mirror
     * Has a bounding box constrained by the given chunk and surrounding chunks to not cause cascading chunk loading
     */
    public static StructurePlaceSettings getPlacementSettings(LevelHeightAccessor level, ChunkPos chunkPos, RandomSource random)
    {
        return new StructurePlaceSettings()
            .setBoundingBox(new BoundingBox(chunkPos.getMinBlockX() - 16, level.getMinBuildHeight(), chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, level.getMaxBuildHeight(), chunkPos.getMaxBlockZ() + 16))
            .setRandom(random)
            .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
            .setRotation(randomRotation(random))
            .setMirror(randomMirror(random));
    }

    public static void randomize(StructurePlaceSettings settings, RandomSource random)
    {
        settings.setRotation(randomRotation(random)).setMirror(randomMirror(random));
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

    private static Rotation randomRotation(RandomSource random)
    {
        return ROTATION_VALUES[random.nextInt(ROTATION_VALUES.length)];
    }

    private static Mirror randomMirror(RandomSource random)
    {
        return MIRROR_VALUES[random.nextInt(MIRROR_VALUES.length)];
    }
}