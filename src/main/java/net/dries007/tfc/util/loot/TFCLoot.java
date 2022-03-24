/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.util.Helpers;

public class TFCLoot
{
    public static final LootContextParam<Boolean> ISOLATED = new LootContextParam<>(Helpers.identifier("isolated"));
    public static final LootContextParam<Boolean> PANNED = new LootContextParam<>(Helpers.identifier("panned"));

    // Loot Conditions
    public static final Supplier<LootItemConditionType> IS_PANNED = lootCondition("is_panned", new PannedCondition.Serializer());
    public static final Supplier<LootItemConditionType> IS_ISOLATED = lootCondition("is_isolated", new IsIsolatedCondition.Serializer());
    public static final Supplier<LootNumberProviderType> CROP_YIELD = numberProvider("crop_yield_uniform", new CropYieldProvider.Serializer());

    public static void registerLootSerializers()
    {
        IS_ISOLATED.get();
        IS_PANNED.get();
        CROP_YIELD.get();
    }

    private static Supplier<LootItemConditionType> lootCondition(String id, Serializer<? extends LootItemCondition> serializer)
    {
        return register(id, serializer, LootItemConditionType::new, Registry.LOOT_CONDITION_TYPE);
    }

    private static Supplier<LootNumberProviderType> numberProvider(String id, Serializer<? extends NumberProvider> serializer)
    {
        return register(id, serializer, LootNumberProviderType::new, Registry.LOOT_NUMBER_PROVIDER_TYPE);
    }

    private static <T, S> Supplier<T> register(String id, S instance, Function<S, T> typeFactory, Registry<T> registry)
    {
        return Lazy.of(() -> Registry.register(registry, Helpers.identifier(id), typeFactory.apply(instance)));
    }
}
