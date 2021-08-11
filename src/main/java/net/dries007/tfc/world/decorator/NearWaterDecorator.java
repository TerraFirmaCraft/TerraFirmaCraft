/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import com.mojang.serialization.Codec;

public class NearWaterDecorator extends FeatureDecorator<NearWaterConfig>
{
    public NearWaterDecorator(Codec<NearWaterConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random random, NearWaterConfig config, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int radius = config.radius();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 0; y >= -radius; y--)
                {
                    mutablePos.set(pos).move(x, y, z);

                    final BlockState state = helper.getBlockState(mutablePos);
                    if (state.getFluidState().is(FluidTags.WATER))
                    {
                        return Stream.of(pos);
                    }
                }
            }
        }
        return Stream.empty();
    }
}
