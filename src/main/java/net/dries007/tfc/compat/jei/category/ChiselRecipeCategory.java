/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.client.IngameOverlays;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ChiselRecipe;
import net.dries007.tfc.util.Metal;

public class ChiselRecipeCategory extends BaseRecipeCategory<ChiselRecipe>
{
    private final Map<ChiselRecipe.Mode, IDrawableStatic> modes;

    public ChiselRecipeCategory(RecipeType<ChiselRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(118, 26), new ItemStack(TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.CHISEL).get()));
        modes = ImmutableMap.of(
            ChiselRecipe.Mode.SLAB, helper.createDrawable(IngameOverlays.TEXTURE, 40, 58, 20, 20),
            ChiselRecipe.Mode.STAIR, helper.createDrawable(IngameOverlays.TEXTURE, 20, 58, 20, 20),
            ChiselRecipe.Mode.SMOOTH, helper.createDrawable(IngameOverlays.TEXTURE, 0, 58, 20, 20)
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChiselRecipe recipe, IFocusGroup focuses)
    {
        Ingredient chiselIngredient = Ingredient.of(TFCTags.Items.TOOLS_CHISEL);

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addIngredients(collapse(recipe.getIngredient()))
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 5)
            .addIngredients(chiselIngredient)
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5)
            .addItemStack(recipe.getResultItem(null))
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 5)
            .addItemStack(recipe.getItemOutput(ItemStack.EMPTY))
            .setBackground(slot, -1, -1);
    }

    @Override
    public void draw(ChiselRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        modes.get(recipe.getMode()).draw(stack, 48, 3);
    }
}
