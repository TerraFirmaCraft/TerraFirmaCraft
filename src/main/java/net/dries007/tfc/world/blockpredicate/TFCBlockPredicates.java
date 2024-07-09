/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.blockpredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class TFCBlockPredicates
{
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATES = DeferredRegister.create(Registries.BLOCK_PREDICATE_TYPE, MOD_ID);

    public static final Id<AirOrEmptyFluidPredicate> AIR_OR_EMPTY_FLUID = register("air_or_empty_fluid", AirOrEmptyFluidPredicate.CODEC);
    public static final Id<WouldSurviveWithFluidPredicate> WOULD_SURVIVE_WITH_FLUID = register("would_survive_with_fluid", WouldSurviveWithFluidPredicate.CODEC);
    public static final Id<ReplaceablePredicate> REPLACEABLE = register("replaceable", ReplaceablePredicate.CODEC);
    public static final Id<DryReplaceablePredicate> DRY_REPLACEABLE = register("dry_replaceable", DryReplaceablePredicate.CODEC);

    private static <T extends BlockPredicate> Id<T> register(String name, MapCodec<T> codec)
    {
        return new Id<>(BLOCK_PREDICATES.register(name, () -> () -> codec));
    }

    public record Id<T extends BlockPredicate>(DeferredHolder<BlockPredicateType<?>, BlockPredicateType<T>> holder)
        implements RegistryHolder<BlockPredicateType<?>, BlockPredicateType<T>> {}
}
