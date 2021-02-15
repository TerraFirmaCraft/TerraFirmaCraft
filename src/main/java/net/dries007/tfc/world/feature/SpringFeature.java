/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.LiquidsConfig;

import com.mojang.serialization.Codec;

/**
 * A cleaned up version of {  net.minecraft.world.gen.feature.SpringFeature}
 */
public class SpringFeature extends Feature<LiquidsConfig>
{
    public SpringFeature(Codec<LiquidsConfig> codec)
    {
        super(codec);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean generate(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos pos, LiquidsConfig config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable().setPos(pos).move(0, 1, 0);
        final BlockState stateAbove = worldIn.getBlockState(mutablePos);
        if (config.acceptedBlocks.contains(stateAbove.getBlock()))
        {
            mutablePos.move(0, -2, 0);
            final BlockState stateBelow = worldIn.getBlockState(mutablePos);
            if (!config.needsBlockBelow || config.acceptedBlocks.contains(stateBelow.getBlock()))
            {
                final BlockState stateAt = worldIn.getBlockState(pos);
                if (stateAt.isAir() || config.acceptedBlocks.contains(stateAt.getBlock()))
                {
                    int rockCount = 0, holeCount = 0;
                    for (Direction direction : Direction.values())
                    {
                        mutablePos.setPos(pos).move(direction);
                        final BlockState stateAdjacent = worldIn.getBlockState(mutablePos);
                        if (config.acceptedBlocks.contains(stateAdjacent.getBlock()))
                        {
                            rockCount++;
                        }
                        if (stateAdjacent.isAir())
                        {
                            holeCount++;
                        }
                    }

                    if (rockCount == config.rockAmount && holeCount == config.holeAmount)
                    {
                        worldIn.setBlockState(pos, config.state.getBlockState(), 2);
                        worldIn.getPendingFluidTicks().scheduleTick(pos, config.state.getFluid(), 0);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
