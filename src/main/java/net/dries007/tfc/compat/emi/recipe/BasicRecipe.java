package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public abstract class BasicRecipe<T extends Recipe<?>> extends BasicEmiRecipe implements ComparableRecipe
{
    protected final T recipe;

    public BasicRecipe(EmiRecipeCategory category, ResourceLocation id, T recipe, int width, int height)
    {
        super(category, id, width, height);
        this.recipe = recipe;
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        ResourceLocation otherId = other.getId();
        if (otherId != null)
        {
            return id.compareTo(other.getId());
        }
        return 0;
    }

}
