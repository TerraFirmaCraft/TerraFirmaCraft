/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.blockpredicate;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraftforge.common.util.Lazy;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

public class TFCBlockPredicates
{
    public static final Supplier<BlockPredicateType<AirOrEmptyFluidPredicate>> AIR_OR_EMPTY_FLUID = register("air_or_empty_fluid", AirOrEmptyFluidPredicate.CODEC);
    public static final Supplier<BlockPredicateType<WouldSurviveWithFluidPredicate>> WOULD_SURVIVE_WITH_FLUID = register("would_survive_with_fluid", WouldSurviveWithFluidPredicate.CODEC);

    public static void registerBlockPredicates()
    {
        AIR_OR_EMPTY_FLUID.get();
        WOULD_SURVIVE_WITH_FLUID.get();
    }

    private static <T extends BlockPredicate> Supplier<BlockPredicateType<T>> register(String name, Codec<T> codec)
    {
        return Lazy.of(() -> Registry.register(Registry.BLOCK_PREDICATE_TYPES, Helpers.identifier(name), () -> codec));
    }
}
