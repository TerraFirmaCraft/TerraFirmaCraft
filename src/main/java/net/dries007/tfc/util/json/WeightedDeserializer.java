package net.dries007.tfc.util.json;

import java.lang.reflect.Type;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.collections.Weighted;

public class WeightedDeserializer<T> implements JsonDeserializer<IWeighted<T>>
{
    private final Class<T> elementClass;

    public WeightedDeserializer(Class<T> elementClass)
    {
        this.elementClass = elementClass;
    }

    @Override
    public IWeighted<T> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException
    {
        if (json.isJsonPrimitive() || json.isJsonObject())
        {
            T state = context.deserialize(json, elementClass);
            return IWeighted.singleton(state);
        }
        else if (json.isJsonArray())
        {
            JsonArray array = json.getAsJsonArray();
            IWeighted<T> states = new Weighted<>();
            for (JsonElement element : array)
            {
                if (element.isJsonObject())
                {
                    JsonObject obj = element.getAsJsonObject();
                    float weight = JSONUtils.getFloat(obj, "weight", 1);
                    states.add(weight, context.deserialize(element, elementClass));
                }
                else
                {
                    states.add(1, context.deserialize(element, elementClass));
                }
            }
            return states;
        }
        throw new JsonParseException("Unable to parse Weighted List of " + elementClass.getSimpleName());
    }
}
