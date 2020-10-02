package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
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
        for (int y = 0; y >= -2; y--)
        {
            if (isFlatEnough(worldIn, pos, y, config))
            {
                return Stream.of(pos.offset(0, y, 0));
            }
        }
        return Stream.empty();
    }

    @SuppressWarnings("deprecation")
    private boolean isFlatEnough(WorldDecoratingHelper worldIn, BlockPos pos, int y, FlatEnoughConfig config)
    {
        int flatAmount = 0;
        for (int x = -config.radius; x <= config.radius; x++)
        {
            for (int z = -config.radius; z <= config.radius; z++)
            {
                BlockPos posAt = pos.offset(x, y, z);
                BlockPos posDown = posAt.below();
                BlockState stateAt = worldIn.getBlockState(posAt);
                BlockState stateDown = worldIn.getBlockState(posDown);
                if (stateDown.canOcclude() && stateAt.isAir()) // No direct access to world, cannot use forge method
                {
                    flatAmount++;
                }
            }
        }
        return flatAmount / (1f + 2 * config.radius) > config.flatness;
    }
}
