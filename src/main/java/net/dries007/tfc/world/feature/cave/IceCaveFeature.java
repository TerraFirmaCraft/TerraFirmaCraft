/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Climate;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class IceCaveFeature extends Feature<NoFeatureConfig>
{
    public IceCaveFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final ChunkPos chunkPos = new ChunkPos(pos);
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData chunkData = provider.get(chunkPos, ChunkData.Status.CLIMATE);
        for (int i = 0; i < 72; i++)
        {
            mutablePos.setAndOffset(pos, rand.nextInt(15) - rand.nextInt(15), -3, rand.nextInt(15) - rand.nextInt(15));
            float maxTemperature = Climate.calculateMonthlyAverageTemperature(mutablePos.getZ(), mutablePos.getY(), chunkData.getAverageTemp(mutablePos), 1);
            if (maxTemperature > -4)
            {
                return false;
            }
            if (world.getBlockState(mutablePos).getBlock().matchesBlock(Blocks.CAVE_AIR))
            {
                for (int j = 0; j < 7; j++)
                {
                    mutablePos.move(0, -1, 0);
                    if (!world.isAirBlock(mutablePos))
                    {
                        break;
                    }
                }
                BlockState finalState = world.getBlockState(mutablePos);
                mutablePos.move(Direction.UP);
                if (finalState.isIn(BlockTags.BASE_STONE_OVERWORLD))
                {
                    placeDisc(world, mutablePos, rand);
                }
                else if (finalState.isIn(BlockTags.ICE) && rand.nextFloat() < 0.03F)
                {
                    placeDisc(world, mutablePos, rand);
                }
            }
            else if (mutablePos.getY() < 96 && rand.nextFloat() < 0.1F)//occluding thin areas
            {
                mutablePos.move(Direction.UP, 5);
                if (!world.isAirBlock(mutablePos))
                {
                    mutablePos.move(Direction.DOWN, 3);
                    if (world.isAirBlock(mutablePos))
                        placeSphere(world, mutablePos, rand);
                }
            }
            if (rand.nextFloat() < 0.002F)//extra springs
            {
                mutablePos.setY(4 + rand.nextInt(7));
                if (world.isAirBlock(mutablePos))
                {
                    mutablePos.move(Direction.UP);
                    if (world.getBlockState(mutablePos).isIn(BlockTags.BASE_STONE_OVERWORLD))
                    {
                        setBlockState(world, mutablePos, Fluids.WATER.getDefaultState().getBlockState());
                        world.getPendingFluidTicks().scheduleTick(mutablePos, Fluids.WATER, 0);
                    }
                }
            }
            if (rand.nextFloat() < 0.03F)//large spikes
            {
                if (mutablePos.getY() < 96 && world.getBlockState(mutablePos).isIn(BlockTags.BASE_STONE_OVERWORLD))
                {
                    mutablePos.move(Direction.DOWN);
                    if (world.isAirBlock(mutablePos))
                    {
                        placeSpike(world, mutablePos, rand, Direction.DOWN);
                    }
                    else
                    {
                        mutablePos.move(Direction.UP, 2);
                        if (world.isAirBlock(mutablePos))
                            placeSpike(world, mutablePos, rand, Direction.UP);
                    }
                }
            }
        }
        return true;
    }

    private void placeSpike(ISeedReader world, BlockPos.Mutable mutablePos, Random rand, Direction direction)
    {
        final BlockState state = getState(rand);
        final BlockPos pos = mutablePos.toImmutable();
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
                    mutablePos.setPos(pos).move(x, y * direction.getYOffset(), z);
                    float actualRadius = ((x * x) + (z * z)) / radiusSquared;
                    if (actualRadius < 0.7)
                    {
                        // Fill in actual blocks
                        setBlockState(world, mutablePos, state);
                        if (x == 0 && z == 0)
                        {
                            maxHeightReached = y;
                        }
                    }
                    else if (actualRadius < 0.85 && rand.nextBoolean())
                    {
                        // Only fill in if continuing downwards
                        if (world.getBlockState(mutablePos.add(0, -direction.getYOffset(), 0)) == state)
                        {
                            setBlockState(world, mutablePos, state);
                        }
                    }
                }
            }
        }
        mutablePos.setPos(pos).move(direction, maxHeightReached - 1);
    }

    private void placeDisc(ISeedReader world, BlockPos.Mutable mutablePos, Random rand)
    {
        final float radius = 1 + rand.nextFloat() * rand.nextFloat() * 3.5f;
        final float radiusSquared = radius * radius;
        final int size = MathHelper.ceil(radius);
        final BlockPos pos = mutablePos.toImmutable();
        final BlockState ice = getState(rand);

        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutablePos.move(d);
            mutablePos.move(Direction.DOWN, 2);
            if (world.isAirBlock(mutablePos))
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
                    mutablePos.setPos(pos).move(x, -1, z);
                    if (!world.isAirBlock(mutablePos))
                        mutablePos.move(Direction.UP);
                    setBlockState(world, mutablePos, ice);
                }
            }
        }
    }

    private void placeSphere(ISeedReader world, BlockPos.Mutable mutablePos, Random rand)
    {
        final float radius = 1 + rand.nextFloat() * rand.nextFloat() * 3.0f;
        final float radiusSquared = radius * radius;
        final int size = MathHelper.ceil(radius);
        final BlockPos pos = mutablePos.toImmutable();
        final BlockState ice = Blocks.ICE.getDefaultState();
        for (int x = -size; x <= size; x++)
        {
            for (int y = -size; y <= size; y++)
            {
                for (int z = -size; z <= size; z++)
                {
                    if (x * x + y * y + z * z <= radiusSquared)
                    {
                        mutablePos.setPos(pos).move(x, y, z);
                        if (world.isAirBlock(mutablePos))
                            setBlockState(world, mutablePos, ice);
                    }
                }
            }
        }
    }

    private BlockState getState(Random rand)
    {
        if (rand.nextFloat() < 0.4F)
        {
            return Blocks.PACKED_ICE.getDefaultState();
        }
        else
        {
            return Blocks.BLUE_ICE.getDefaultState();
        }
    }
}
