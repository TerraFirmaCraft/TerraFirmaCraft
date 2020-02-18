package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.*;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@ParametersAreNonnullByDefault
public abstract class TFCTypeDeserializer<T> implements JsonDeserializer<T>
{
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (!json.isJsonObject())
        {
            throw new JsonParseException("Type object must be JSON object.");
        }
        JsonObject obj = json.getAsJsonObject();
        return create(obj);
    }

    protected abstract T create(JsonObject obj);

    protected <K, V extends IForgeRegistryEntry<V>> Map<K, V> findRegistryObjects(JsonObject obj, String path, IForgeRegistry<V> registry, K[] keyValues, NonNullFunction<K, String> keyStringMapper)
    {
        if (obj.has(path))
        {
            Map<K, V> objects = new HashMap<>();
            JsonObject objectsJson = JSONUtils.getJsonObject(obj, path);
            for (K expectedKey : keyValues)
            {
                String jsonKey = keyStringMapper.apply(expectedKey);
                ResourceLocation blockId = new ResourceLocation(JSONUtils.getString(objectsJson, jsonKey));
                V block = registry.getValue(blockId);
                if (block == null)
                {
                    throw new JsonParseException("Unknown block: " + blockId);
                }
                objects.put(expectedKey, block);
            }
            return objects;
        }
        return Collections.emptyMap();
    }
}
