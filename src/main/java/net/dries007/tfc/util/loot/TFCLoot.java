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

    public static LootItemConditionType IS_ISOLATED;
    public static LootNumberProviderType CROP_YIELD;

    public static void registerAll()
    {
        IS_ISOLATED = lootCondition("is_isolated", new IsIsolatedCondition.Serializer());
        CROP_YIELD = numberProvider("crop_yield_uniform", new CropYieldProvider.Serializer());
    }

    private static LootItemConditionType lootCondition(String id, Serializer<? extends LootItemCondition> serializer)
    {
        return register(id, serializer, LootItemConditionType::new, Registry.LOOT_CONDITION_TYPE);
    }

    private static LootNumberProviderType numberProvider(String id, Serializer<? extends NumberProvider> serializer)
    {
        return Lazy.of(() -> Registry.register(Registry.LOOT_CONDITION_TYPE, Helpers.identifier(id), new LootItemConditionType(serializer.get())));
        return register(id, serializer, LootNumberProviderType::new, Registry.LOOT_NUMBER_PROVIDER_TYPE);
    }

    private static <T, S> T register(String id, S serializer, Function<S, T> typeFactory, Registry<T> registry)
    {
        return Registry.register(registry, Helpers.identifier(id), typeFactory.apply(serializer));
    }
}
