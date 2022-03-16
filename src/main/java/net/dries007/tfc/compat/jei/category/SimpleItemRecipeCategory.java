/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.recipes.SimpleItemRecipe;

public abstract class SimpleItemRecipeCategory<T extends SimpleItemRecipe> extends BaseRecipeCategory<T>
{
    public SimpleItemRecipeCategory(RecipeType<T> type, IGuiHelper helper, ItemStack icon)
    {
        super(type, helper, helper.createBlankDrawable(120, 38), icon);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 1, 17);
        IRecipeSlotBuilder toolSlot = builder.addSlot(RecipeIngredientRole.CATALYST, 21, 17);
        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 85, 17);

        inputSlot.addIngredients(recipe.getIngredient());
        toolSlot.addIngredients(Ingredient.of(getToolTag()));
        outputSlot.addItemStack(recipe.getResultItem());
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 50, 16);
        arrowAnimated.draw(stack, 50, 16);
        slot.draw(stack, 0, 16);
        slot.draw(stack, 20, 16);
        slot.draw(stack, 84, 16);
    }

    protected abstract TagKey<Item> getToolTag();
}
