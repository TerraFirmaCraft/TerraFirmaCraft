/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

/**
 * Places a single loose rock at the target position
 */
public class LooseRockFeature extends Feature<NoFeatureConfig>
{
    public LooseRockFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.ROCKS);
        final Rock rock = data.getRockData().getRock(pos.getX(), pos.getY(), pos.getZ());
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
