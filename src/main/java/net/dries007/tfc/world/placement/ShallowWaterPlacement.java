/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public class ShallowWaterPlacement extends PlacementModifier
{
    public static final Codec<ShallowWaterPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.POSITIVE_INT.optionalFieldOf("max_depth", 5).forGetter(c -> c.maxDepth)
    ).apply(instance, ShallowWaterPlacement::new));

    private final int maxDepth;

    public ShallowWaterPlacement(int maxDepth)
    {
        this.maxDepth = maxDepth;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext ctx, Random rand, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        for (int i = 0; i < maxDepth; i++)
        {
            mutablePos.move(Direction.DOWN);
            if (!ctx.getLevel().isFluidAtPosition(mutablePos, state -> Helpers.isFluid(state, FluidTags.WATER)))
            {
                return rand.nextFloat() > (double) i / maxDepth ? Stream.of(pos) : Stream.empty();
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
