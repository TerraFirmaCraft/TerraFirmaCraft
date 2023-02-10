/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import org.jetbrains.annotations.Nullable;

public class KnappingRecipeCategory<T extends KnappingRecipe> extends BaseRecipeCategory<T>
{
    @Nullable
    private final IDrawable high, low;

    public KnappingRecipeCategory(RecipeType<T> type, IGuiHelper helper, ItemStack icon, @Nullable ResourceLocation high, @Nullable ResourceLocation low)
    {
        super(type, helper, helper.createBlankDrawable(135, 82), icon);
        this.high = high == null ? null : helper.drawableBuilder(high, 0, 0, 16, 16).setTextureSize(16, 16).build();
        this.low = low == null ? null : helper.drawableBuilder(low, 0, 0, 16, 16).setTextureSize(16, 16).build();
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 86, 33);
        arrowAnimated.draw(stack, 86, 33);
        IDrawable high = getHigh(recipe, recipeSlots);
        IDrawable low = getLow(recipe, recipeSlots);

        final int height = recipe.getPattern().getHeight();
        final int width = recipe.getPattern().getWidth();
        final boolean osr = recipe.getPattern().isOutsideSlotRequired();
        final int offsetX = Math.floorDiv(5 - width, 2);
        final int offsetY = Math.floorDiv(5 - height, 2);

        for (int y = 0; y < 5; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                final int yd = y - offsetY;
                final int xd = x - offsetX;
                if (0 <= yd && yd < height && 0 <= xd && xd < width)
                {
                    if (recipe.getPattern().get(xd, yd))
                    {
                        if (high != null)
                        {
                            high.draw(stack, 1 + x * 16, 1 + y * 16);
                        }
                    }
                    else if (low != null)
                    {
                        low.draw(stack, 1 + x * 16, 1 + y * 16);
                    }
                }
                else
                {
                    // out of bounds
                    if (osr)
                    {
                        if (high != null)
                        {
                            high.draw(stack, 1 + x * 16, 1 + y * 16);
                        }
                    }
                    else if (low != null)
                    {
                        low.draw(stack, 1 + x * 16, 1 + y * 16);
                    }
                }
            }
        }
    }

    @Nullable
    public IDrawable getHigh(T recipe, IRecipeSlotsView recipeSlots)
    {
        return high;
    }

    @Nullable
    public IDrawable getLow(T recipe, IRecipeSlotsView recipeSlots)
    {
        return low;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33);
        outputSlot.addItemStack(recipe.getResultItem());
        outputSlot.setBackground(slot, -1, -1);
    }
}
