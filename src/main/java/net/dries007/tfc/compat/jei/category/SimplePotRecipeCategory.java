/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei.category;

import java.util.List;

import net.minecraft.world.item.ItemStack;

import net.minecraftforge.fluids.FluidStack;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;

public class SimplePotRecipeCategory extends PotRecipeCategory<PotRecipe>
{
    public SimplePotRecipeCategory(RecipeType<PotRecipe> type, IGuiHelper helper)
    {
        super(type, helper, helper.createBlankDrawable(154, 63));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PotRecipe potRecipe, IFocusGroup focuses)
    {
        super.setRecipe(builder, potRecipe, focuses);
        SimplePotRecipe recipe = (SimplePotRecipe) potRecipe;

        FluidStack displayFluid = recipe.getDisplayFluid();
        if (!displayFluid.isEmpty())
        {
            IRecipeSlotBuilder fluidOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 26);
            fluidOutput.addIngredient(VanillaTypes.FLUID, displayFluid);
            fluidOutput.setFluidRenderer(1, false, 16, 16);
            fluidOutput.setSlotName("fluidOutput");
        }

        List<ItemStack> outputStacks = recipe.getOutputStacks();
        if (!outputStacks.isEmpty())
        {
            // todo:  temporarily just adding the first output so JEI picks up the output stuff, we need to show up to all 5
            IRecipeSlotBuilder itemOutput = builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 6);
            itemOutput.addItemStack(outputStacks.get(0));
            itemOutput.setSlotName("output1");
        }
    }

    @Override
    public void draw(PotRecipe recipe, IRecipeSlotsView recipeSlots, PoseStack stack, double mouseX, double mouseY)
    {
        super.draw(recipe, recipeSlots, stack, mouseX, mouseY);
        if (recipeSlots.findSlotByName("fluidOutput").isPresent())
        {
            slot.draw(stack, 131, 25);
        }
        if (recipeSlots.findSlotByName("output1").isPresent())
        {
            slot.draw(stack, 131, 5);
        }
    }
}
