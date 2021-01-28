/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;

public class IsIsolatedCondition implements ILootCondition
{
    public static final IsIsolatedCondition INSTANCE = new IsIsolatedCondition();

    private IsIsolatedCondition() {}

    @Override
    public LootConditionType getType()
    {
        return TFCLoot.IS_ISOLATED;
    }

    @Override
    public boolean test(LootContext context)
    {
        return context.hasParam(TFCLoot.ISOLATED);
    }

    public static class Serializer implements ILootSerializer<IsIsolatedCondition>
    {
        @Override
        public void serialize(JsonObject json, IsIsolatedCondition condition, JsonSerializationContext context) {}

        @Override
        public IsIsolatedCondition deserialize(JsonObject json, JsonDeserializationContext context)
        {
            return IsIsolatedCondition.INSTANCE;
        }
    }
}
