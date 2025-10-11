package net.dries007.tfc.compat.emi.recipe;

import java.util.Arrays;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiScrapingRecipe extends AutoLayoutRecipe<ScrapingRecipe>
{

    public EmiScrapingRecipe(ResourceLocation id, ScrapingRecipe recipe)
    {
        super(EmiIntegration.SCRAPING, id, recipe);
    }

    @Override
    protected void processRecipe()
    {
        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem(EmiHelpers.registryAccess())));
        for (ItemStack extra : EmiHelpers.collapse(Arrays.asList(recipe.getIngredient().getItems()), recipe.getExtraDrop()))
        {
            if (!extra.isEmpty())
            {
                outputs.add(EmiStack.of(extra));
            }
        }
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiScrapingRecipe scraping)
        {
            return outputs.size() - scraping.outputs.size();
        }
        return super.compareTo(other);
    }
}
