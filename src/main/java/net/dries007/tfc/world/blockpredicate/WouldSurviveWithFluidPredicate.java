/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.blockpredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.fluids.FluidHelpers;

public record WouldSurviveWithFluidPredicate(Vec3i offset, BlockState state) implements BlockPredicate
{
    public static final Codec<WouldSurviveWithFluidPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(c -> c.offset),
        BlockState.CODEC.fieldOf("state").forGetter(c -> c.state)
    ).apply(instance, WouldSurviveWithFluidPredicate::new));

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos)
    {
        final BlockState stateWithFluid = FluidHelpers.fillWithFluid(state, level.getFluidState(pos).getType());
        return stateWithFluid != null && stateWithFluid.canSurvive(level, pos.offset(offset));
    }

    @Override
    public BlockPredicateType<?> type()
    {
        return TFCBlockPredicates.WOULD_SURVIVE_WITH_FLUID.get();
    }
}
