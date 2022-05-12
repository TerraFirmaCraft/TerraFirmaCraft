/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
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
        super(type, helper, helper.createBlankDrawable(118, 26), new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BLACK_BRONZE).get(Metal.ItemType.CHISEL).get()));
        modes = ImmutableMap.of(
            ChiselRecipe.Mode.SLAB, helper.createDrawable(IngameOverlays.TEXTURE, 40, 58, 20, 20),
            ChiselRecipe.Mode.STAIR, helper.createDrawable(IngameOverlays.TEXTURE, 20, 58, 20, 20),
            ChiselRecipe.Mode.SMOOTH, helper.createDrawable(IngameOverlays.TEXTURE, 0, 58, 20, 20)
        );
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ChiselRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 6, 5);
        IRecipeSlotBuilder chiselSlot = builder.addSlot(RecipeIngredientRole.INPUT, 26, 5);

        IRecipeSlotBuilder outputSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5);
        IRecipeSlotBuilder dropSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 5);

        inputSlot.addIngredients(collapse(recipe.getBlockIngredient()));
        if (recipe.getItemIngredient() == null)
        {
            chiselSlot.addIngredients(Ingredient.of(TFCTags.Items.CHISELS));
        }
        else
        {
            chiselSlot.addIngredients(recipe.getItemIngredient());
        }
        outputSlot.addItemStack(new ItemStack(recipe.getBlockRecipeOutput()));
        dropSlot.addItemStack(recipe.getExtraDrop(ItemStack.EMPTY));
    }

    @Override
    public void draw(ChiselRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 5, 4);
        slot.draw(stack, 25, 4);
        slot.draw(stack, 75, 4);
        slot.draw(stack, 95, 4);
        modes.get(recipe.getMode()).draw(stack, 45, 2);
    }
}
