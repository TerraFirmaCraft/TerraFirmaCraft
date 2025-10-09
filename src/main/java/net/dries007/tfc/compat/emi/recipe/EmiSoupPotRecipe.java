package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.SoupPotRecipe;

public class EmiSoupPotRecipe extends EmiBasePotRecipe<SoupPotRecipe>
{
    private final int servings;

    public EmiSoupPotRecipe(ResourceLocation id, SoupPotRecipe recipe)
    {
        super(id, recipe, 113, 80);

        int ingredientCount = 0;
        for (Ingredient ingredient : recipe.getItemIngredients())
        {
            if (!ingredient.isEmpty())
            {
                ingredientCount++;
                inputs.add(EmiIngredient.of(ingredient));
            }
        }
        servings = SoupPotRecipe.ingredientsToServings(ingredientCount);

        for (ItemLike soup : TFCItems.SOUPS.values())
        {
            outputs.add(EmiStack.of(soup, servings));
        }
    }

    @Override
    protected void addOutputWidgets(WidgetHolder widgets)
    {
        widgets.add(new CyclingSlotWidget(outputs, 90, 24, 25)).recipeContext(this);
    }

    private static class CyclingSlotWidget extends SlotWidget
    {
        private final float cycleTime;
        private final EmiIngredient[] ingredients;
        private float currentTime = 0;
        private int index = 0;

        public CyclingSlotWidget(List<? extends EmiIngredient> ingredients, int x, int y, float cycleTime)
        {
            super(ingredients.getFirst(), x, y);
            this.ingredients = ingredients.toArray(EmiIngredient[]::new);
            this.cycleTime = cycleTime;
        }

        @Override
        public EmiIngredient getStack()
        {
            return ingredients[index];
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            currentTime += delta;
            if (currentTime >= cycleTime)
            {
                currentTime -= cycleTime;
                index = (index + 1) % ingredients.length;
            }
            super.render(draw, mouseX, mouseY, delta);
        }
    }
}
