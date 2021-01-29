/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import com.mojang.serialization.Codec;

public class FlatEnoughDecorator extends Placement<FlatEnoughConfig>
{
    public FlatEnoughDecorator(Codec<FlatEnoughConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper worldIn, Random random, FlatEnoughConfig config, BlockPos pos)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int y = 0; y < config.maxDepth; y++)
        {
            if (isFlatEnough(worldIn, pos, -y, mutablePos, config))
            {
                return Stream.of(pos.offset(0, -y, 0));
            }
        }
        return Stream.empty();
    }

    @SuppressWarnings("deprecation")
    private boolean isFlatEnough(WorldDecoratingHelper worldIn, BlockPos pos, int y, BlockPos.Mutable mutablePos, FlatEnoughConfig config)
    {
        int flatAmount = 0;
        for (int x = -config.radius; x <= config.radius; x++)
        {
            for (int z = -config.radius; z <= config.radius; z++)
            {
                mutablePos.set(pos).move(x, y, z);
                BlockState stateAt = worldIn.getBlockState(mutablePos);
                if (!stateAt.isAir() && stateAt.getFluidState().getType() == Fluids.EMPTY) // No direct access to world, cannot use forge method
                {
                    flatAmount++;
                }
            }
        }
        return flatAmount / ((1f + 2 * config.radius) * (1f + 2 * config.radius)) > config.flatness;
    }
}
