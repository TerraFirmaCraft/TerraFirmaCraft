/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.CacheInvalidationListener;

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
    public static <K, R> IndirectHashCollection<K, R> create(Function<R, Iterable<? extends K>> keyExtractor, Supplier<Collection<R>> reloadableCollection)
    {
        return new IndirectHashCollection<>(keyExtractor, reloadableCollection);
    }

    public static <C extends Container, K, R extends Recipe<C>> IndirectHashCollection<K, R> createForRecipe(Function<R, Iterable<? extends K>> keyExtractor, Supplier<RecipeType<R>> recipeType)
    {
        return new IndirectHashCollection<>(keyExtractor, () -> getTheRecipeManagerInTheMostHackyAwfulWay().getAllRecipesFor(recipeType.get()));
    }

    /**
     * Cannot cache recipes on resource reload, when a level is available, as tags aren't present and can't be resolved. Recipes may be accessed through {@link CacheInvalidationListener} but holding references to them is awkward for client side caches.
     * Resolving recipes needs access to a {@link RecipeManager} which needs to be obtained from a {@link Level} which is prohibitively difficult for use cases such as item stack capabilities, where a level is not readily available.
     * This seems to work.
     */
    private static RecipeManager getTheRecipeManagerInTheMostHackyAwfulWay()
    {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
        {
            return server.getRecipeManager();
        }
        try
        {
            final Level level = ClientHelpers.getLevel();
            if (level != null)
            {
                return level.getRecipeManager();
            }
        }
        catch (Throwable t) { /* super safe */ }
        throw new IllegalStateException("For the love of god");
    }

    private final Map<K, Collection<R>> indirectResultMap;
    private final Function<R, Iterable<? extends K>> keyExtractor;
    private final Supplier<Collection<R>> reloadableCollection;
    private volatile boolean valid;

    private IndirectHashCollection(Function<R, Iterable<? extends K>> keyExtractor, Supplier<Collection<R>> reloadableCollection)
    {
        this.keyExtractor = keyExtractor;
        this.indirectResultMap = new HashMap<>();
        this.reloadableCollection = reloadableCollection;
        this.valid = false;

        CacheInvalidationListener.INSTANCE.doOnInvalidate(this::invalidate);
    }

    public Collection<R> getAll(K key)
    {
        if (!valid)
        {
            synchronized (this)
            {
                if (!valid)
                {
                    valid = true;
                    indirectResultMap.clear();
                    reloadableCollection.get().forEach(result -> {
                        for (K directKey : keyExtractor.apply(result))
                        {
                            indirectResultMap.computeIfAbsent(directKey, k -> new ArrayList<>()).add(result);
                        }
                    });
                }
            }
        }
        return indirectResultMap.getOrDefault(key, Collections.emptyList());
    }

    public void invalidate()
    {
        valid = false;
    }
}