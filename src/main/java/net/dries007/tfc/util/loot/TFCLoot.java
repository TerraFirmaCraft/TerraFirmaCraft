/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCLoot
{
    public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, MOD_ID);
    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister.create(Registry.LOOT_NUMBER_PROVIDER_REGISTRY, MOD_ID);
    public static final LootContextParam<Boolean> ISOLATED = new LootContextParam<>(Helpers.identifier("isolated"));
    public static final LootContextParam<Boolean> PANNED = new LootContextParam<>(Helpers.identifier("panned"));

    // Loot Conditions
    public static final RegistryObject<LootItemConditionType> IS_PANNED = lootCondition("is_panned", new PannedCondition.Serializer());
    public static final RegistryObject<LootItemConditionType> IS_ISOLATED = lootCondition("is_isolated", new IsIsolatedCondition.Serializer());
    public static final RegistryObject<LootNumberProviderType> CROP_YIELD = numberProvider("crop_yield_uniform", new CropYieldProvider.Serializer());

    private static RegistryObject<LootItemConditionType> lootCondition(String id, Serializer<? extends LootItemCondition> serializer)
    {
        return CONDITIONS.register(id, () -> new LootItemConditionType(serializer));
    }

    private static RegistryObject<LootNumberProviderType> numberProvider(String id, Serializer<? extends NumberProvider> serializer)
    {
        return NUMBER_PROVIDERS.register(id, () -> new LootNumberProviderType(serializer));
    }

    public static void registerAll(IEventBus bus)
    {
        CONDITIONS.register(bus);
        NUMBER_PROVIDERS.register(bus);
    }
}
