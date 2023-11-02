/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

/**
 * An ingredient for a single fluid.
 * Used in conjunction with recipes that primarily accept {@link Fluid}s.
 */
public final class FluidIngredient implements Predicate<Fluid>
{
    public static FluidIngredient of(Fluid... fluids)
    {
        return new FluidIngredient(Arrays.stream(fluids).map(FluidEntry::new));
    }

    public static FluidIngredient of(TagKey<Fluid> tag)
    {
        return new FluidIngredient(Stream.of(new TagEntry(tag)));
    }

    public static FluidIngredient of(FluidIngredient... others)
    {
        return new FluidIngredient(Arrays.stream(others).flatMap(e -> e.entries.stream()));
    }

    public static FluidIngredient fromJson(JsonElement json)
    {
        if (json.isJsonPrimitive())
        {
            return new FluidIngredient(Collections.singletonList(new FluidEntry(JsonHelpers.getRegistryEntry(json, ForgeRegistries.FLUIDS))));
        }
        if (json.isJsonObject())
        {
            final JsonObject obj = json.getAsJsonObject();
            if (obj.has("fluid") && obj.has("tag"))
            {
                throw new JsonParseException("Fluid ingredient cannot have both 'fluid' and 'tag' entries");
            }
            if (obj.has("fluid"))
            {
                return FluidIngredient.of(JsonHelpers.getRegistryEntry(obj, "fluid", ForgeRegistries.FLUIDS));
            }
            else if (obj.has("tag"))
            {
                return FluidIngredient.of(JsonHelpers.getTag(obj, "tag", Registry.FLUID_REGISTRY));
            }
            else
            {
                throw new JsonParseException("Fluid ingredient must have one of 'fluid' or 'tag' entries");
            }
        }
        final JsonArray array = JsonHelpers.convertToJsonArray(json, "fluid ingredient");
        final List<FluidIngredient> entries = new ArrayList<>();
        for (JsonElement element : array)
        {
            entries.add(FluidIngredient.fromJson(element));
        }
        return new FluidIngredient(entries.stream().flatMap(e -> e.entries.stream()));
    }

    public static FluidIngredient fromNetwork(FriendlyByteBuf buffer)
    {
        return new FluidIngredient(Helpers.decodeAll(buffer, new ArrayList<>(), Entry::fromNetwork));
    }

    public static void toNetwork(FriendlyByteBuf buffer, FluidIngredient ingredient)
    {
        Helpers.encodeAll(buffer, ingredient.entries, Entry::toNetwork);
    }

    private final List<Entry> entries;

    private FluidIngredient(Stream<Entry> entries)
    {
        this(entries.toList());
    }

    private FluidIngredient(List<Entry> entries)
    {
        this.entries = entries;
    }

    @Override
    public boolean test(Fluid fluid)
    {
        for (Entry entry : entries)
        {
            if (entry.test(fluid))
            {
                return true;
            }
        }
        return false;
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        Helpers.encodeAll(buffer, entries, Entry::toNetwork);
    }

    public Collection<Fluid> getMatchingFluids()
    {
        return entries.stream().flatMap(Entry::fluids).toList();
    }

    public JsonElement toJson()
    {
        if (entries.size() == 1)
        {
            return entries.get(0).toJson();
        }
        else
        {
            JsonArray json = new JsonArray(entries.size());
            entries.forEach(entry -> json.add(entry.toJson()));
            return json;
        }
    }

    private interface Entry extends Predicate<Fluid>
    {
        static Entry fromNetwork(FriendlyByteBuf buffer)
        {
            final byte id = buffer.readByte();
            if (id == 0)
            {
                final Fluid fluid = buffer.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
                return new FluidEntry(fluid);
            }
            else if (id == 1)
            {
                final TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, buffer.readResourceLocation());
                return new TagEntry(tag);
            }
            throw new IllegalArgumentException("Illegal id: " + id);
        }

        void toNetwork(FriendlyByteBuf buffer);

        Stream<Fluid> fluids();

        JsonElement toJson();
    }

    private record FluidEntry(Fluid fluid) implements Entry
    {
        @Override
        public boolean test(Fluid fluid)
        {
            return this.fluid == fluid;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer)
        {
            buffer.writeByte(0);
            buffer.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
        }

        @Override
        public Stream<Fluid> fluids()
        {
            return Stream.of(fluid);
        }

        @Override
        public JsonElement toJson()
        {
            assert fluid.getRegistryName() != null;
            JsonObject json = new JsonObject();
            json.addProperty("fluid", fluid.getRegistryName().toString());
            return json;
        }
    }

    private record TagEntry(TagKey<Fluid> tag) implements Entry
    {
        @Override
        public boolean test(Fluid fluid)
        {
            return Helpers.isFluid(fluid, tag);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer)
        {
            buffer.writeByte(1);
            buffer.writeResourceLocation(tag.location());
        }

        @Override
        public Stream<Fluid> fluids()
        {
            return Helpers.getAllTagValues(tag, ForgeRegistries.FLUIDS).stream();
        }

        @Override
        public JsonElement toJson()
        {
            JsonObject json = new JsonObject();
            json.addProperty("tag", tag.location().toString());
            return json;
        }
    }
}
