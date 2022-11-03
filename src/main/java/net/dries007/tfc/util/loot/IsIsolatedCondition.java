/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public enum IsIsolatedCondition implements LootItemCondition
{
    INSTANCE;

    @Override
    public LootItemConditionType getType()
    {
        return TFCLoot.IS_ISOLATED.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return context.hasParam(TFCLoot.ISOLATED);
    }
}
