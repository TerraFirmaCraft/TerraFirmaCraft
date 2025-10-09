package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.joml.Vector2i;

import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;

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
        Vector2i pos = outputSlotPosition();
        widgets.add(new CyclingSlotWidget(EmiIngredient.of(outputs, servings), pos.x, pos.y, 25)).recipeContext(this);
    }

    private static class CyclingSlotWidget extends SlotWidget
    {
        private final float cycleTime;
        private float currentTime = 0;
        private int index = 0;

        public CyclingSlotWidget(EmiIngredient stack, int x, int y, float cycleTime)
        {
            super(stack, x, y);
            this.cycleTime = cycleTime;
        }

        @Override
        public EmiIngredient getStack()
        {
            return super.getStack().getEmiStacks().get(index);
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            currentTime += delta;
            if (currentTime >= cycleTime)
            {
                currentTime -= cycleTime;
                index = (index + 1) % stack.getEmiStacks().size();
            }
            super.render(draw, mouseX, mouseY, delta);
        }
    }
}
