/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class PannedCondition implements LootItemCondition
{
    public static final PannedCondition INSTANCE = new PannedCondition();

    private PannedCondition() {}

    @Override
    public LootItemConditionType getType()
    {
        return TFCLoot.IS_PANNED.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return context.hasParam(TFCLoot.PANNED);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<PannedCondition>
    {
        @Override
        public void serialize(JsonObject json, PannedCondition condition, JsonSerializationContext context) {}

        @Override
        public PannedCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            return PannedCondition.INSTANCE;
        }
    }
}
