/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.blockpredicate;

import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBlockPredicates
{
    public static final DeferredRegister<BlockPredicateType<?>> BLOCK_PREDICATES = DeferredRegister.create(Registry.BLOCK_PREDICATE_TYPE_REGISTRY, MOD_ID);

    public static final RegistryObject<BlockPredicateType<AirOrEmptyFluidPredicate>> AIR_OR_EMPTY_FLUID = register("air_or_empty_fluid", () -> AirOrEmptyFluidPredicate.CODEC);
    public static final RegistryObject<BlockPredicateType<WouldSurviveWithFluidPredicate>> WOULD_SURVIVE_WITH_FLUID = register("would_survive_with_fluid", () -> WouldSurviveWithFluidPredicate.CODEC);
    public static final RegistryObject<BlockPredicateType<ReplaceablePredicate>> REPLACEABLE = register("replaceable", () -> ReplaceablePredicate.CODEC);

    private static <T extends BlockPredicate> RegistryObject<BlockPredicateType<T>> register(String name, BlockPredicateType<T> codec)
    {
        return BLOCK_PREDICATES.register(name, () -> codec);
    }
}
