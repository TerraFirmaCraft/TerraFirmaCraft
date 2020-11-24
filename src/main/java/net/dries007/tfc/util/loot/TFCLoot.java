package net.dries007.tfc.util.loot;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.registry.Registry;

import net.dries007.tfc.util.Helpers;

public class TFCLoot
{
    public static final LootParameter<Boolean> ISOLATED = new LootParameter<>(Helpers.identifier("isolated"));

    public static final LootConditionType IS_ISOLATED = register("is_isolated", new IsIsolatedCondition.Serializer());

    public static void setup() {}

    private static LootConditionType register(String id, ILootSerializer<? extends ILootCondition> serializer)
    {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, Helpers.identifier(id), new LootConditionType(serializer));
    }
}
