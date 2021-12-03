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
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class NearWaterDecorator extends PlacementModifier
{
    public static final Codec<NearWaterDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.NONNEGATIVE_INT.optionalFieldOf("radius", 2).forGetter(c -> c.radius)
    ).apply(instance, NearWaterDecorator::new));

    private final int radius;

    public NearWaterDecorator(int radius)
    {
        this.radius = radius;
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCDecorators.NEAR_WATER.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 0; y >= -radius; y--)
                {
                    mutablePos.set(pos).move(x, y, z);

                    final BlockState state = context.getBlockState(mutablePos);
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
