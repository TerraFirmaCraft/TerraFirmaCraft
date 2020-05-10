package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public abstract class TypeBasedDeserializer<T> implements JsonDeserializer<T>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceLocation, Function<JsonObject, ? extends T>> deserializers;
    private final String typeName;

    protected TypeBasedDeserializer(String typeName)
    {
        this.typeName = typeName;
        this.deserializers = new HashMap<>();
    }

    public void register(ResourceLocation name, Function<JsonObject, ? extends T> deserializer)
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
    public T deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        return read(element);
    }

    public T read(JsonElement element)
    {
        JsonObject json = JSONUtils.getJsonObject(element, typeName);
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
            return deserializers.get(type).apply(json);
        }
        else
        {
            throw new JsonParseException("Unknown " + typeName + ": " + type);
        }
    }

    /**
     * Returns a fallback deserializer ID, if the "type" field is not present
     */
    @Nullable
    protected ResourceLocation getFallbackType()
    {
        return null;
    }
}
