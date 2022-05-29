/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import net.dries007.tfc.util.Helpers;

/**
 * This is a structure which provides O(1), {@link HashMap} access of the wrapped {@code Map<Predicate<V>, R>}
 * It does this by using the "indirect key", or {@code K}. By using two constructs:
 * - The key mapper, a {@code Function<V, K>}.
 * - And the key extractor, a{@code Function<R, Iterable<K>>}.
 * The above must satisfy the following condition:
 * - For any K, V, R, {@code (keyMapper.apply(V) == K) -> (keyExtractor.apply(R).contains(K))}
 * <p>
 * This was benchmarked using VisualVM, with a recipe list of ~1000 recipes (not uncommon), using Landslide Recipes, over >10,000 invocations.
 * - Vanilla's recipe manager query took 847 us / recipe
 * - Using a LRU cache of size 1, delegating to the above took 273 us / recipe
 * - this took 11 us / recipe.
 */
public class IndirectHashCollection<K, R>
{
    private static final Map<IndirectHashCollection<?, ?>, Supplier<Collection<?>>> DIRECT_CACHES = new HashMap<>();
    private static final Map<IndirectHashCollection<?, ?>, Supplier<RecipeType<?>>> RECIPE_CACHES = new HashMap<>();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <K, R> IndirectHashCollection<K, R> create(Function<R, Iterable<? extends K>> keyExtractor, Supplier<Collection<R>> reloadableCollection)
    {
        final IndirectHashCollection<K, R> cache = new IndirectHashCollection<>(keyExtractor);
        DIRECT_CACHES.put(cache, (Supplier) reloadableCollection);
        return cache;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <C extends Container, K, R extends Recipe<C>> IndirectHashCollection<K, R> createForRecipe(Function<R, Iterable<? extends K>> keyExtractor, Supplier<RecipeType<R>> recipeType)
    {
        final IndirectHashCollection<K, R> cache = new IndirectHashCollection<>(keyExtractor);
        RECIPE_CACHES.put(cache, (Supplier) recipeType);
        return cache;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void reloadAllCaches(RecipeManager manager)
    {
        DIRECT_CACHES.forEach((cache, values) -> reloadDirectCache((IndirectHashCollection) cache, (Supplier) values));
        RECIPE_CACHES.forEach((cache, type) -> reloadRecipeCache((IndirectHashCollection) cache, manager, (Supplier) type));
    }

    private static <K, R> void reloadDirectCache(IndirectHashCollection<K, R> cache, Supplier<Collection<R>> values)
    {
        cache.reload(values.get());
    }

    private static <C extends Container, K, R extends Recipe<C>> void reloadRecipeCache(IndirectHashCollection<K, R> cache, RecipeManager manager, Supplier<RecipeType<R>> recipe)
    {
        cache.reload(Helpers.getRecipes(manager, recipe).values());
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
}