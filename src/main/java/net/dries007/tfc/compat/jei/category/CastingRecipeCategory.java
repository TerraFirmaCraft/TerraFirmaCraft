/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.compat.jei.JEIIntegration;
import net.dries007.tfc.util.Metal;

public class CastingRecipeCategory extends BaseRecipeCategory<CastingRecipe>
{
    private static final String INPUT_SLOT = "input";

    public CastingRecipeCategory(RecipeType<CastingRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(98, 26), new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT).get()));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CastingRecipe recipe, IFocusGroup focuses)
    {
        final List<ItemStack> inputs = new ArrayList<>();
        for (ItemStack input : recipe.getIngredient().getItems())
        {
            for (FluidStack fluid : recipe.getFluidIngredient().getFluids())
            {
                final ItemStack filled = input.copy();
                final @Nullable IFluidHandler fluidHandler = filled.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandler != null)
                {
                    fluidHandler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                    inputs.add(filled);
                }
            }
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5)
            .addItemStacks(inputs)
            .setSlotName(INPUT_SLOT)
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 5)
            .addIngredients(JEIIntegration.FLUID_STACK, collapse(recipe.getFluidIngredient()))
            .setFluidRenderer(1, false, 16, 16)
            .setBackground(slot, -1, -1);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 5)
            .addItemStack(recipe.getResultItem(registryAccess()))
            .setBackground(slot, -1, -1);
    }

    @Override
    public void draw(CastingRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics stack, double mouseX, double mouseY)
    {
        recipeSlots.findSlotByName(INPUT_SLOT)
            .flatMap(IRecipeSlotView::getDisplayedItemStack)
            .ifPresent(filled -> HeatCapability.setTemperature(filled, Heat.maxVisibleTemperature()));
        arrow.draw(stack, 48, 5);
        arrowAnimated.draw(stack, 48, 5);
    }
}
