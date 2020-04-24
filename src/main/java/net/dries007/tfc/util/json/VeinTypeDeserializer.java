package net.dries007.tfc.util.json;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.google.gson.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.vein.ClusterVeinType;
import net.dries007.tfc.world.vein.DiscVeinType;
import net.dries007.tfc.world.vein.PipeVeinType;
import net.dries007.tfc.world.vein.VeinType;

/**
 * Root deserializer for all vein types
 * To add new vein types simply call {@code VeinTypeDeserializer.INSTANCE.register()}
 */
public enum VeinTypeDeserializer implements JsonDeserializer<VeinType<?>>
{
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<ResourceLocation, BiFunction<JsonObject, JsonDeserializationContext, ? extends VeinType<?>>> deserializers;

    VeinTypeDeserializer()
    {
        deserializers = new HashMap<>();
        deserializers.put(Helpers.identifier("cluster"), ClusterVeinType::new);
        deserializers.put(Helpers.identifier("disc"), DiscVeinType::new);
        deserializers.put(Helpers.identifier("pipe"), PipeVeinType::new);
    }

    public void register(ResourceLocation name, BiFunction<JsonObject, JsonDeserializationContext, ? extends VeinType<?>> deserializer)
    {
        if (!deserializers.containsKey(name))
        {
            LOGGER.info("Registered Vein Type: {}", name);
            deserializers.put(name, deserializer);
        }
        else
        {
            LOGGER.info("Denied registration of Vein Type {} as it would overwrite an existing vein type!", name);
        }
    }

    @Override
    public VeinType<?> deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject json = JSONUtils.getJsonObject(element, "vein");
        ResourceLocation type = new ResourceLocation(JSONUtils.getString(json, "type"));
        if (deserializers.containsKey(type))
        {
            return deserializers.get(type).apply(json, context);
        }
        else
        {
            throw new JsonParseException("Unknown vein type: " + type);
        }
    }
}
