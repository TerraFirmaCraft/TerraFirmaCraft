/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.common.recipes.RecipeHelpers;

/**
 * This is a structure which provides O(1), {@link HashMap} access of the wrapped {@code Map<Predicate<V>, R>} It does this by using
 * the "indirect key", or {@code K}. By using two constructs:
 * <ul>
 *     <li>The key mapper, a {@code Function<V, K>}</li>
 *     <li>And the key extractor, a{@code Function<R, Iterable<K>>}</li>
 * </ul>
 * The above must satisfy the following condition: For any K, V, R, {@code (keyMapper.apply(V) == K) -> (keyExtractor.apply(R).contains(K))}
 * <p>
 * This was benchmarked using VisualVM, with a recipe list of ~1000 recipes (not uncommon), using Landslide Recipes, over >10,000 invocations.
 * <ul>
 *     <li>Vanilla's recipe manager query took 847 us / recipe</li>
 *     <li>Using an LRU cache of size 1, delegating to the above took 273 us / recipe</li>
 *     <li>This took 11 us / recipe.</li>
 * </ul>
 */
public class IndirectHashCollection<K, R>
{
    private static final List<Cache> CACHES = new ArrayList<>();

    /**
     * Create a new {@link IndirectHashCollection} that is backed from the provided value supplier. This will manage the cache's overall
     * lifecycle, including clearing and reloading, as necessary.
     */
    public static <K, R> IndirectHashCollection<K, R> create(Function<R, Iterable<? extends K>> keyExtractor, Supplier<Collection<R>> values)
    {
        final IndirectHashCollection<K, R> cache = new IndirectHashCollection<>(keyExtractor);
        create(new DirectCache<>(cache, values));
        return cache;
    }

    /**
     * Creates a new {@link IndirectHashCollection} that is backed from the given recipe type. This will manage the cache's overall
     * lifecycle, including clearing and reloading, as necessary.
     */
    public static <K, R extends Recipe<?>> IndirectHashCollection<K, R> createForRecipe(Function<R, Iterable<? extends K>> keyExtractor, Supplier<RecipeType<R>> recipeType)
    {
        final IndirectHashCollection<K, R> cache = new IndirectHashCollection<>(keyExtractor);
        create(new RecipeCache<>(cache, recipeType));
        return cache;
    }

    /**
     * Creates a new bijective ID map between that is backed from the given recipe type. This will manage the cache's overall
     * lifecycle, including clearing and reloading, as necessary
     */
    public static <R extends Recipe<?>> BiMap<ResourceLocation, R> createForRecipeId(Supplier<RecipeType<R>> recipeType)
    {
        final BiMap<ResourceLocation, R> cache = HashBiMap.create();
        create(new RecipeIdCache<>(cache, recipeType));
        return cache;
    }

    /**
     * Adds a cache to the list of all known caches. this is synchronized and thus threadsafe for parallel mod loading or class loading.
     * @param cache The cache to add
     */
    private static synchronized void create(Cache cache)
    {
        CACHES.add(cache);
    }

    public static void reloadAllCaches(RecipeManager manager)
    {
        CACHES.forEach(c -> c.reload(manager));
    }

    public static void clearAllCaches()
    {
        CACHES.forEach(Cache::clear);
    }

    private final Map<K, Collection<R>> indirectResultMap;
    private final Function<R, Iterable<? extends K>> keyExtractor;

    public IndirectHashCollection(Function<R, Iterable<? extends K>> keyExtractor)
    {
        this.keyExtractor = keyExtractor;
        this.indirectResultMap = new HashMap<>();
    }

    public Collection<R> getAll(K key)
    {
        return indirectResultMap.getOrDefault(key, Collections.emptyList());
    }

    public void reload(Collection<R> values)
    {
        indirectResultMap.clear();
        values.forEach(result -> {
            for (K directKey : keyExtractor.apply(result))
            {
                indirectResultMap.computeIfAbsent(directKey, k -> new ArrayList<>()).add(result);
            }
        });
    }

    public void clear()
    {
        indirectResultMap.clear();
    }


    /**
     * An interface representing a cache that is aware of its ability to reload, without requiring an external source
     * of values. This is also typesafe with each implementation having refined generic types.
     */
    interface Cache
    {
        void clear();
        void reload(RecipeManager manager);
    }

    record DirectCache<K, R>(IndirectHashCollection<K, R> cache, Supplier<Collection<R>> values) implements Cache
    {
        @Override public void clear() { cache.clear(); }
        @Override public void reload(RecipeManager manager) { cache.reload(values.get()); }
    }

    record RecipeCache<K, R extends Recipe<?>>(IndirectHashCollection<K, R> cache, Supplier<RecipeType<R>> recipeType) implements Cache
    {
        @Override public void clear() { cache.clear(); }
        @Override public void reload(RecipeManager manager) { cache.reload(RecipeHelpers.getRecipes(manager, recipeType).stream().map(RecipeHolder::value).toList()); }
    }

    record RecipeIdCache<R extends Recipe<?>>(BiMap<ResourceLocation, R> cache, Supplier<RecipeType<R>> recipeType) implements Cache
    {
        @Override
        public void clear()
        {
            cache.clear();
        }

        @Override
        public void reload(RecipeManager manager)
        {
            cache.clear();
            RecipeHelpers.getRecipes(manager, recipeType).forEach(holder -> cache.put(holder.id(), holder.value()));
        }
    }
}