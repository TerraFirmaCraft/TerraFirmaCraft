/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.RockLayerSettings;
import net.dries007.tfc.world.settings.RockSettings;

public class CaveSpikesFeature extends Feature<NoneFeatureConfiguration>
{
    public CaveSpikesFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final RockLayerSettings rockSettings = ((ChunkGeneratorExtension) context.chunkGenerator()).getRockLayerSettings();

        // The direction that the spike is pointed
        Direction direction = random.nextBoolean() ? Direction.UP : Direction.DOWN;
        BlockState wallState = level.getBlockState(pos.relative(direction.getOpposite()));
        RockSettings wallRock = rockSettings.getRock(wallState.getBlock());
        if (wallRock != null && wallRock.isRawOrHardened(wallState))
        {
            placeIfPresent(level, pos, direction, random, wallRock);
        }
        else
        {
            // Switch directions and try again
            direction = direction.getOpposite();
            wallState = level.getBlockState(pos.relative(direction));
            wallRock = rockSettings.getRock(wallState.getBlock());
            if (wallRock != null && wallRock.isRawOrHardened(wallState))
            {
                placeIfPresent(level, pos, direction, random, wallRock);
            }
        }
        return true;
    }

    protected void place(WorldGenLevel level, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random random)
    {
        placeSmallSpike(level, pos, spike, raw, direction, random);
    }

    protected void placeSmallSpike(WorldGenLevel level, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random random)
    {
        placeSmallSpike(level, pos, spike, raw, direction, random, random.nextFloat());
    }

    protected void placeSmallSpike(WorldGenLevel level, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random random, float sizeWeight)
    {
        // Replace the block above from raw -> hardened, if necessary
        final BlockPos above = pos.above();
        final BlockState stateAbove = level.getBlockState(pos.above());
        if (Helpers.isBlock(stateAbove, BlockTags.BASE_STONE_OVERWORLD))
        {
            level.setBlock(above, raw, 2);
        }

        // Build a spike starting downwards from the target block
        if (sizeWeight < 0.2f)
        {
            replaceBlock(level, pos, spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(level, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else if (sizeWeight < 0.7f)
        {
            replaceBlock(level, pos, spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE));
            replaceBlock(level, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(level, pos.relative(direction, 2), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
        else
        {
            replaceBlockWithoutFluid(level, pos, raw);
            replaceBlock(level, pos.relative(direction, 1), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.BASE));
            replaceBlock(level, pos.relative(direction, 2), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.MIDDLE));
            replaceBlock(level, pos.relative(direction, 3), spike.setValue(RockSpikeBlock.PART, RockSpikeBlock.Part.TIP));
        }
    }

    protected void replaceBlock(WorldGenLevel level, BlockPos pos, BlockState state)
    {
        final Block block = level.getBlockState(pos).getBlock();
        if (block == Blocks.CAVE_AIR)
        {
            setBlock(level, pos, state);
        }
        else if (block == Blocks.WATER || block == TFCBlocks.RIVER_WATER.get())
        {
            setBlock(level, pos, state.setValue(RockSpikeBlock.FLUID, RockSpikeBlock.FLUID.keyFor(Fluids.WATER)));
        }
        else if (block == Blocks.LAVA)
        {
            setBlock(level, pos, state.setValue(RockSpikeBlock.FLUID, RockSpikeBlock.FLUID.keyFor(Fluids.LAVA)));
        }
    }

    protected void replaceBlockWithoutFluid(WorldGenLevel level, BlockPos pos, BlockState state)
    {
        final Block block = level.getBlockState(pos).getBlock();
        if (block == Blocks.CAVE_AIR || block == Blocks.WATER || block == TFCBlocks.RIVER_WATER.get() || block == Blocks.LAVA)
        {
            setBlock(level, pos, state);
        }
    }

    private void placeIfPresent(WorldGenLevel level, BlockPos pos, Direction direction, Random random, RockSettings wallRock)
    {
        wallRock.spike().ifPresent(spike -> place(level, pos, spike.defaultBlockState(), wallRock.hardened().defaultBlockState(), direction, random));
    }
}