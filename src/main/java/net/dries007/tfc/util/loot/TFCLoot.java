/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.core.Registry;

import net.dries007.tfc.util.Helpers;

public class TFCLoot
{
    public static final LootContextParam<Boolean> ISOLATED = new LootContextParam<>(Helpers.identifier("isolated"));

    public static final LootItemConditionType IS_ISOLATED = register("is_isolated", new IsIsolatedCondition.Serializer());

    public static void setup() {}

    private static LootItemConditionType register(String id, Serializer<? extends LootItemCondition> serializer)
    {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, Helpers.identifier(id), new LootItemConditionType(serializer));
    }
}
