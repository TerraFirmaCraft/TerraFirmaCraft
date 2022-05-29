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

public class AlwaysTrueCondition implements LootItemCondition
{
    public static final AlwaysTrueCondition INSTANCE = new AlwaysTrueCondition();

    private AlwaysTrueCondition() {}

    @Override
    public LootItemConditionType getType()
    {
        return TFCLoot.ALWAYS_TRUE.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return true;
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AlwaysTrueCondition>
    {
        @Override
        public void serialize(JsonObject json, AlwaysTrueCondition condition, JsonSerializationContext context) {}

        @Override
        public AlwaysTrueCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            return AlwaysTrueCondition.INSTANCE;
        }
    }
}
