/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.SimpleItemRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;

public abstract class SimpleItemRecipeCategory<T extends SimpleItemRecipe> extends BaseRecipeCategory<T>
{
    public SimpleItemRecipeCategory(RecipeType<T> type, IGuiHelper helper, ItemStack icon)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        final IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        inputSlot.setBackground(slot, -1, -1);
        IRecipeSlotBuilder toolSlot = null;
        if (getTool() != null)
        {
            toolSlot = builder.addSlot(RecipeIngredientRole.CATALYST, 26, 5);
        }
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5);

        final Ingredient ingredient = recipe.getIngredient();
        final List<ItemStack> inputList = List.of(ingredient.getItems());
        inputSlot.addIngredients(JEIIntegration.ITEM_STACK, inputList);

        if (toolSlot != null)
        {
            toolSlot.addIngredients(getTool());
            toolSlot.setBackground(slot, -1, -1);
        }

        outputSlot.addItemStacks(collapse(inputList, recipe.getResult()));
        outputSlot.setBackground(slot, -1, -1);

        if (!addItemsToOutputSlot(recipe, outputSlot, inputList))
        {
            builder.createFocusLink(inputSlot, outputSlot);
        }
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, getTool() == null ? 36 : 48, 5);
        arrowAnimated.draw(stack, getTool() == null ? 36 : 48, 5);
    }

    protected boolean addItemsToOutputSlot(T recipe, IRecipeSlotBuilder output, List<ItemStack> inputs)
    {
        return false;
    }


    /**
     * @return The ingredient used in the recipe. One of this or {@link #getToolTag()} should be overridden.
     */
    @Nullable
    @Contract(pure = true)
    protected Ingredient getTool()
    {
        return getToolTag() == null ? null : Ingredient.of(getToolTag());
    }

    /**
     * @return The ingredient used in the recipe. One of {@link #getTool()} or this should be overridden.
     * Don't call this implementation, prefer calling {@link #getTool()} as it is more generic.
     */
    @Nullable
    @Contract(pure = true)
    protected TagKey<Item> getToolTag()
    {
        return null;
    }
}
