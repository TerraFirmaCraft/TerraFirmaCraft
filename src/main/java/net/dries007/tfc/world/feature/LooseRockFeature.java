/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

/**
 * Places a single loose rock at the target position
 */
public class LooseRockFeature extends Feature<NoneFeatureConfiguration>
{
    public LooseRockFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.get(generator);
        final ChunkData data = provider.get(pos);
        final Rock rock = data.getRockDataOrThrow().getRock(pos.getX(), pos.getY(), pos.getZ());
        final BlockState stateAt = worldIn.getBlockState(pos);
        final BlockState rockState = FluidHelpers.fillWithFluid(rock.getBlock(Rock.BlockType.LOOSE).defaultBlockState(), stateAt.getFluidState().getType());

        if (FluidHelpers.isAirOrEmptyFluid(stateAt) && rockState != null && rockState.canSurvive(worldIn, pos))
        {
            setBlock(worldIn, pos, rockState.setValue(TFCBlockStateProperties.COUNT_1_3, 1 + rand.nextInt(2)));
            return true;
        }
        return false;
    }
}
