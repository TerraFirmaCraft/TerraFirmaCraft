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
import net.dries007.tfc.util.EnvironmentHelpers;

public enum ReplaceablePredicate implements BlockPredicate
{
    INSTANCE;

    public static final Codec<ReplaceablePredicate> CODEC = Codec.unit(INSTANCE);

    @Override
    public BlockPredicateType<?> type()
    {
        return TFCBlockPredicates.REPLACEABLE.get();
    }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos)
    {
        return EnvironmentHelpers.isWorldgenReplaceable(level, pos);
    }
}
