package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

public class EmiBarrelRecipe extends BasicEmiRecipe
{
    public EmiBarrelRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height)
    {
        super(category, id, width, height);
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {

    }
}
