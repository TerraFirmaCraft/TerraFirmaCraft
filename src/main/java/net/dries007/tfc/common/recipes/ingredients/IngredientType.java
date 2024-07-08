/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;

/**
 * A super interface for ingredients that can be represented by a single object, or a tag.
 *
 * @param <T> The object type.
 */
public interface IngredientType<T> extends Predicate<T>
{
    /**
     * Parse a new {@link IngredientType} of type {@code I} from JSON, using the given {@link Factory} instance as a prototype.
     * Accepts any of the following formats:
     * <ul>
     *     <li>A string, which is interpreted as an object ID</li>
     *     <li>An object with a "tag" key, which is interpreted as a tag ID</li>
     *     <li>An object with a key based on the factory, which is interpreted as an object ID</li>
     *     <li>A list of any of the above, which is interpreted as OR-ing each ingredient together.</li>
     * </ul>
     */
    static <T, I extends IngredientType<T>> I fromJson(JsonElement json, Factory<T, I> factory)
    {
        if (json.isJsonPrimitive())
        {
            final T object = JsonHelpers.getRegistryEntry(json, factory.registry);
            return of(object, factory);
        }
        if (json.isJsonObject())
        {
            final JsonObject obj = json.getAsJsonObject();
            if (obj.has(factory.key) && obj.has("tag"))
            {
                throw new JsonParseException(factory.key + " ingredient cannot have both '" + factory.key + "' and 'tag' entries");
            }
            if (obj.has(factory.key))
            {
                final T object = JsonHelpers.getRegistryEntry(obj, factory.key, factory.registry);
                return of(object, factory);
            }
            else if (obj.has("tag"))
            {
                final TagKey<T> tag = JsonHelpers.getTag(obj, "tag", factory.registry.key());
                return of(tag, factory);
            }
            else
            {
                throw new JsonParseException("Fluid ingredient must have one of 'fluid' or 'tag' entries");
            }
        }
        final JsonArray array = JsonHelpers.convertToJsonArray(json, "ingredient");
        final List<Entry<T>> entries = new ArrayList<>();
        for (JsonElement element : array)
        {
            entries.addAll(fromJson(element, factory).entries());
        }
        return factory.factory.apply(entries);
    }

    /***
     * Converts an ingredient back to a canonical JSON representation.
     */
    static <T, I extends IngredientType<T>> JsonElement toJson(I ingredient, Factory<T, I> factory)
    {
        final List<Entry<T>> entries = ingredient.entries();
        if (entries.size() == 1)
        {
            return toJson(entries.get(0), factory);
        }
        else
        {
            final JsonArray json = new JsonArray(entries.size());
            entries.forEach(entry -> json.add(toJson(entry, factory)));
            return json;
        }
    }

    private static <T, I extends IngredientType<T>> I of(T object, Factory<T, I> factory)
    {
        return factory.factory.apply(List.of(new ObjEntry<>(object)));
    }

    private static <T, I extends IngredientType<T>> I of(TagKey<T> tag, Factory<T, I> factory)
    {
        return factory.factory.apply(List.of(factory.tagEntry.apply(tag)));
    }

    private static <T, I extends IngredientType<T>> JsonElement toJson(Entry<T> entry, Factory<T, I> factory)
    {
        final JsonObject obj = new JsonObject();
        if (entry instanceof IngredientType.ObjEntry<T> objEntry)
        {
            obj.addProperty(factory.key, Objects.requireNonNull(factory.registry.getKey(objEntry.object)).toString());
        }
        else
        {
            obj.addProperty("tag", ((TagEntry<T>) entry).tag().location().toString());
        }
        return obj;
    }

    /**
     * Encodes the given ingredient to the network.
     */
    static <T, I extends IngredientType<T>> void toNetwork(FriendlyByteBuf buffer, I ingredient, Factory<T, I> factory)
    {
        Helpers.encodeAll(buffer, ingredient.entries(), (entry, buf) -> {
            if (entry instanceof IngredientType.ObjEntry<T> objEntry)
            {
                buf.writeByte(0);
                buf.writeVarInt(factory.registry.getId(objEntry.object));
            }
            else
            {
                buf.writeByte(1);
                buf.writeResourceLocation(((TagEntry<T>) entry).tag().location());
            }
        });
    }

    /**
     * Decodes the given ingredient from the network. Inverse operation of {@link #toNetwork(FriendlyByteBuf, IngredientType, Factory)}
     */
    static <T, I extends IngredientType<T>> I fromNetwork(FriendlyByteBuf buffer, Factory<T, I> factory)
    {
        return factory.factory.apply(Helpers.decodeAll(buffer, new ArrayList<>(), buf -> {
            if (buf.readByte() == 0)
            {
                final T object = factory.registry.byId(buffer.readVarInt());
                return new ObjEntry<>(object);
            }
            else
            {
                final TagKey<T> tag = TagKey.create(factory.registry.key(), buf.readResourceLocation());
                return factory.tagEntry.apply(tag);
            }
        }));
    }

    /**
     * @return The list of entries (either tags, or objects) of this ingredient.
     */
    List<Entry<T>> entries();

    /**
     * @return A stream of all the individual objects that may match this ingredient.
     */
    default Stream<T> all()
    {
        return entries().stream().flatMap(Entry::stream);
    }

    /**
     * Bouncer to {@link IngredientType#toJson(IngredientType, Factory)} with the current ingredient and factory type.
     */
    JsonElement toJson();

    /**
     * Bouncer to {@link IngredientType#toNetwork(FriendlyByteBuf, IngredientType, Factory)} with the current ingredient and factory type.
     */
    void toNetwork(FriendlyByteBuf buffer);

    /**
     * @return {@code true} if the object satisfies this ingredient.
     */
    @Override
    default boolean test(T object)
    {
        for (Entry<T> entry : entries())
        {
            if (entry.test(object))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * The entry type of the ingredient, which permits a single object via {@link ObjEntry}, and a tag entry, via {@link TagEntry}, which is intended to be implemented individually in a simple record. The implementation is not sealed as tag checks are specific to the registry.
     * @param <T> The type of the underlying object.
     */
    sealed interface Entry<T>
        extends Predicate<T>
        permits TagEntry, ObjEntry
    {
        Stream<T> stream();
    }

    record ObjEntry<T>(T object) implements Entry<T>
    {
        @Override
        public boolean test(T object)
        {
            return this.object == object;
        }

        @Override
        public Stream<T> stream()
        {
            return Stream.of(object);
        }
    }

    non-sealed interface TagEntry<T> extends Entry<T>
    {
        TagKey<T> tag();
    }

    /**
     * This abstracts all non-common ingredient parsing, handling, and serializing operations that is not common to {@link IngredientType}.
     * @param key The string key used in serialization, i.e. "block"
     * @param registry The registry of objects
     * @param tagEntry A factory to construct a {@link TagEntry} instance
     * @param factory A factory to construct an {@link IngredientType} instance.
     * @param <T> The element type.
     * @param <I> The ingredient type.
     */
    record Factory<T, I extends IngredientType<T>>(
        String key,
        DefaultedRegistry<T> registry,
        Function<TagKey<T>, Entry<T>> tagEntry,
        Function<List<Entry<T>>, I> factory
    ) {}
}
