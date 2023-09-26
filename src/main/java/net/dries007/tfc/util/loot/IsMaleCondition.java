/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;

public enum IsMaleCondition implements LootItemCondition
{
    INSTANCE;

    @Override
    public LootItemConditionType getType()
    {
        return TFCLoot.IS_MALE.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return context.hasParam(LootContextParams.THIS_ENTITY) && context.getParam(LootContextParams.THIS_ENTITY) instanceof TFCAnimalProperties properties && properties.isMale();
    }


}
