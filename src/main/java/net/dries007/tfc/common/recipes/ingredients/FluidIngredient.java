/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.*;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

/**
 * An ingredient for a single fluid.
 * Used in conjunction with recipes that primarily accept {@link Fluid}s.
 */
public final class FluidIngredient implements Predicate<Fluid>
{
    public static FluidIngredient fromJson(JsonElement json)
    {
        if (json.isJsonPrimitive())
        {
            return new FluidIngredient(JsonHelpers.getRegistryEntry(json, ForgeRegistries.FLUIDS));
        }
        if (json.isJsonObject())
        {
            return new FluidIngredient(fromJsonObject(json.getAsJsonObject(), new ObjectOpenHashSet<>()));
        }
        return new FluidIngredient(fromJsonArray(JsonHelpers.convertToJsonArray(json, "fluid ingredient"), new ObjectOpenHashSet<>()));
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        return new FluidIngredient(buffer);
    }

    public static void toNetwork(FriendlyByteBuf buffer, FluidIngredient ingredient)
    {
        Helpers.encodeAll(buffer, ingredient.fluids, (b, f) -> b.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, f));
    }

    private static void fromJson(JsonElement json, Set<Fluid> entries)
    {
        if (json.isJsonPrimitive())
        {
            entries.add(JsonHelpers.getRegistryEntry(json, ForgeRegistries.FLUIDS));
        }
        else if (json.isJsonObject())
        {
            fromJsonObject(json.getAsJsonObject(), entries);
        }
        else if (json.isJsonArray())
        {
            fromJsonArray(JsonHelpers.convertToJsonArray(json, "fluid ingredient array entry"), entries);
        }
        else
        {
            throw new JsonParseException("Expected fluid ingredient array entry to be either string, object, or array, was " + JsonHelpers.getType(json));
        }
    }

    private static Set<Fluid> fromJsonArray(JsonArray array, Set<Fluid> entries)
    {
        for (JsonElement json : array)
        {
            fromJson(json, entries);
        }
        return entries;
    }

    private static Set<Fluid> fromJsonObject(JsonObject json, Set<Fluid> entries)
    {
        if (json.has("fluid") && json.has("tag"))
        {
            throw new JsonParseException("Fluid ingredient cannot have both 'fluid' and 'tag' entries");
        }
        if (json.has("fluid"))
        {
            entries.add(JsonHelpers.getRegistryEntry(json, "fluid", ForgeRegistries.FLUIDS));
        }
        else if (json.has("tag"))
        {
            entries.addAll(getAll(JsonHelpers.getTag(json, "tag", Registry.FLUID_REGISTRY)));
        }
        else
        {
            throw new JsonParseException("Fluid ingredient must have one of 'fluid' or 'tag' entries");
        }
        return entries;
    }

    private static Collection<Fluid> getAll(TagKey<Fluid> tag)
    {
        return Helpers.getAllTagValues(tag, ForgeRegistries.FLUIDS);
    }

    private final Set<Fluid> fluids;

    FluidIngredient(Fluid fluid)
    {
        this(Collections.singleton(fluid));
    }

    FluidIngredient(Set<Fluid> fluids)
    {
        this.fluids = fluids;
    }

    FluidIngredient(FriendlyByteBuf buffer)
    {
        this.fluids = Helpers.decodeAll(buffer, new ObjectOpenHashSet<>(), b -> b.readRegistryIdUnsafe(ForgeRegistries.FLUIDS));
    }

    public boolean test(Fluid fluid)
    {
        return fluids.contains(fluid);
    }

    public Collection<Fluid> getMatchingFluids()
    {
        return fluids;
    }
}
