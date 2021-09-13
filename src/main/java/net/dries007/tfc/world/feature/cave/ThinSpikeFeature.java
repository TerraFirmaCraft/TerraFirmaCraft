/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.ThinSpikeBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;

public class ThinSpikeFeature extends Feature<ThinSpikeConfig>
{
    public ThinSpikeFeature(Codec<ThinSpikeConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ThinSpikeConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final ThinSpikeConfig config = context.config();

        final BlockState spike = config.state();
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean placedAny = false;

        for (int attempt = 0; attempt < config.tries(); attempt++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(config.radius()) - rand.nextInt(config.radius()), rand.nextInt(config.radius() - rand.nextInt(config.radius())), rand.nextInt(config.radius()) - rand.nextInt(config.radius()));

            // Move upwards to find a suitable spot
            for (int i = 0; i < 7; i++)
            {
                mutablePos.move(0, 1, 0);
                if (!FluidHelpers.isAirOrEmptyFluid(level.getBlockState(mutablePos)))
                {
                    mutablePos.move(0, -1, 0);
                    break;
                }
            }

            placedAny |= placeSpike(level, mutablePos, spike, rand, config);
        }
        return placedAny;
    }

    private boolean placeSpike(WorldGenLevel level, BlockPos.MutableBlockPos pos, BlockState spike, Random random, ThinSpikeConfig config)
    {
        // Place the first spike block
        if (!placeSpikeBlock(level, pos, spike))
        {
            return false;
        }

        // Continue downwards, until we reach max height, or we can't go any further
        final int height = config.getHeight(random);
        for (int i = 0; i < height; i++)
        {
            pos.move(0, -1, 0);
            if (!placeSpikeBlock(level, pos, spike))
            {
                // Could not place a spike at this position. Back up, and exit the loop to fix the tip.
                pos.move(0, 1, 0);
                break;
            }
        }

        // Add the tip, at the last valid position
        BlockState lastState = level.getBlockState(pos);
        if (lastState.getBlock() == spike.getBlock())
        {
            lastState = lastState.setValue(ThinSpikeBlock.TIP, true);
        }
        else
        {
            // wut
            lastState = Blocks.REDSTONE_LAMP.defaultBlockState();
        }
        level.setBlock(pos, lastState, 2);
        return true;
    }

    private boolean placeSpikeBlock(WorldGenLevel level, BlockPos pos, BlockState spike)
    {
        final BlockState state = level.getBlockState(pos);
        if (FluidHelpers.isAirOrEmptyFluid(state) && spike.canSurvive(level, pos))
        {
            final BlockState adjustedSpike = FluidHelpers.fillWithFluid(spike, state.getFluidState().getType());
            if (adjustedSpike != null)
            {
                level.setBlock(pos, spike, 2);
                return true;
            }
        }
        return false;
    }
}
