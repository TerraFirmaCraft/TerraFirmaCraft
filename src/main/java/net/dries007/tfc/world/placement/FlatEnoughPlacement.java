/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class FlatEnoughPlacement extends PlacementModifier
{
    public static final Codec<FlatEnoughPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.floatRange(0, 1).optionalFieldOf("flatness", 0.5f).forGetter(c -> c.flatness),
        Codecs.POSITIVE_INT.optionalFieldOf("radius", 2).forGetter(c -> c.radius),
        Codecs.POSITIVE_INT.optionalFieldOf("max_depth", 4).forGetter(c -> c.maxDepth)
    ).apply(instance, FlatEnoughPlacement::new));

    private final float flatness;
    private final int radius;
    private final int maxDepth;

    public FlatEnoughPlacement(float flatness, int radius, int maxDepth)
    {
        this.flatness = flatness;
        this.radius = radius;
        this.maxDepth = maxDepth;
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.FLAT_ENOUGH.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < maxDepth; y++)
        {
            if (isFlatEnough(context, pos, -y, mutablePos))
            {
                return Stream.of(pos.offset(0, -y, 0));
            }
        }
        return Stream.empty();
    }

    private boolean isFlatEnough(PlacementContext level, BlockPos pos, int y, BlockPos.MutableBlockPos mutablePos)
    {
        int flatAmount = 0;
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                mutablePos.set(pos).move(x, y, z);
                BlockState stateAt = level.getBlockState(mutablePos);
                if (!stateAt.isAir() && stateAt.getFluidState().getType() == Fluids.EMPTY) // No direct access to world, cannot use forge method
                {
                    flatAmount++;
                }
            }
        }
        return flatAmount / ((1f + 2 * radius) * (1f + 2 * radius)) > flatness;
    }
}
