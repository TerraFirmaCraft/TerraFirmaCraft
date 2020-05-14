package net.dries007.tfc.objects.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

/**
 * This is a simple cache of the most recently accessed recipe for any {@link IRecipeType}
 */
public enum RecipeCache
{
    INSTANCE;

    private final Map<IRecipeType<?>, IRecipe<?>> recipes;

    RecipeCache()
    {
        recipes = new HashMap<>();
    }

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
