package net.dries007.tfc.compat.emi.recipe;

import java.util.Arrays;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public abstract class GenericRecipe<T extends Recipe<?>> extends BasicEmiRecipe
{
    protected final T recipe;

    public GenericRecipe(EmiRecipeCategory category, ResourceLocation id, T recipe, int width, int height)
    {
        super(category, id, width, height);
        this.recipe = recipe;
    }

    protected static RegistryAccess registryAccess()
    {
        return ClientHelpers.getLevelOrThrow().registryAccess();
    }

    protected static EmiIngredient toIngredient(SizedFluidIngredient ingredient)
    {
        return EmiIngredient.of(Arrays.stream(ingredient.getFluids()).map(s -> EmiStack.of(s.getFluid())).toList(), ingredient.amount());
    }

    protected static EmiIngredient toIngredient(SizedIngredient ingredient)
    {
        return EmiIngredient.of(ingredient.ingredient(), ingredient.count());
    }

    protected static EmiIngredient toIngredient(BlockIngredient ingredient)
    {
        return EmiIngredient.of(ingredient.all().map(ItemStack::new).filter(item -> !item.isEmpty()).map(EmiStack::of).toList());
    }
}
