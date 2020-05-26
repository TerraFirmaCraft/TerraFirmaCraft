package net.dries007.tfc.util.collections;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;

/**
 * This is a structure which provides O(1), {@link HashMap} access of the wrapped {@code Map<Predicate<V>, R>}
 * It does this by using the "indirect key", or {@code K}. By using two constructs:
 * - The key mapper, a {@code Function<V, K>}.
 * - And the key extractor, a{@code Function<R, Iterable<K>>}.
 * The above must satisfy the following condition:
 * - For any K, V, R, {@code (keyMapper.apply(V) == K) -> (keyExtractor.apply(R).contains(K))}
 * <p>
 * This was benchmarked using VisualVM, with a recipe list of ~1000 recipes (not uncommon), using Landslide Recipes, over >10,000 invocations.
 * - {@link net.minecraft.item.crafting.RecipeManager#getRecipe(IRecipeType, IInventory, World)} took 847 us / recipe
 * - Using a LRU cache of size 1, delegating to the above took 273 us / recipe
 * - this took 11 us / recipe.
 */
public class IndirectHashCollection<K, R> {
    private final Map<K, Collection<R>> indirectResultMap;
    private final Function<R, Iterable<K>> keyExtractor;

    public IndirectHashCollection(Function<R, Iterable<K>> keyExtractor) {
        this.keyExtractor = keyExtractor;
        this.indirectResultMap = new HashMap<>();
    }

    /**
     * This is implemented for convenience rather than add / clear methods.
     */
    public void reload(Collection<R> results) {
        indirectResultMap.clear();
        results.forEach(result -> {
            for (K directKey : keyExtractor.apply(result)) {
                indirectResultMap.computeIfAbsent(directKey, k -> new ArrayList<>()).add(result);
            }
        });
    }

    public Collection<R> getAll(K key) {
        return indirectResultMap.getOrDefault(key, Collections.emptySet());
    }
}
