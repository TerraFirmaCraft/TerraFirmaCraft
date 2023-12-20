/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.blockpredicate;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.util.EnvironmentHelpers;

public enum DryReplaceablePredicate implements BlockPredicate
{
    INSTANCE;

    public static final Codec<DryReplaceablePredicate> CODEC = Codec.unit(INSTANCE);

    @Override
    public BlockPredicateType<?> type()
    {
        return TFCBlockPredicates.DRY_REPLACEABLE.get();
    }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos)
    {
        return EnvironmentHelpers.isWorldgenReplaceable(level, pos) && level.getFluidState(pos).getType().isSame(Fluids.EMPTY);
    }
}
