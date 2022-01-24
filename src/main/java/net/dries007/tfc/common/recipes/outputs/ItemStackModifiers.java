package net.dries007.tfc.common.recipes.outputs;

import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class ItemStackModifiers
{
    private static final BiMap<ResourceLocation, ItemStackModifier.Serializer<?>> REGISTRY = HashBiMap.create();

    public static void registerItemStackModifierTypes()
    {
        register("copy_input", CopyInputModifier.INSTANCE);
        register("copy_food", CopyFoodModifier.INSTANCE);
        register("copy_heat", CopyHeatModifier.INSTANCE);
        register("reset_food", ResetFoodModifier.INSTANCE);

        register("add_trait", AddTraitModifier.Serializer.INSTANCE);
        register("add_heat", AddHeatModifier.Serializer.INSTANCE);
    }

    /**
     * Registers a block ingredient serializer
     * This method is safe to call during parallel mod loading
     */
    public static synchronized <V extends ItemStackModifier, T extends ItemStackModifier.Serializer<V>> T register(ResourceLocation key, T serializer)
    {
        if (REGISTRY.containsKey(key))
        {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        REGISTRY.put(key, serializer);
        return serializer;
    }

    public static ItemStackModifier fromJson(JsonElement json)
    {
        if (json.isJsonPrimitive())
        {
            final String type = JsonHelpers.convertToString(json, "modifier");
            final ItemStackModifier.Serializer<?> serializer = getSerializer(type);
            if (serializer instanceof ItemStackModifier.SingleInstance<?> factory)
            {
                return factory.instance();
            }
            throw new JsonParseException("Serializer type: " + type + " cannot be declared inline");
        }
        final JsonObject obj = JsonHelpers.convertToJsonObject(json, "modifier");
        final String type = JsonHelpers.getAsString(obj, "type");
        final ItemStackModifier.Serializer<?> serializer = getSerializer(type);
        return serializer.fromJson(obj);
    }

    public static ItemStackModifier fromNetwork(FriendlyByteBuf buffer)
    {
        final ResourceLocation id = buffer.readResourceLocation();
        final ItemStackModifier.Serializer<?> serializer = Objects.requireNonNull(REGISTRY.get(id), "Unknown item stack modifier: " + id);
        return serializer.fromNetwork(buffer);
    }

    private static ItemStackModifier.Serializer<?> getSerializer(String type)
    {
        final ItemStackModifier.Serializer<?> serializer = REGISTRY.get(new ResourceLocation(type));
        if (serializer != null)
        {
            return serializer;
        }
        throw new JsonParseException("Unknown item stack modifier type: " + type);
    }

    private static void register(String name, ItemStackModifier.Serializer<?> serializer)
    {
        register(Helpers.identifier(name), serializer);
    }
}
