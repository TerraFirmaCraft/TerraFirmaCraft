/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class TFCLoot
{
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MOD_ID);
    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, MOD_ID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MOD_ID);

    public static final LootContextParam<Boolean> ISOLATED = new LootContextParam<>(Helpers.identifier("isolated"));
    public static final LootContextParam<Boolean> BURNT_OUT = new LootContextParam<>(Helpers.identifier("burnt_out"));

    public static final Id<LootItemConditionType> IS_ISOLATED = lootCondition("is_isolated", new InstanceSerializer<>(IsIsolatedCondition.INSTANCE));
    public static final Id<LootItemConditionType> IS_BURNT_OUT = lootCondition("is_burnt_out", new InstanceSerializer<>(IsBurntOutCondition.INSTANCE));
    public static final Id<LootItemConditionType> IS_MALE = lootCondition("is_male", new InstanceSerializer<>(IsMaleCondition.INSTANCE));
    public static final Id<LootItemConditionType> ALWAYS_TRUE = lootCondition("always_true", new InstanceSerializer<>(AlwaysTrueCondition.INSTANCE));
    public static final Id<LootItemConditionType> NOT_PREDATED = lootCondition("not_predated", new InstanceSerializer<>(NotPredatedCondition.INSTANCE));
    public static final Id<LootNumberProviderType> CROP_YIELD = numberProvider("crop_yield_uniform", new MinMaxProvider.Serializer(CropYieldProvider::new));
    public static final Id<LootNumberProviderType> ANIMAL_YIELD = numberProvider("animal_yield", new MinMaxProvider.Serializer(AnimalYieldProvider::new));
    public static final Id<LootItemFunctionType<?>> COPY_FLUID = lootFunction("copy_fluid", new CopyFluidFunction.Serializer());
    public static final Id<LootItemFunctionType<?>> ROTTEN = lootFunction("rotten", new RottenFunction.Serializer());

    private static Id<LootItemFunctionType<?>> lootFunction(String id, Serializer<? extends LootItemFunction> serializer)
    {
        return LOOT_FUNCTIONS.register(id, () -> new LootItemFunctionType<>(serializer));
    }

    private static Id<LootItemConditionType> lootCondition(String id, Serializer<? extends LootItemCondition> serializer)
    {
        return CONDITIONS.register(id, () -> new LootItemConditionType(serializer));
    }

    private static Id<LootNumberProviderType> numberProvider(String id, Serializer<? extends NumberProvider> serializer)
    {
        return NUMBER_PROVIDERS.register(id, () -> new LootNumberProviderType(serializer));
    }

    public record Id<T>(DeferredHolder<T, T> holder) implements RegistryHolder<T, T> {}

    public record InstanceSerializer<T extends LootItemCondition>(T instance) implements Serializer<T>
    {
        @Override
        public void serialize(JsonObject json, T value, JsonSerializationContext context) { }

        @Override
        public T deserialize(JsonObject json, JsonDeserializationContext context)
        {
            return instance;
        }
    }
}
