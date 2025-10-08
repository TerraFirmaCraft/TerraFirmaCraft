package net.dries007.tfc.compat.emi.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiJamPotRecipe extends GenericRecipe<JamPotRecipe>
{
    public EmiJamPotRecipe(ResourceLocation id, JamPotRecipe recipe)
    {
        super(EmiIntegration.POT, id, recipe, 175, 50);

        inputs.add(toIngredient(recipe.getFluidIngredient()));
        HashMap<Ingredient, Integer> stackedIngredients = new HashMap<>();
        for (var ingredient : recipe.getItemIngredients())
        {
            stackedIngredients.compute(ingredient, (x, i) -> i == null ? 1 : i + 1);

        }
        stackedIngredients.forEach(((ingredient, count) -> inputs.add(EmiIngredient.of(ingredient, count))));

        outputs.add(EmiStack.of(recipe.getResultItem(registryAccess())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        for (int i = 0; i < inputs.size(); i++)
        {
            widgets.addSlot(inputs.get(i), 6 + 18 * i, 6);
        }
        widgets.addSlot(outputs.getFirst(), 40, 40).recipeContext(this);
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiJamPotRecipe jamPot)
        {
            return outputs.size() - jamPot.outputs.size();
        }
        return super.compareTo(other);
    }
}
