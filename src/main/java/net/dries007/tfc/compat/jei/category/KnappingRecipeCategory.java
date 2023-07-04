/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.minecraft.client.gui.GuiGraphics;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.util.KnappingType;

import org.jetbrains.annotations.Nullable;

public class KnappingRecipeCategory<T extends KnappingRecipe> extends BaseRecipeCategory<T>
{
    private static final String INPUT_SLOT_NAME = "input";

    private final KnappingType knappingType;
    private final IGuiHelper helper;

    public KnappingRecipeCategory(RecipeType<T> type, IGuiHelper helper, KnappingType knappingType)
    {
        super(type, helper, helper.createBlankDrawable(135, 82), knappingType.jeiIcon());

        this.knappingType = knappingType;
        this.helper = helper;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 86, 33);
        arrowAnimated.draw(stack, 86, 33);
        IDrawable high = getTexture(recipeSlots, false);
        IDrawable low = getTexture(recipeSlots, true);

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

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 33);
        outputSlot.addItemStack(recipe.getResultItem(ClientHelpers.getLevelOrThrow().registryAccess()));
        outputSlot.setBackground(slot, -1, -1);
    }

    @Nullable
    private IDrawable getTexture(IRecipeSlotsView slots, boolean disabled)
    {
        if (disabled && !knappingType.usesDisabledTexture())
        {
            return null;
        }
        return slots.findSlotByName(INPUT_SLOT_NAME)
            .flatMap(slot -> slot.getDisplayedIngredient(JEIIntegration.ITEM_STACK))
            .map(displayed -> {
                final ResourceLocation high = KnappingScreen.getButtonLocation(displayed.getItem(), false);
                return helper.drawableBuilder(high, 0, 0, 16, 16).setTextureSize(16, 16).build();
            })
            .orElse(null);
    }
}
