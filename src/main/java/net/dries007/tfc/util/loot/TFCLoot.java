/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.function.Supplier;

import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.LazyRegistry;

public class TFCLoot
{
    // Loot Parameters
    public static final LootParameter<Boolean> ISOLATED = new LootParameter<>(Helpers.identifier("isolated"));

    // Loot Conditions
    public static final LazyRegistry<LootConditionType> LOOT_CONDITIONS = new LazyRegistry<>();
    public static final Lazy<LootConditionType> IS_ISOLATED = register("is_isolated", IsIsolatedCondition.Serializer::new);

    private static Lazy<LootConditionType> register(String id, Supplier<ILootSerializer<? extends ILootCondition>> serializer)
    {
        return LOOT_CONDITIONS.register(() -> {
            final LootConditionType type = new LootConditionType(serializer.get());
            Registry.register(Registry.LOOT_CONDITION_TYPE, Helpers.identifier(id), type);
            return type;
        });
    }
}
