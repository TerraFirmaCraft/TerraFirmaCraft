/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class IceCaveFeature extends Feature<NoneFeatureConfiguration>
{
    public IceCaveFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource random = context.random();

        final OverworldClimateModel model = OverworldClimateModel.getIfPresent(level);
        if (model == null)
        {
            return false;
        }

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final ChunkPos chunkPos = new ChunkPos(pos);
        final ChunkData chunkData = ChunkData.get(level, chunkPos);
        for (int i = 0; i < 72; i++)
        {
            mutablePos.setWithOffset(pos, random.nextInt(15) - random.nextInt(15), -3, random.nextInt(15) - random.nextInt(15));
            float maxTemperature = model.getAverageMonthlyTemperature(mutablePos.getZ(), mutablePos.getY(), chunkData.getAverageTemp(mutablePos), 1);
            if (maxTemperature > -4)
            {
                return false;
            }
            if (level.getBlockState(mutablePos).getBlock() == Blocks.CAVE_AIR)
            {
                for (int j = 0; j < 7; j++)
                {
                    mutablePos.move(0, -1, 0);
                    if (!level.isEmptyBlock(mutablePos))
                    {
                        break;
                    }
                }
                BlockState finalState = level.getBlockState(mutablePos);
                mutablePos.move(Direction.UP);
                if (Helpers.isBlock(finalState, BlockTags.BASE_STONE_OVERWORLD))
                {
                    placeDisc(level, mutablePos, random);
                }
                else if (Helpers.isBlock(finalState, BlockTags.ICE) && random.nextFloat() < 0.03F)
                {
                    placeDisc(level, mutablePos, random);
                }
            }
            else if (mutablePos.getY() < 96 && random.nextFloat() < 0.1F)//occluding thin areas
            {
                mutablePos.move(Direction.UP, 5);
                if (!level.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(Direction.DOWN, 3);
                    if (level.isEmptyBlock(mutablePos))
                        placeSphere(level, mutablePos, random);
                }
            }
            if (random.nextFloat() < 0.002F)//extra springs
            {
                mutablePos.setY(4 + random.nextInt(7));
                if (level.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(Direction.UP);
                    if (Helpers.isBlock(level.getBlockState(mutablePos), BlockTags.BASE_STONE_OVERWORLD))
                    {
                        setBlock(level, mutablePos, Fluids.WATER.defaultFluidState().createLegacyBlock());
                        level.scheduleTick(mutablePos, Fluids.WATER, 0);
                    }
                }
            }
            if (random.nextFloat() < 0.03F)//large spikes
            {
                if (mutablePos.getY() < 96 && Helpers.isBlock(level.getBlockState(mutablePos), BlockTags.BASE_STONE_OVERWORLD))
                {
                    mutablePos.move(Direction.DOWN);
                    if (level.isEmptyBlock(mutablePos))
                    {
                        placeSpike(level, mutablePos, random, Direction.DOWN);
                    }
                    else
                    {
                        mutablePos.move(Direction.UP, 2);
                        if (level.isEmptyBlock(mutablePos))
                            placeSpike(level, mutablePos, random, Direction.UP);
                    }
                }
            }
        }
        return true;
    }

    private void placeSpike(WorldGenLevel world, BlockPos.MutableBlockPos mutablePos, RandomSource rand, Direction direction)
    {
        final BlockState state = getState(rand);
        final BlockPos pos = mutablePos.immutable();
        int height = 6 + rand.nextInt(11);
        int radius = 2 + rand.nextInt(1);
        int maxHeightReached = 0;
        for (int y = -3; y <= height; y++)
        {
            float radiusSquared = radius * (1 - 1.5f * Math.abs(y) / height);
            if (radiusSquared < 0)
            {
                continue;
            }
            radiusSquared *= radiusSquared;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos).move(x, y * direction.getStepY(), z);
                    float actualRadius = ((x * x) + (z * z)) / radiusSquared;
                    if (actualRadius < 0.7)
                    {
                        // Fill in actual blocks
                        setBlock(world, mutablePos, state);
                        if (x == 0 && z == 0)
                        {
                            maxHeightReached = y;
                        }
                    }
                    else if (actualRadius < 0.85 && rand.nextBoolean())
                    {
                        // Only fill in if continuing downwards
                        if (world.getBlockState(mutablePos.offset(0, -direction.getStepY(), 0)) == state)
                        {
                            setBlock(world, mutablePos, state);
                        }
                    }
                }
            }
        }
        mutablePos.set(pos).move(direction, maxHeightReached - 1);
    }

    private void placeDisc(WorldGenLevel world, BlockPos.MutableBlockPos mutablePos, RandomSource random)
    {
        final float radius = 1 + random.nextFloat() * random.nextFloat() * 3.5f;
        final float radiusSquared = radius * radius;
        final int size = Mth.ceil(radius);
        final BlockPos pos = mutablePos.immutable();
        final BlockState ice = getState(random);

        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutablePos.move(d);
            mutablePos.move(Direction.DOWN, 2);
            if (world.isEmptyBlock(mutablePos))
                return;
            mutablePos.move(d.getOpposite());
            mutablePos.move(Direction.UP, 2);
        }
        for (int x = -size; x <= size; x++)
        {
            for (int z = -size; z <= size; z++)
            {
                if (x * x + z * z <= radiusSquared)
                {
                    mutablePos.set(pos).move(x, -1, z);
                    if (!world.isEmptyBlock(mutablePos))
                        mutablePos.move(Direction.UP);
                    setBlock(world, mutablePos, ice);
                }
            }
        }
    }

    private void placeSphere(WorldGenLevel world, BlockPos.MutableBlockPos mutablePos, RandomSource rand)
    {
        final float radius = 1 + rand.nextFloat() * rand.nextFloat() * 3.0f;
        final float radiusSquared = radius * radius;
        final int size = Mth.ceil(radius);
        final BlockPos pos = mutablePos.immutable();
        final BlockState ice = Blocks.ICE.defaultBlockState();
        for (int x = -size; x <= size; x++)
        {
            for (int y = -size; y <= size; y++)
            {
                for (int z = -size; z <= size; z++)
                {
                    if (x * x + y * y + z * z <= radiusSquared)
                    {
                        mutablePos.set(pos).move(x, y, z);
                        if (world.isEmptyBlock(mutablePos))
                            setBlock(world, mutablePos, ice);
                    }
                }
            }
        }
    }

    private BlockState getState(RandomSource rand)
    {
        if (rand.nextFloat() < 0.4F)
        {
            return Blocks.PACKED_ICE.defaultBlockState();
        }
        else
        {
            return Blocks.BLUE_ICE.defaultBlockState();
        }
    }
}
