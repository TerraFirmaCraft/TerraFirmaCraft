/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
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
        final BlockState state = getStateToPlace(rock.getBlock(Rock.BlockType.LOOSE).defaultBlockState(), stateAt);

        if (state != null && state.canSurvive(worldIn, pos))
        {
            setBlock(worldIn, pos, state.setValue(TFCBlockStateProperties.COUNT_1_3, 1 + rand.nextInt(2)));
            return true;
        }
        return false;
    }

    @Nullable
    @SuppressWarnings("deprecation")
    private BlockState getStateToPlace(BlockState state, BlockState stateAt)
    {
        if (stateAt.isAir())
        {
            return state;
        }
        if (state.getBlock() instanceof IFluidLoggable)
        {
            final FluidProperty property = ((IFluidLoggable) state.getBlock()).getFluidProperty();
            final Fluid fluid = stateAt.getFluidState().getType();
            if (property.canContain(fluid) && fluid.isSame(Fluids.EMPTY))
            {
                return state.setValue(property, property.keyFor(fluid));
            }
        }
        return null;
    }
}
