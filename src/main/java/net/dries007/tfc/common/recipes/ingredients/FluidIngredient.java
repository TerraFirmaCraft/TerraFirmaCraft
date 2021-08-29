/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Collection;
import java.util.function.Predicate;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.util.Helpers;

public interface FluidIngredient extends Predicate<FluidStack>
{
    /* Internal Access Only */
    BiMap<ResourceLocation, Serializer<?>> REGISTRY = HashBiMap.create();

    ResourceLocation FLUID_KEY = Helpers.identifier("fluid");
    ResourceLocation TAG_KEY = Helpers.identifier("tag");

    SimpleFluidIngredient.Serializer FLUID = register(FLUID_KEY, new SimpleFluidIngredient.Serializer());
    TagFluidIngredient.Serializer TAG = register(TAG_KEY, new TagFluidIngredient.Serializer());

    static <V extends FluidIngredient, T extends FluidIngredient.Serializer<V>> T register(ResourceLocation key, T serializer)
    {
        if (REGISTRY.containsKey(key))
        {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        REGISTRY.put(key, serializer);
        return serializer;
    }

    static FluidIngredient fromJson(JsonObject json)
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
                serializer = FLUID;
            }
            else if (json.has("tag"))
            {
                serializer = TAG;
            }
            else
            {
                throw new JsonParseException("Fluid ingredient must have one of 'type', 'fluid', or 'tag' entries");
            }
        }
        return serializer.fromJson(json);
    }

    static FluidIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        final FluidIngredient.Serializer<?> serializer = REGISTRY.get(buffer.readResourceLocation());
        return serializer.fromNetwork(buffer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void toNetwork(FriendlyByteBuf buffer, FluidIngredient ingredient)
    {
        buffer.writeResourceLocation(REGISTRY.inverse().get(ingredient.getSerializer()));
        ((Serializer) ingredient.getSerializer()).toNetwork(buffer, ingredient);
    }

    /**
     * Test the ingredient against the provided fluid stack, including amounts.
     */
    @Override
    boolean test(FluidStack fluidStack);

    /**
     * Test the ingredient against the provided fluid stack, ignoring amounts.
     */
    boolean testIgnoreAmount(Fluid fluid);

    /**
     * Get all possible fluids that can matching this ingredient
     */
    Collection<Fluid> getMatchingFluids();

    FluidIngredient.Serializer<?> getSerializer();

    interface Serializer<T extends FluidIngredient>
    {
        T fromJson(JsonObject json);

        T fromNetwork(FriendlyByteBuf buffer);

        void toNetwork(FriendlyByteBuf buffer, T ingredient);
    }
}
