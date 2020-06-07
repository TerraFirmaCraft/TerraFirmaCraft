/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class TypedDataManager<T> extends DataManager<T>
{
    protected final Map<ResourceLocation, BiFunction<ResourceLocation, JsonObject, ? extends T>> deserializers;

    public TypedDataManager(Gson gson, String domain, String typeName)
    {
        super(gson, domain, typeName);

        this.deserializers = new HashMap<>();
    }

    public void register(ResourceLocation name, BiFunction<ResourceLocation, JsonObject, ? extends T> deserializer)
    {
        if (!deserializers.containsKey(name))
        {
            LOGGER.info("Registered {}: {}", typeName, name);
            deserializers.put(name, deserializer);
        }
        else
        {
            LOGGER.info("Denied registration of {}: {} as it would overwrite an existing entry!", typeName, name);
        }
    }

    @Override
    protected T read(ResourceLocation id, JsonObject json)
    {
        ResourceLocation type;
        if (json.has("type"))
        {
            type = new ResourceLocation(JSONUtils.getString(json, "type"));
        }
        else
        {
            type = getFallbackType();
            if (type == null)
            {
                throw new JsonParseException("missing type id, and this deserializer does not have a fallback type!");
            }
        }
        if (deserializers.containsKey(type))
        {
            return deserializers.get(type).apply(id, json);
        }
        else
        {
            throw new JsonParseException("Unknown " + typeName + ": " + type);
        }
    }

    @Nullable
    protected ResourceLocation getFallbackType()
    {
        return null;
    }
}
