/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.quern.QuernRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import vazkii.patchouli.api.VariableHolder;

@SuppressWarnings("unused")
public class QuernComponent extends IngredientItemStackComponent
{
    @VariableHolder
    @SerializedName("recipe")
    public String recipeName;

    @Override
    public void build(int componentX, int componentY, int pageNum)
    {
        super.build(componentX, componentY, pageNum);
        QuernRecipe recipe = TFCRegistries.QUERN.getValue(new ResourceLocation(recipeName));
        if (recipe == null)
        {
            TerraFirmaCraft.getLog().warn("Unknown recipe in QuernComponent: " + recipeName);
        }
        IIngredient<ItemStack> ingredient = recipe.getIngredients().get(0);
        inputIngredient = TFCPatchouliPlugin.getIngredient(ingredient);
        outputStack = recipe.getOutputItem(ingredient.getValidIngredients().get(0).copy());
    }
}
