/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import com.mojang.serialization.Codec;

public class FlatEnoughDecorator extends FeatureDecorator<FlatEnoughConfig>
{
    public FlatEnoughDecorator(Codec<FlatEnoughConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext worldIn, Random random, FlatEnoughConfig config, BlockPos pos)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < config.maxDepth(); y++)
        {
            if (isFlatEnough(worldIn, pos, -y, mutablePos, config))
            {
                return Stream.of(pos.offset(0, -y, 0));
            }
        }
        return Stream.empty();
    }

    private boolean isFlatEnough(DecorationContext worldIn, BlockPos pos, int y, BlockPos.MutableBlockPos mutablePos, FlatEnoughConfig config)
    {
        int flatAmount = 0;
        for (int x = -config.radius(); x <= config.radius(); x++)
        {
            for (int z = -config.radius(); z <= config.radius(); z++)
            {
                mutablePos.set(pos).move(x, y, z);
                BlockState stateAt = worldIn.getBlockState(mutablePos);
                if (!stateAt.isAir() && stateAt.getFluidState().getType() == Fluids.EMPTY) // No direct access to world, cannot use forge method
                {
                    flatAmount++;
                }
            }
        }
        return flatAmount / ((1f + 2 * config.radius()) * (1f + 2 * config.radius())) > config.flatness();
    }
}
