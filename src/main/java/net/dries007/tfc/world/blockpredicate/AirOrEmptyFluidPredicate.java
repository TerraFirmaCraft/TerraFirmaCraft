/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.blockpredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.fluids.FluidHelpers;

public enum AirOrEmptyFluidPredicate implements BlockPredicate
{
    INSTANCE;

    public static final Codec<AirOrEmptyFluidPredicate> CODEC = Codec.unit(INSTANCE);

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos)
    {
        return FluidHelpers.isAirOrEmptyFluid(level.getBlockState(pos));
    }

    @Override
    public BlockPredicateType<?> type()
    {
        return TFCBlockPredicates.AIR_OR_EMPTY_FLUID.get();
    }
}
