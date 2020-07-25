package net.dries007.tfc.compat.patchouli;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import vazkii.patchouli.api.VariableHolder;

@SuppressWarnings("unused")
public class SimpleHeatRecipeComponent extends IngredientItemStackComponent {
    @VariableHolder
    @SerializedName("recipe")
    public String recipeName;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        Objects.requireNonNull(recipeName, "Recipe name is null?");
        HeatRecipe recipe = TFCRegistries.HEAT.getValue(new ResourceLocation(recipeName));
        Objects.requireNonNull(recipe, "Unknown Heat recipe: " + recipeName);
        IIngredient<ItemStack> ingredient = recipe.getIngredients().get(0);
        inputIngredient = TFCPatchouliPlugin.getIngredient(ingredient);
        outputStack = recipe.getOutputStack(ingredient.getValidIngredients().get(0).copy());

        super.build(componentX, componentY, pageNum);
    }
}
