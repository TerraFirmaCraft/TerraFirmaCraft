/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;


import com.mojang.serialization.MapCodec;
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
    public static final LootContextParam<Boolean> SLUICE = new LootContextParam<>(Helpers.identifier("sluice"));

    public static final Id<LootItemConditionType> IS_ISOLATED = lootCondition("is_isolated", MapCodec.unit(IsIsolatedCondition.INSTANCE));
    public static final Id<LootItemConditionType> IS_BURNT_OUT = lootCondition("is_burnt_out", MapCodec.unit(IsBurntOutCondition.INSTANCE));
    public static final Id<LootItemConditionType> IS_SLUICE = lootCondition("is_sluice", MapCodec.unit(IsSluiceCondition.INSTANCE));
    public static final Id<LootItemConditionType> IS_MALE = lootCondition("is_male", MapCodec.unit(IsMaleCondition.INSTANCE));
    public static final Id<LootItemConditionType> ALWAYS_TRUE = lootCondition("always_true", MapCodec.unit(AlwaysTrueCondition.INSTANCE));
    public static final Id<LootItemConditionType> NOT_PREDATED = lootCondition("not_predated", MapCodec.unit(NotPredatedCondition.INSTANCE));
    public static final Id<LootNumberProviderType> CROP_YIELD = numberProvider("crop_yield_uniform", MinMaxProvider.codec(CropYieldProvider::new));
    public static final Id<LootNumberProviderType> ANIMAL_YIELD = numberProvider("animal_yield", MinMaxProvider.codec(AnimalYieldProvider::new));
    public static final LootFunctionId<CopyFluidFunction> COPY_FLUID = lootFunction("copy_fluid", CopyFluidFunction.CODEC);
    public static final LootFunctionId<RottenFunction> ROTTEN = lootFunction("rotten", RottenFunction.CODEC);

    private static <T extends LootItemFunction> LootFunctionId<T> lootFunction(String id, MapCodec<T> codec)
    {
        return new LootFunctionId<>(LOOT_FUNCTIONS.register(id, () -> new LootItemFunctionType<>(codec)));
    }

    private static Id<LootItemConditionType> lootCondition(String id, MapCodec<? extends LootItemCondition> codec)
    {
        return new Id<>(CONDITIONS.register(id, () -> new LootItemConditionType(codec)));
    }

    private static Id<LootNumberProviderType> numberProvider(String id, MapCodec<? extends NumberProvider> codec)
    {
        return new Id<>(NUMBER_PROVIDERS.register(id, () -> new LootNumberProviderType(codec)));
    }

    public record Id<T>(DeferredHolder<T, T> holder) implements RegistryHolder<T, T> {}
    public record LootFunctionId<T extends LootItemFunction>(DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<T>> holder)
        implements RegistryHolder<LootItemFunctionType<?>, LootItemFunctionType<T>> {}
}
