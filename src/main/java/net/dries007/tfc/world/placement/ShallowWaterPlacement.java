/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.stream.Stream;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public class ShallowWaterPlacement extends PlacementModifier
{
    public static final MapCodec<ShallowWaterPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codecs.POSITIVE_INT.optionalFieldOf("min_depth", 0).forGetter(c -> c.minDepth),
        Codecs.POSITIVE_INT.optionalFieldOf("max_depth", 5).forGetter(c -> c.maxDepth)
    ).apply(instance, ShallowWaterPlacement::new));

    private final int minDepth;
    private final int maxDepth;

    public ShallowWaterPlacement(int minDepth, int maxDepth)
    {
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        for (int i = 0; i < maxDepth; i++)
        {
            mutablePos.move(0, -1, 0);
            if (!context.getLevel().isFluidAtPosition(mutablePos, state -> Helpers.isFluid(state, TFCTags.Fluids.ANY_INFINITE_WATER)))
            {
                if (i < minDepth)
                {
                    return Stream.empty();
                }
                return random.nextFloat() > (double) i / maxDepth ? Stream.of(pos) : Stream.empty();
            }
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.SHALLOW_WATER.get();
    }
}
