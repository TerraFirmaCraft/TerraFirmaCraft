/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.util.Helpers;

public class TFCLoot
{
    // Loot Parameters
    public static final LootContextParam<Boolean> ISOLATED = new LootContextParam<>(Helpers.identifier("isolated"));
    public static final LootContextParam<Boolean> PANNED = new LootContextParam<>(Helpers.identifier("panned"));

    // Loot Conditions
    public static final Lazy<LootItemConditionType> IS_ISOLATED = register("is_isolated", IsIsolatedCondition.Serializer::new);
    public static final Lazy<LootItemConditionType> IS_PANNED = register("is_panned", PannedCondition.Serializer::new);

    public static void registerLootConditions()
    {
        IS_ISOLATED.get();
        IS_PANNED.get();
    }

    private static Lazy<LootItemConditionType> register(String id, Supplier<Serializer<? extends LootItemCondition>> serializer)
    {
        return Lazy.of(() -> Registry.register(Registry.LOOT_CONDITION_TYPE, Helpers.identifier(id), new LootItemConditionType(serializer.get())));
    }
}
