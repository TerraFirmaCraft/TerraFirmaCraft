/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.common.recipes.ingredients.ItemStackIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

// todo: make this nice and show extra cool info
public class BarrelRecipeCategory<T extends BarrelRecipe> extends BaseRecipeCategory<T>
{
    public BarrelRecipeCategory(RecipeType<T> type, IGuiHelper helper, int height, Wood iconType)
    {
        super(type, helper, helper.createBlankDrawable(118, height), new ItemStack(TFCBlocks.WOODS.get(iconType).get(Wood.BlockType.BARREL).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, T recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputItem = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        if (recipe.getInputFluid() != FluidStackIngredient.EMPTY)
        {
            IRecipeSlotBuilder inputFluid = builder.addSlot(RecipeIngredientRole.INPUT, 26, 5);
            // todo: reflect fluid stack amount of the ingredient
            inputFluid.addIngredients(VanillaTypes.FLUID, collapse(recipe.getInputFluid()));
            inputFluid.setFluidRenderer(1, false, 16, 16);
        }
        IRecipeSlotBuilder outputItem = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5);
        if (!recipe.getOutputFluid().isEmpty())
        {
            IRecipeSlotBuilder outputFluid = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 5);
            outputFluid.addIngredient(VanillaTypes.FLUID, recipe.getOutputFluid());
            outputFluid.setFluidRenderer(1, false, 16, 16);
        }

        // todo: this should reflect the count part of the itemstack ingredient
        inputItem.addIngredients(recipe.getInputItem().ingredient());


        outputItem.addIngredients(itemStackProviderIngredient(recipe.getOutputItem(), recipe.getInputItem()));
    }

    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 5, 4);
        if (recipe.getInputFluid() != FluidStackIngredient.EMPTY)
        {
            slot.draw(stack, 25, 4);
        }
        slot.draw(stack, 75, 4);
        if (!recipe.getOutputFluid().isEmpty())
        {
            slot.draw(stack, 95, 4);
        }

        arrow.draw(stack, 48, 5);
        arrowAnimated.draw(stack, 48, 5);
    }

    protected Ingredient itemStackProviderIngredient(ItemStackProvider output, ItemStackIngredient input)
    {
        // todo: this sucks and may not be properly sensitive to everything
        // todo: maybe we should just list the item stack modifiers
        ItemStack[] possibleItems = input.ingredient().getItems();
        List<ItemStack> items = new ArrayList<>(possibleItems.length);
        for (ItemStack item : possibleItems)
        {
            items.add(output.getStack(item));
        }
        return Ingredient.of(items.stream());
    }
}
