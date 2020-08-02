/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import vazkii.patchouli.api.VariableHolder;

@SuppressWarnings("unused")
public class AnvilComponent extends IngredientItemStackComponent
{
    @VariableHolder
    @SerializedName("recipe")
    public String recipeName;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        Objects.requireNonNull(recipeName, "Recipe name is null?");
        AnvilRecipe recipe = TFCRegistries.ANVIL.getValue(new ResourceLocation(recipeName));
        Objects.requireNonNull(recipe, "Unknown anvil recipe: " + recipeName);
        IIngredient<ItemStack> ingredient = recipe.getIngredients().get(0);
        inputIngredient = TFCPatchouliPlugin.getIngredient(ingredient);
        outputStack = recipe.getOutput(ingredient.getValidIngredients().get(0).copy()).get(0);

        super.build(componentX, componentY, pageNum);
    }
}
