/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.util.tooltip.Tooltips;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.util.Metal;

public class WeldingRecipeCategory extends BaseRecipeCategory<WeldingRecipe>
{
    public WeldingRecipeCategory(RecipeType<RecipeHolder<WeldingRecipe>> type, IGuiHelper helper)
    {
        super(type, helper, 118, 26, new ItemStack(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HAMMER).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, WeldingRecipe recipe, IFocusGroup focuses)
    {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addIngredients(recipe.getFirstInput())
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 5)
            .addIngredients(recipe.getSecondInput())
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, 46, 5)
            .addIngredients(Ingredient.of(TFCTags.Items.WELDING_FLUX))
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 5)
            .addItemStack(recipe.getResultItem(registryAccess()))
            .setBackground(slot, -1, -1)
            .addRichTooltipCallback((view, tooltip) -> {
                if (recipe.getTier() > 0)
                {
                    tooltip.add(Component.translatable("tfc.tooltip.anvil_tier_required", Tooltips.tier(recipe.getTier())));
                }
            });
    }

    @Override
    public void draw(WeldingRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        arrow.draw(stack, 68, 5);
        arrowAnimated.draw(stack, 68, 5);
    }
}
