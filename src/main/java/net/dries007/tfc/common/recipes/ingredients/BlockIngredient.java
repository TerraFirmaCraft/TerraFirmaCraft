/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;

/**
 * This is a simple predicate wrapper for block states.
 * It can compare a single or multiple blocks, or a tag.
 */
public interface BlockIngredient extends Predicate<BlockState>
{
    /* Internal Access Only */
    BiMap<ResourceLocation, Serializer<?>> REGISTRY = HashBiMap.create();

    ResourceLocation BLOCK_KEY = Helpers.identifier("block");
    ResourceLocation TAG_KEY = Helpers.identifier("tag");

    SimpleBlockIngredient.Serializer BLOCK = register(BLOCK_KEY, new SimpleBlockIngredient.Serializer());
    TagBlockIngredient.Serializer TAG = register(TAG_KEY, new TagBlockIngredient.Serializer());

    static <V extends BlockIngredient, T extends BlockIngredient.Serializer<V>> T register(ResourceLocation key, T serializer)
    {
        if (REGISTRY.containsKey(key))
        {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        REGISTRY.put(key, serializer);
        return serializer;
    }

    static BlockIngredient fromJson(JsonElement json)
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
            final String type = JSONUtils.getAsString(obj, "type");
            serializer = REGISTRY.get(new ResourceLocation(type));
            if (serializer == null)
            {
                throw new JsonParseException("Unknown block ingredient type: " + type);
            }
        }
        else if (obj.has("block"))
        {
            serializer = BLOCK;
        }
        else if (obj.has("tag"))
        {
            serializer = TAG;
        }
        else
        {
            throw new JsonParseException("Block ingredient must be either array, string, or object with either 'type', 'block', or 'tag' property");
        }
        return serializer.fromJson(obj);
    }

    static SimpleBlockIngredient fromJsonArray(JsonArray array)
    {
        final Set<Block> blocks = new HashSet<>();
        for (JsonElement e : array)
        {
            final ResourceLocation blockName = new ResourceLocation(e.getAsString());
            final Block block = Helpers.nonNullOrJsonError(ForgeRegistries.BLOCKS.getValue(blockName), "Not a block: " + blockName);
            blocks.add(block);
        }
        return new SimpleBlockIngredient(blocks);
    }

    static SimpleBlockIngredient fromJsonString(String string)
    {
        final ResourceLocation blockName = new ResourceLocation(string);
        final Block block = Helpers.nonNullOrJsonError(ForgeRegistries.BLOCKS.getValue(blockName), "Not a block: " + blockName);
        return new SimpleBlockIngredient(block);
    }

    static BlockIngredient fromNetwork(PacketBuffer buffer)
    {
        final BlockIngredient.Serializer<?> serializer = REGISTRY.get(buffer.readResourceLocation());
        return serializer.fromNetwork(buffer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void toNetwork(PacketBuffer buffer, BlockIngredient ingredient)
    {
        buffer.writeResourceLocation(REGISTRY.inverse().get(ingredient.getSerializer()));
        ((BlockIngredient.Serializer) ingredient.getSerializer()).toNetwork(buffer, ingredient);
    }

    /**
     * Test if the specified block state is accepted by the ingredient
     */
    @Override
    boolean test(BlockState state);

    /**
     * Return a list of all possible blocks that can be accepted by the ingredient.
     * This is mostly for populating visual lists of recipes and does not obey the exact nature of the ingredient.
     */
    Collection<Block> getValidBlocks();

    BlockIngredient.Serializer<?> getSerializer();

    interface Serializer<T extends BlockIngredient>
    {
        T fromJson(JsonObject json);

        T fromNetwork(PacketBuffer buffer);

        void toNetwork(PacketBuffer buffer, T ingredient);
    }
}