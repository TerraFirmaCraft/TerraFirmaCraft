package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.DrawableWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.TextureWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.util.data.KnappingPattern;

public class EmiKnappingRecipe extends GenericRecipe<KnappingRecipe>
{
    private final SizedIngredient inputItem;
    private final PatternWidget pattern;

    public EmiKnappingRecipe(EmiRecipeCategory category, ResourceLocation id, KnappingRecipe recipe)
    {
        super(category, id, recipe, 250, 250);

        inputItem = recipe.getIngredient() != null
            // If this knapping recipe has an ingredient, we need to apply the count of the type's ingredient to it
            // See TerraFirmaCraft#2725
            ? new SizedIngredient(recipe.getIngredient(), recipe.knappingType().get().inputItem().count())
            : recipe.knappingType().get().inputItem();

        inputs.add(EmiIngredient.of(recipe.getIngredient()));
        outputs.add(EmiStack.of(recipe.getResultItem(registryAccess())));
        pattern = new PatternWidget(recipe, recipe.getPattern(), inputItem.ingredient(), 5, 5);
    }

    @Override
    public int getDisplayHeight()
    {
        return pattern.getBounds().bottom() + 5;
    }

    @Override
    public int getDisplayWidth()
    {
        return pattern.getBounds().right() + 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        widgets.add(pattern);
        Bounds bounds = pattern.getBounds();
        int height = bounds.y() - TextWidget.Alignment.CENTER.offset(bounds.height());
        Widget arrow = widgets.addFillingArrow(bounds.right() + 4, height - 8, 3000);
        widgets.addSlot(outputs.getFirst(), arrow.getBounds().right() + 4, height - 9).recipeContext(this);
    }

    private static class PatternWidget extends Widget
    {
        private final int x;
        private final int y;
        private final int width;
        private final int height;
        private final KnappingPattern pattern;
        private final ResourceLocation high;
        private final ResourceLocation low;


        public PatternWidget(KnappingRecipe recipe, KnappingPattern pattern, Ingredient input, int x, int y)
        {
            this.x = x;
            this.y = y;
            this.width = KnappingPattern.MAX_WIDTH * 16;
            this.height = KnappingPattern.MAX_HEIGHT * 16;
            this.pattern = pattern;
            ItemStack stack = input.getItems()[0];
            high = KnappingScreen.getHighTexture(stack);
            low = KnappingScreen.getLowTexture(recipe.knappingType().get(), stack);
        }

        @Override
        public Bounds getBounds()
        {
            return new Bounds(x, y, width, height);
        }

        @Override
        public void render(GuiGraphics draw, int mouseX, int mouseY, float delta)
        {
            draw.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xffaaaaaa);
            for (int xi = 0; xi < KnappingPattern.MAX_WIDTH; xi++)
            {
                for (int yi = 0; yi < KnappingPattern.MAX_HEIGHT; yi++)
                {
                    int xp = x + 16 * xi;
                    int yp = y + 16 * yi;
                    if (pattern.get(xi, yi) && xi < pattern.getWidth() && yi < pattern.getHeight())
                    {
                        if (high != null)
                        {
                            draw.blit(high, xp, yp, 0, 0, 16, 16, 16, 16);
                        }
                    }
                    else
                    {
                        if (low != null)
                        {
                            draw.blit(low, xp, yp, 0, 0, 16, 16, 16, 16);
                        }
                    }
                }
            }

        }
    }
}
