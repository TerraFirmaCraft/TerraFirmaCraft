/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.FallenLeavesBlock;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.util.tracker.WorldTracker;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ForestFeature extends Feature<ForestConfig>
{
    public ForestFeature(Codec<ForestConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ForestConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource random = context.random();
        final ForestConfig config = context.config();

        final ChunkData data = ChunkData.get(level, pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final ForestType forestType = data.getForestType();

        if (random.nextFloat() > forestType.getPerChunkChance()) return false;

        int treeCount = forestType.sampleTrees(random);
        int bushCount = forestType.sampleBushes(random);

        boolean placedTrees = false;
        boolean placedBushes = false;

        for (int i = 0; i < treeCount; i++)
        {
            placedTrees |= placeTree(level, context.chunkGenerator(), random, pos, config, data, mutablePos, forestType);
        }
        for (int j = 0; j < bushCount; j++)
        {
            placedBushes |= placeBush(level, random, pos, config, data, mutablePos, forestType);
        }
        if (placedTrees)
        {
            placeGroundcover(level, random, pos, config, data, mutablePos, forestType.sampleGroundcover(random), forestType);
            placeLeafPile(level, random, pos, config, data, mutablePos, forestType.sampleLeafPiles(random), forestType);
            placeFallenTree(level, random, pos, config, data, mutablePos, forestType);
        }
        return placedTrees || placedBushes;
    }

    private boolean placeTree(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, ForestType typeConfig)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos, typeConfig);
        if (entry != null)
        {
            if (entry.floating())
            {
                mutablePos.setY(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));
            }
            ConfiguredFeature<?, ?> feature;
            final int oldChance = entry.oldGrowthChance();
            final int deadChance = entry.deadChance();
            final float krumChance = mutablePos.getY() > 110 ? 1f : Mth.clampedMap(mutablePos.getY(), 90, 110, 0f, 1f);
            if (entry.krummholz().isPresent() && random.nextFloat() < krumChance)
            {
                feature = entry.krummholz().get().value();
            }
            else if (typeConfig.isPrimary() && oldChance > 0 && random.nextInt(oldChance) == 0)//todo fix
            {
                feature = entry.getOldGrowthFeature();
            }
            else if (deadChance > 0 && (random.nextInt(deadChance) == 0 || typeConfig.isDead()))
            {
                feature = entry.getDeadFeature();
            }
            else
            {
                final int spoilerChance = entry.spoilerOldGrowthChance();
                if (spoilerChance > 0 && random.nextInt(spoilerChance) == 0)
                {
                    feature = entry.getOldGrowthFeature();
                }
                else
                {
                    feature = entry.getFeature();
                }
            }
            return feature.place(level, generator, random, mutablePos);
        }
        return false;
    }

    private boolean placeBush(WorldGenLevel level, RandomSource random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, ForestType type)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos, type);
        if (entry != null && EnvironmentHelpers.canPlaceBushOn(level, mutablePos))
        {
            entry.bushLog().ifPresent(log -> entry.bushLeaves().ifPresent(leaves -> {
                placeBushPart(level, mutablePos, log, leaves, 1.0F, random, true);
                for (int i = 0; i < 5; i++)
                {
                    if (random.nextInt(4) == 0)
                    {
                        mutablePos.move(Direction.Plane.HORIZONTAL.getRandomDirection(random));
                        placeBushPart(level, mutablePos, leaves, leaves, 0.7F, random, false);
                        if (random.nextInt(6) == 0)
                        {
                            mutablePos.move(Direction.UP);
                            placeBushPart(level, mutablePos, leaves, leaves, 0.6F, random, false);
                            break;
                        }
                    }
                }
            }));
            return true;
        }
        return false;
    }

    private void placeBushPart(WorldGenLevel level, BlockPos.MutableBlockPos mutablePos, BlockState log, BlockState leaves, float decay, RandomSource rand, boolean needsEmptyCenter)
    {
        if (EnvironmentHelpers.isWorldgenReplaceable(level, mutablePos))
        {
            setBlock(level, mutablePos, log);
        }
        else if (needsEmptyCenter)
        {
            return;
        }
        for (Direction facing : Helpers.DIRECTIONS)
        {
            if (facing != Direction.DOWN)
            {
                BlockPos offsetPos = mutablePos.offset(facing.getStepX(), facing.getStepY(), facing.getStepZ());
                if (EnvironmentHelpers.isWorldgenReplaceable(level, offsetPos) && rand.nextFloat() < decay)
                {
                    setBlock(level, offsetPos, leaves);
                }
            }
        }
    }

    private void placeGroundcover(WorldGenLevel level, RandomSource random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, int tries, ForestType type)
    {
        if (tries == 0)
            return;
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos, type);
        if (entry != null)
        {
            entry.groundcover().ifPresent(groundcover -> {
                for (int j = 0; j < tries; ++j)
                {
                    BlockState placementState = groundcover.get(random);

                    mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
                    mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

                    placementState = FluidHelpers.fillWithFluid(placementState, level.getFluidState(mutablePos).getType());
                    if (placementState != null && EnvironmentHelpers.isWorldgenReplaceable(level.getBlockState(mutablePos)) && EnvironmentHelpers.isOnSturdyFace(level, mutablePos))
                    {
                        setBlock(level, mutablePos, placementState);
                    }
                }
            });
        }
    }

    private void placeLeafPile(WorldGenLevel level, RandomSource random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, int tries, ForestType type)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos, type);
        if (entry != null)
        {
            entry.fallenLeaves().ifPresent(placementState -> {
                for (int i = 0; i < tries; ++i)
                {
                    mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
                    mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));
                    final BlockPos origin = mutablePos.immutable();

                    for (int j = 0; j < 8; j++)
                    {
                        mutablePos.setWithOffset(origin, Mth.nextInt(random, -2, 2), 0, Mth.nextInt(random, -2, 2));
                        if (level.getFluidState(mutablePos).isEmpty() && EnvironmentHelpers.isOnSturdyFace(level, mutablePos) && EnvironmentHelpers.isWorldgenReplaceable(level, mutablePos))
                        {
                            placementState = placementState.setValue(FallenLeavesBlock.LAYERS, Mth.nextInt(random, 1, FallenLeavesBlock.MAX_LAYERS - 3));
                            level.setBlock(mutablePos, placementState, 3);
                        }
                    }
                }
            });
        }
    }


    private void placeFallenTree(WorldGenLevel level, RandomSource random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, ForestType type)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

        mutablePos.move(Direction.DOWN);
        BlockState downState = level.getBlockState(mutablePos);
        mutablePos.move(Direction.UP);
        if (Helpers.isBlock(downState, TFCTags.Blocks.BUSH_PLANTABLE_ON) || Helpers.isBlock(downState, TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
        {
            final ForestConfig.Entry entry = getTree(data, random, config, mutablePos, type);
            if (entry != null)
            {
                final int fallChance = entry.fallenChance();
                if (fallChance > 0 && level.getRandom().nextInt(fallChance) == 0)
                {
                    BlockState log = entry.fallenLog().orElse(null);
                    if (log != null)
                    {
                        final Direction axis = Direction.Plane.HORIZONTAL.getRandomDirection(random);

                        log = Helpers.setProperty(log, TFCBlockStateProperties.NATURAL, false);
                        log = Helpers.setProperty(log, BlockStateProperties.AXIS, axis.getAxis());

                        final int length = 4 + random.nextInt(10);
                        final BlockPos start = mutablePos.immutable();
                        final boolean[] moment = new boolean[length];

                        mutablePos.set(start);
                        int valid = 0;
                        for (; valid < length; valid++)
                        {
                            final BlockState replaceState = level.getBlockState(mutablePos);
                            if (EnvironmentHelpers.isWorldgenReplaceable(replaceState) || replaceState.getBlock() instanceof ILeavesBlock)
                            {
                                mutablePos.move(Direction.DOWN);
                                moment[valid] = level.getBlockState(mutablePos).isFaceSturdy(level, mutablePos, Direction.UP);
                            }
                            else
                            {
                                break;
                            }

                            mutablePos.move(Direction.UP);
                            mutablePos.move(axis);
                        }

                        int left = 0, right = valid - 1;
                        for (; left < moment.length; left++)
                        {
                            if (moment[left]) break;
                        }
                        for (; right >= 0; right--)
                        {
                            if (moment[right]) break;
                        }

                        if (left <= valid / 2 && right >= valid / 2 && valid >= 3)
                        {
                            // Balanced
                            mutablePos.set(start);
                            for (int i = 0; i < length; i++)
                            {
                                level.setBlock(mutablePos, log, 2);
                                mutablePos.move(axis);
                            }
                        }

                    }
                }
            }
        }
    }

    @Nullable
    private ForestConfig.Entry getTree(ChunkData chunkData, RandomSource random, ForestConfig config, BlockPos pos, ForestType type)
    {
        final float groundwater = chunkData.getGroundwater(pos);
        final float averageTemperature = EnvironmentHelpers.adjustAvgTempForElev(pos.getY(), chunkData.getAverageTemp(pos));
        final List<ForestConfig.Entry> entries = config.entries().stream().map(configuredFeature -> configuredFeature.value().config()).map(cfg -> (ForestConfig.Entry) cfg)
            .filter(entry -> entry.isValid(averageTemperature, groundwater))
            .sorted(Comparator.comparingDouble(entry -> entry.distanceFromMean(averageTemperature, groundwater)))
            .collect(Collectors.toList());

        if (entries.isEmpty()) return null;
        if (entries.size() == 1)
            return entries.getFirst();
        final int maxSize = type.getMaxTreeTypes();
        final int originalSize = entries.size();
        for (int i = maxSize; i < originalSize; i++)
        {
            entries.removeLast();
        }
        int alternate = type.getAlternateSize();
        while (entries.size() > 1 && alternate > 0)
        {
            entries.remove(0);
            alternate--;
        }

        int index = 0;
        while (index < entries.size() - 1 && random.nextFloat() < 0.6f)
        {
            index++;
        }
        return entries.get(index);
    }

    /**
     * Holder for {@link ForestConfig.Entry} in order to hold all tree instances in a tag.
     */
    public static class Entry extends Feature<ForestConfig.Entry>
    {
        public Entry(Codec<ForestConfig.Entry> codec)
        {
            super(codec);
        }

        @Override
        public boolean place(FeaturePlaceContext<ForestConfig.Entry> context)
        {
            throw new IllegalArgumentException("This is not a real feature and should never be placed!");
        }
    }
}