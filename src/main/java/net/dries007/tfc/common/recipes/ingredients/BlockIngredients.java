/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

public class BlockIngredients
{
    private static final BiMap<ResourceLocation, BlockIngredient.Serializer<?>> REGISTRY = HashBiMap.create();

    public static void registerBlockIngredientTypes()
    {
        register(Helpers.identifier("block"), SimpleBlockIngredient.Serializer.INSTANCE);
        register(Helpers.identifier("tag"), TagBlockIngredient.Serializer.INSTANCE);
    }

    /**
     * Registers a block ingredient serializer
     * This method is safe to call during parallel mod loading
     */
    public static synchronized <V extends BlockIngredient, T extends BlockIngredient.Serializer<V>> T register(ResourceLocation key, T serializer)
    {
        if (REGISTRY.containsKey(key))
        {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        REGISTRY.put(key, serializer);
        return serializer;
    }

    public static BlockIngredient of(Block... blocks)
    {
        return new SimpleBlockIngredient(Arrays.asList(blocks));
    }

    public static BlockIngredient of(TagKey<Block> tag)
    {
        return new TagBlockIngredient(tag);
    }

    public static BlockIngredient fromJson(JsonElement json)
    {
        if (json.isJsonArray())
        {
            return fromJsonArray(json.getAsJsonArray());
        }
        if (json.isJsonPrimitive())
        {
            return fromJsonString(json.getAsString());
        }

        final JsonObject obj = json.getAsJsonObject();
        BlockIngredient.Serializer<?> serializer;
        if (obj.has("type"))
        {
            final String type = GsonHelper.getAsString(obj, "type");
            serializer = REGISTRY.get(new ResourceLocation(type));
            if (serializer == null)
            {
                throw new JsonParseException("Unknown block ingredient type: " + type);
            }
        }
        else if (obj.has("block"))
        {
            serializer = SimpleBlockIngredient.Serializer.INSTANCE;
        }
        else if (obj.has("tag"))
        {
            serializer = TagBlockIngredient.Serializer.INSTANCE;
        }
        else
        {
            throw new JsonParseException("Block ingredient must be either array, string, or object with either 'type', 'block', or 'tag' property");
        }
        return serializer.fromJson(obj);
    }

    public static SimpleBlockIngredient fromJsonArray(JsonArray array)
    {
        final Set<Block> blocks = new HashSet<>();
        for (JsonElement e : array)
        {
            blocks.add(JsonHelpers.getRegistryEntry(e, ForgeRegistries.BLOCKS));
        }
        return new SimpleBlockIngredient(blocks);
    }

    public static SimpleBlockIngredient fromJsonString(String string)
    {
        return new SimpleBlockIngredient(JsonHelpers.getRegistryEntry(string, ForgeRegistries.BLOCKS));
    }

    public static BlockIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        final BlockIngredient.Serializer<?> serializer = REGISTRY.get(buffer.readResourceLocation());
        return serializer.fromNetwork(buffer);
    }

    public static BlockIngredient.Serializer<?> byId(ResourceLocation id)
    {
        return Objects.requireNonNull(REGISTRY.get(id), () -> "No serializer by id: " + id);
    }

    public static ResourceLocation getId(BlockIngredient.Serializer<?> serializer)
    {
        return Objects.requireNonNull(REGISTRY.inverse().get(serializer), () -> "Unregistered serializer: " + serializer);
    }

}
