/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.util.data.KnappingType;

public class KnappingRecipeCategory<T extends KnappingRecipe> extends BaseRecipeCategory<T>
{
    private static final String INPUT_SLOT_NAME = "input";

    private final KnappingType knappingType;
    private final IGuiHelper helper;

    public KnappingRecipeCategory(RecipeType<T> type, IGuiHelper helper, KnappingType knappingType)
    {
        super(type, helper, helper.createBlankDrawable(155, 82), knappingType.icon());

        this.knappingType = knappingType;
        this.helper = helper;
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 106, 33);
        arrowAnimated.draw(stack, 106, 33);
        IDrawable high = getTexture(recipeSlots, false);
        IDrawable low = getTexture(recipeSlots, true);

        final int height = recipe.getPattern().getHeight();
        final int width = recipe.getPattern().getWidth();
        final boolean osr = recipe.getPattern().defaultIsOn();
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
                            high.draw(stack, 21 + x * 16, 1 + y * 16);
                        }
                    }
                    else if (low != null)
                    {
                        low.draw(stack, 21 + x * 16, 1 + y * 16);
                    }
                }
                else
                {
                    // out of bounds
                    if (osr)
                    {
                        if (high != null)
                        {
                            high.draw(stack, 21 + x * 16, 1 + y * 16);
                        }
                    }
                    else if (low != null)
                    {
                        low.draw(stack, 21 + x * 16, 1 + y * 16);
                    }
                }
            }
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        final SizedIngredient inputItem = recipe.getIngredient() != null
            // If this knapping recipe has an ingredient, we need to apply the count of the type's ingredient to it
            // See TerraFirmaCraft#2725
            ? new SizedIngredient(recipe.getIngredient(), recipe.knappingType().get().inputItem().count())
            : recipe.knappingType().get().inputItem();
        final IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 0, 33);
        inputSlot.addItemStacks(collapse(inputItem)).setSlotName(INPUT_SLOT_NAME);
        inputSlot.setBackground(slot, -1, -1);

        final IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 137, 33);
        outputSlot.addItemStack(recipe.getResultItem(registryAccess()));
        outputSlot.setBackground(slot, -1, -1);
    }

    @Nullable
    private IDrawable getTexture(IRecipeSlotsView slots, boolean disabled)
    {
        if (disabled && !knappingType.hasOffTexture())
        {
            return null;
        }
        return slots.findSlotByName(INPUT_SLOT_NAME)
            .flatMap(slot -> slot.getDisplayedIngredient(JEIIntegration.ITEM_STACK))
            .map(displayed -> {
                final ResourceLocation high = KnappingScreen.getButtonLocation(displayed.getItem(), disabled);
                return helper.drawableBuilder(high, 0, 0, 16, 16).setTextureSize(16, 16).build();
            })
            .orElse(null);
    }
}
