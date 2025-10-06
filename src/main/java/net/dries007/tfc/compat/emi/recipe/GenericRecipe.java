package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import net.dries007.tfc.client.ClientHelpers;

public abstract class GenericRecipe<T extends Recipe<?>> extends BasicEmiRecipe
{
    protected final T recipe;

    public GenericRecipe(EmiRecipeCategory category, ResourceLocation id, T recipe, int width, int height)
    {
        super(category, id, width, height);
        this.recipe = recipe;
    }

    public static RegistryAccess registryAccess()
    {
        return ClientHelpers.getLevelOrThrow().registryAccess();
    }
}
