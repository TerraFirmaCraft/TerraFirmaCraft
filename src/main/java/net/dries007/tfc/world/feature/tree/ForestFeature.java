/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
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
        final Random rand = context.random();
        final ForestConfig config = context.config();

        final ChunkDataProvider provider = ChunkDataProvider.get(context.chunkGenerator());
        final ChunkData data = provider.get(level, pos);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final ForestType forestType = data.getForestType();

        int treeCount;
        int groundCount;
        boolean placedTrees = false;
        boolean placedBushes = false;
        if (forestType == ForestType.SPARSE)
        {
            if (rand.nextFloat() < 0.08f)
            {
                int trees = 1 + rand.nextInt(3);
                for (int i = 0; i < trees; i++)
                {
                    placedTrees |= placeTree(level, context.chunkGenerator(), rand, pos, config, data, mutablePos, false);
                }
                placeGroundcover(level, rand, pos, config, data, mutablePos, 10);
            }
            return true;
        }
        else if (forestType == ForestType.EDGE)
        {
            treeCount = 2;
            groundCount = 15;
        }
        else if (forestType == ForestType.NORMAL)
        {
            treeCount = 5;
            groundCount = 30;
        }
        else if (forestType == ForestType.OLD_GROWTH)
        {
            treeCount = 7;
            groundCount = 40;
        }
        else
        {
            return false;
        }

        final float density = data.getForestDensity();
        treeCount = (int) (treeCount * (0.6f + 0.9f * density));
        for (int i = 0; i < treeCount; i++)
        {
            placedTrees |= placeTree(level, context.chunkGenerator(), rand, pos, config, data, mutablePos, forestType == ForestType.OLD_GROWTH);
        }
        int bushCount = (int) (treeCount * density);
        for (int j = 0; j < bushCount; j++)
        {
            placedBushes |= placeBush(level, context.chunkGenerator(), rand, pos, config, data, mutablePos);
        }
        if (placedTrees)
        {
            placeGroundcover(level, rand, pos, config, data, mutablePos, groundCount);
            if (rand.nextInt(14) == 0)
            {
                placeFallenTree(level, rand, pos, config, data, mutablePos, groundCount);
            }
        }
        return placedTrees || placedBushes;
    }

    private boolean placeTree(WorldGenLevel level, ChunkGenerator generator, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, boolean allowOldGrowth)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null)
        {
            ConfiguredFeature<?, ?> feature;
            if (allowOldGrowth && random.nextInt(6) == 0)
            {
                feature = entry.getOldGrowthFeature();
            }
            else
            {
                feature = random.nextInt(200) == 0 ? entry.getOldGrowthFeature() : entry.getFeature();
            }
            return feature.place(level, generator, random, mutablePos);
        }
        return false;
    }

    private boolean placeBush(WorldGenLevel level, ChunkGenerator generator, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null && level.isEmptyBlock(mutablePos) && level.getBlockState(mutablePos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            entry.bushLog().ifPresent(log -> entry.bushLeaves().ifPresent(leaves -> {
                placeBushPart(level, mutablePos, log, leaves, 1.0F, random);
                for (int i = 0; i < 5; i++)
                {
                    if (random.nextInt(4) == 0)
                    {
                        mutablePos.move(Direction.Plane.HORIZONTAL.getRandomDirection(random));
                        placeBushPart(level, mutablePos, leaves, leaves, 0.7F, random);
                        if (random.nextInt(6) == 0)
                        {
                            mutablePos.move(Direction.UP);
                            placeBushPart(level, mutablePos, leaves, leaves, 0.6F, random);
                            break;
                        }
                    }
                }


            }));
            return true;
        }
        return false;
    }

    private void placeBushPart(WorldGenLevel level, BlockPos.MutableBlockPos mutablePos, BlockState log, BlockState leaves, float decay, Random rand)
    {
        setBlock(level, mutablePos, log);
        for (Direction facing : Helpers.DIRECTIONS)
        {
            if (facing != Direction.DOWN)
            {
                BlockPos offsetPos = mutablePos.offset(facing.getStepX(), facing.getStepY(), facing.getStepZ());
                if (level.isEmptyBlock(offsetPos) || level.getBlockState(offsetPos).is(TFCTags.Blocks.PLANTS) && rand.nextFloat() < decay)
                {
                    setBlock(level, offsetPos, leaves);
                }
            }
        }
    }

    private void placeGroundcover(WorldGenLevel level, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, int tries)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null)
        {
            entry.groundcover().ifPresent(groundcover -> {
                for (int j = 0; j < tries; ++j)
                {
                    final int idx = random.nextInt(groundcover.size());
                    BlockState placementState = groundcover.get(idx);

                    mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
                    mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

                    placementState = FluidHelpers.fillWithFluid(placementState, level.getFluidState(mutablePos).getType());
                    if (placementState != null && (level.isEmptyBlock(mutablePos) || level.isWaterAt(mutablePos)) && level.getBlockState(mutablePos.below()).isFaceSturdy(level, mutablePos, Direction.UP))
                    {
                        setBlock(level, mutablePos, placementState);
                    }
                }
            });
        }
    }

    private void placeFallenTree(WorldGenLevel level, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.MutableBlockPos mutablePos, int tries)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(level.getHeight(Heightmap.Types.OCEAN_FLOOR, mutablePos.getX(), mutablePos.getZ()));

        mutablePos.move(Direction.DOWN);
        BlockState downState = level.getBlockState(mutablePos);
        mutablePos.move(Direction.UP);
        if (downState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || downState.is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
        {
            final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
            if (entry != null)
            {
                BlockState log = entry.fallenLog().orElse(null);
                if (log != null)
                {
                    final Direction axis = Direction.Plane.HORIZONTAL.getRandomDirection(random);

                    log = Helpers.setProperty(log, TFCBlockStateProperties.NATURAL, true);
                    log = Helpers.setProperty(log, BlockStateProperties.AXIS, axis.getAxis());

                    final int length = 4 + random.nextInt(10);
                    final BlockPos start = mutablePos.immutable();
                    final boolean[] moment = new boolean[length];

                    mutablePos.set(start);
                    int valid = 0;
                    for (; valid < length; valid++)
                    {
                        final BlockState replaceState = level.getBlockState(mutablePos);
                        if (replaceState.getMaterial().isReplaceable() || replaceState.getBlock() instanceof ILeavesBlock)
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

    @Nullable
    private ForestConfig.Entry getTree(ChunkData chunkData, Random random, ForestConfig config, BlockPos pos)
    {
        List<ForestConfig.Entry> entries = new ArrayList<>(4);
        float rainfall = chunkData.getRainfall(pos);
        float averageTemperature = chunkData.getAverageTemp(pos);
        for (ForestConfig.Entry entry : config.entries())
        {
            // silly way to halfway guarantee that stuff is in general order of dominance
            float lastRain = entry.getAverageRain();
            float lastTemp = entry.getAverageTemp();
            if (entry.isValid(averageTemperature, rainfall))
            {
                if (entry.distanceFromMean(lastTemp, lastRain) < entry.distanceFromMean(averageTemperature, rainfall))
                {
                    entries.add(entry); // if the last one was closer to it's target, just add it normally
                }
                else
                {
                    entries.add(0, entry); // if the new one is closer, stick it in front
                }
            }
        }

        float weirdness = chunkData.getForestWeirdness();
        Collections.rotate(entries, -(int) (weirdness * (entries.size() - 1f)));
        // remove up to 3 entries from the config based on weirdness, less likely to happen each time
        if (!entries.isEmpty())
        {
            for (int i = 1; i >= -1; i--)
            {
                if (entries.size() <= 1)
                    break;
                if (random.nextFloat() > weirdness - (0.15f * i) + 0.1f)
                {
                    entries.remove(entries.size() - 1);
                }
            }
        }
        else
        {
            return null;
        }

        int index = 0;
        while (index < entries.size() - 1 && random.nextFloat() < 0.6f)
        {
            index++;
        }
        return entries.get(index);
    }
}