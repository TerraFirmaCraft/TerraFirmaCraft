package net.dries007.tfc.common.recipes.ingredients;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class FluidIngredients
{
    private static final BiMap<ResourceLocation, FluidIngredient.Serializer<?>> REGISTRY = HashBiMap.create();

    /**
     * Registers a fluid ingredient serializer
     * This method is safe to call during parallel mod loading.
     */
    public synchronized static <V extends FluidIngredient, T extends FluidIngredient.Serializer<V>> T register(ResourceLocation key, T serializer)
    {
        if (REGISTRY.containsKey(key))
        {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        REGISTRY.put(key, serializer);
        return serializer;
    }

    public static FluidIngredient fromJson(JsonObject json)
    {
        FluidIngredient.Serializer<?> serializer;
        if (json.has("type"))
        {
            final String type = GsonHelper.getAsString(json, "type");
            serializer = REGISTRY.get(new ResourceLocation(type));
            if (serializer == null)
            {
                throw new JsonParseException("Unknown fluid ingredient type: " + type);
            }
        }
        else
        {
            if (json.has("fluid") && json.has("tag"))
            {
                throw new JsonParseException("Fluid ingredient cannot have both 'fluid' and 'tag' entries");
            }
            if (json.has("fluid"))
            {
                serializer = FluidIngredient.FLUID;
            }
            else if (json.has("tag"))
            {
                serializer = FluidIngredient.TAG;
            }
            else
            {
                throw new JsonParseException("Fluid ingredient must have one of 'type', 'fluid', or 'tag' entries");
            }
        }
        return serializer.fromJson(json);
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        final FluidIngredient.Serializer<?> serializer = REGISTRY.get(buffer.readResourceLocation());
        return serializer.fromNetwork(buffer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void toNetwork(FriendlyByteBuf buffer, FluidIngredient ingredient)
    {
        buffer.writeResourceLocation(REGISTRY.inverse().get(ingredient.getSerializer()));
        ((FluidIngredient.Serializer) ingredient.getSerializer()).toNetwork(buffer, ingredient);
    }
}
