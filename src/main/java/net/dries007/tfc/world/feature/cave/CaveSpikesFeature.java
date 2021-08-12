/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class CaveSpikesFeature extends Feature<NoneFeatureConfiguration>
{
    public CaveSpikesFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel worldIn = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();

        // The direction that the spike is pointed
        Direction direction = rand.nextBoolean() ? Direction.UP : Direction.DOWN;
        BlockState wallState = worldIn.getBlockState(pos.relative(direction.getOpposite()));
        Rock wallRock = RockManager.INSTANCE.getRock(wallState.getBlock());
        if (wallRock != null && wallRock.getBlock(Rock.BlockType.RAW) == wallState.getBlock())
        {
            place(worldIn, pos, wallRock.getBlock(Rock.BlockType.SPIKE).defaultBlockState(), wallRock.getBlock(Rock.BlockType.RAW).defaultBlockState(), direction, rand);
        }
        else
        {
            // Switch directions and try again
            direction = direction.getOpposite();
            wallState = worldIn.getBlockState(pos.relative(direction));
            wallRock = RockManager.INSTANCE.getRock(wallState.getBlock());
            if (wallRock != null && wallRock.getBlock(Rock.BlockType.RAW) == wallState.getBlock())
            {
                place(worldIn, pos, wallRock.getBlock(Rock.BlockType.SPIKE).defaultBlockState(), wallRock.getBlock(Rock.BlockType.RAW).defaultBlockState(), direction, rand);
            }
        }
        return true;
    }

    protected void place(WorldGenLevel worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        placeSmallSpike(worldIn, pos, spike, raw, direction, rand);
    }

    protected void placeSmallSpike(WorldGenLevel worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        placeSmallSpike(worldIn, pos, spike, raw, direction, rand, rand.nextFloat());
    }

    protected void placeSmallSpike(WorldGenLevel worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand, float sizeWeight)
    {
        if (!raw.is(BlockTags.BASE_STONE_OVERWORLD))
            return;
        // Build a spike starting downwards from the target block
        if (sizeWeight < 0.2f)
        {
            replaceBlock(worldIn, pos, spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(worldIn, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else if (sizeWeight < 0.7f)
        {
            replaceBlock(worldIn, pos, spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE));
            replaceBlock(worldIn, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(worldIn, pos.relative(direction, 2), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else
        {
            replaceBlockWithoutFluid(worldIn, pos, raw);
            replaceBlock(worldIn, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE));
            replaceBlock(worldIn, pos.relative(direction, 2), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(worldIn, pos.relative(direction, 3), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
    }

    protected void replaceBlock(WorldGenLevel world, BlockPos pos, BlockState state)
    {
        // We check explicitly for cave air here, because spikes shouldn't generate not in caves
        // Otherwise, try and fill in all possible fluids this allows
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.CAVE_AIR)
        {
            setBlock(world, pos, state);
        }
        else if (block == Blocks.WATER)
        {
            setBlock(world, pos, state.setValue(RockSpikeBlock.FLUID, RockSpikeBlock.FLUID.keyFor(Fluids.WATER)));
        }
        else if (block == Blocks.LAVA)
        {
            setBlock(world, pos, state.setValue(RockSpikeBlock.FLUID, RockSpikeBlock.FLUID.keyFor(Fluids.LAVA)));
        }
    }

    protected void replaceBlockWithoutFluid(WorldGenLevel world, BlockPos pos, BlockState state)
    {
        Block block = world.getBlockState(pos).getBlock();
        if (block == Blocks.CAVE_AIR || block == Blocks.WATER || block == Blocks.LAVA)
        {
            setBlock(world, pos, state);
        }
    }
}