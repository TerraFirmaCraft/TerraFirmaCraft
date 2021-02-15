/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import com.mojang.serialization.Codec;

public class NearWaterDecorator extends Placement<NearWaterConfig>
{
    public NearWaterDecorator(Codec<NearWaterConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random random, NearWaterConfig config, BlockPos pos)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final int radius = config.getRadius();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 0; y >= -radius; y--)
                {
                    mutablePos.setPos(pos).move(x, y, z);

                    final BlockState state = helper.getBlockState(mutablePos);
                    if (state.getFluidState().isIn(FluidTags.WATER))
                    {
                        return Stream.of(pos);
                    }
                }
            }
        }
        return Stream.empty();
    }
}
