package net.dries007.tfc.objects.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

/**
 * This is a simple LRU cache of each recipe type
 * Since there's a lot of cases (especially with some recipes) where the same recipe is likely to be queried again and again, we keep a cache of the last recipe and check that first.
 */
public enum RecipeCache
{
    INSTANCE;

    private final Map<IRecipeType<?>, IRecipe<?>> recipes;

    RecipeCache()
    {
        recipes = new HashMap<>();
    }

    // todo: figure out when to call this
    public void invalidate()
    {
        recipes.clear();
    }

    @SuppressWarnings("unchecked")
    public <C extends IInventory, R extends IRecipe<C>> Optional<R> get(IRecipeType<R> type, World world, C inv)
    {
        R lastRecipe = (R) recipes.get(type);
        if (lastRecipe != null && lastRecipe.matches(inv, world))
        {
            // Matched cache, return directly
            return Optional.of(lastRecipe);
        }
        else
        {
            return world.getRecipeManager().getRecipe(type, inv, world).map(r -> {
                // If recipe is present, update cache
                recipes.put(type, r);
                return r;
            });
        }
    }
}
