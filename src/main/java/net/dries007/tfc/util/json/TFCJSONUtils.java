package net.dries007.tfc.util.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.BlockState;
import net.minecraft.util.JSONUtils;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.util.collections.Weighted;

/**
 * @see net.minecraft.util.JSONUtils
 */
public class TFCJSONUtils
{
    public static BlockState getBlockState(JsonElement json)
    {
        if (json.isJsonObject())
        {
            return Helpers.readBlockState(JSONUtils.getString(json.getAsJsonObject(), "block"), true);
        }
        else
        {
            return Helpers.readBlockState(json.getAsString(), true);
        }
    }

    public static <T> List<T> getListLenient(JsonElement json, Function<JsonElement, T> elementDeserializer)
    {
        return getCollectionLenient(json, elementDeserializer, Collections::singletonList, ArrayList::new);
    }

    public static <T, E extends Collection<T>> E getCollectionLenient(JsonElement json, Function<JsonElement, T> elementDeserializer, Function<T, E> singletonSupplier, Function<Collection<T>, E> listSupplier)
    {
        if (json.isJsonArray())
        {
            JsonArray array = json.getAsJsonArray();
            List<T> elements = new ArrayList<>(array.size());
            for (JsonElement element : array)
            {
                elements.add(elementDeserializer.apply(element));
            }
            return listSupplier.apply(elements);
        }
        else
        {
            return singletonSupplier.apply(elementDeserializer.apply(json));
        }
    }

    public static <T> IWeighted<T> getWeighted(JsonElement json, Function<JsonElement, T> elementDeserializer)
    {
        if (json.isJsonPrimitive() || json.isJsonObject())
        {
            return IWeighted.singleton(elementDeserializer.apply(json));
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
                    states.add(weight, elementDeserializer.apply(obj));
                }
                else
                {
                    states.add(1, elementDeserializer.apply(element));
                }
            }
            return states;
        }
        throw new JsonParseException("Weighted list must be single object or array");
    }
}
