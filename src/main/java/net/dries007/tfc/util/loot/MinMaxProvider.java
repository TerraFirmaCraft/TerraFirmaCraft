/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.function.BiFunction;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public abstract class MinMaxProvider implements NumberProvider
{
    protected final NumberProvider min, max;

    public MinMaxProvider(NumberProvider min, NumberProvider max)
    {
        this.min = min;
        this.max = max;
    }

    public record Serializer(BiFunction<NumberProvider, NumberProvider, MinMaxProvider> factory) implements net.minecraft.world.level.storage.loot.Serializer<MinMaxProvider>
    {
        @Override
        public void serialize(JsonObject json, MinMaxProvider value, JsonSerializationContext context)
        {
            json.add("min", context.serialize(value.min));
            json.add("max", context.serialize(value.max));
        }

        @Override
        public MinMaxProvider deserialize(JsonObject json, JsonDeserializationContext context)
        {
            final NumberProvider min = GsonHelper.getAsObject(json, "min", context, NumberProvider.class);
            final NumberProvider max = GsonHelper.getAsObject(json, "max", context, NumberProvider.class);
            return factory.apply(min, max);
        }
    }
}
