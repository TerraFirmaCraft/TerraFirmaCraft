/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.recipes.RockKnappingRecipe;

public class RockKnappingRecipeCategory extends KnappingRecipeCategory<RockKnappingRecipe>
{
    private static final String ROCK_SLOT_NAME = "input";

    private final IDrawableStatic blank;
    private final IGuiHelper helper;

    public RockKnappingRecipeCategory(RecipeType<RockKnappingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, new ItemStack(TFCBlocks.ROCK_BLOCKS.get(Rock.CHERT).get(Rock.BlockType.LOOSE).get()), null, null);
        this.helper = helper;
        this.blank = helper.createBlankDrawable(1, 1);
    }

    @Override
    public void draw(RockKnappingRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        slot.draw(stack, 88, 60);
        super.draw(recipe, recipeSlots, stack, mouseX, mouseY);
    }

    @Nullable
    @Override
    public IDrawable getHigh(RockKnappingRecipe recipe, IRecipeSlotsView recipeSlots)
    {
        IRecipeSlotView inputSlot = recipeSlots.findSlotByName(ROCK_SLOT_NAME).get();
        ItemStack displayed = inputSlot.getDisplayedIngredient(VanillaTypes.ITEM).orElse(null);
        if (displayed == null) return blank;

        ResourceLocation high = KnappingScreen.getButtonLocation(displayed.getItem(), false);
        return helper.drawableBuilder(high, 0, 0, 16, 16).setTextureSize(16, 16).build();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RockKnappingRecipe recipe, IFocusGroup focuses)
    {
        IRecipeSlotBuilder inputSlot = builder.addSlot(RecipeIngredientRole.INPUT, 89, 61);
        inputSlot.addIngredients(recipe.getIngredient());
        inputSlot.setSlotName(ROCK_SLOT_NAME);
        super.setRecipe(builder, recipe, focuses);
    }
}
