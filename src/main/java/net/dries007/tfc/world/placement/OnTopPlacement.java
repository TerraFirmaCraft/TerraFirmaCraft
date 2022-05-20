/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class OnTopPlacement extends PlacementModifier
{
    public static final Codec<OnTopPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockPredicate.CODEC.fieldOf("predicate").forGetter(c -> c.predicate)
    ).apply(instance, OnTopPlacement::new));

    private final BlockPredicate predicate;

    public OnTopPlacement(BlockPredicate predicate)
    {
        this.predicate = predicate;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos)
    {
        return predicate.test(context.getLevel(), pos.below()) ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.ON_TOP.get();
    }
}
