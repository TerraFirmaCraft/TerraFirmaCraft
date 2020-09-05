/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFluidMixing;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFoodPreservation;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFoodTraits;
import net.dries007.tfc.objects.inventory.ingredient.IngredientFluidItem;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
public class BarrelRecipeWrapper implements IRecipeWrapper
{
    private final BarrelRecipe recipe;
    private final List<ItemStack> itemIngredients;
    private final List<FluidStack> fluidIngredients;
    private final List<ItemStack> outputItems;
    private final FluidStack outputFluid;

    private final FluidStack inputFluid; // Special case, used in BarrelRecipeFluidMixing cases

    public BarrelRecipeWrapper(BarrelRecipe recipe)
    {
        this.recipe = recipe;
        itemIngredients = recipe.getItemIngredient().getValidIngredients();
        fluidIngredients = recipe.getFluidIngredient().getValidIngredients();
        outputItems = new ArrayList<>();
        outputFluid = recipe.getOutputFluid();
        if (recipe instanceof BarrelRecipeFoodTraits)
        {
            // Special cased to show output food with applied trait
            BarrelRecipeFoodTraits recipeFoodTraits = (BarrelRecipeFoodTraits) recipe;
            FluidStack fluid = fluidIngredients.size() > 0 ? fluidIngredients.get(0) : null;
            for (ItemStack ingredient : itemIngredients)
            {
                outputItems.addAll(recipeFoodTraits.getOutputItem(fluid, ingredient));
            }
            inputFluid = null;
        }
        else if (recipe instanceof BarrelRecipeFoodPreservation)
        {
            // Special cased to show output food with applied trait
            BarrelRecipeFoodPreservation recipePreservation = (BarrelRecipeFoodPreservation) recipe;
            FluidStack fluid = fluidIngredients.size() > 0 ? fluidIngredients.get(0) : null;
            for (ItemStack ingredient : itemIngredients)
            {
                recipePreservation.onBarrelSealed(fluid, ingredient);
                outputItems.add(ingredient);
            }
            inputFluid = null;
        }
        else if (recipe instanceof BarrelRecipeFluidMixing)
        {
            // Special cased to show a fluid in the slot, like Alloy category.
            // Also make the scale of buckets for more readability (if possible).
            // If this conversion overflows the capacity of a barrel it will fix itself to the maximum capacity
            IngredientFluidItem ingredient = (IngredientFluidItem) recipe.getItemIngredient();
            inputFluid = ingredient.getFluid();
            int highestFluid = Math.max(inputFluid.amount, fluidIngredients.get(0).amount);
            if (outputFluid != null && highestFluid < outputFluid.amount)
            {
                highestFluid = outputFluid.amount;
            }
            double maximumMultiplier = (double) ConfigTFC.Devices.BARREL.tank / highestFluid; // Fix the multiplier to the maximum allowed value into a barrel
            double multiplier = Math.min((double) Fluid.BUCKET_VOLUME / inputFluid.amount, maximumMultiplier);
            inputFluid.amount *= multiplier;
            fluidIngredients.forEach(x -> x.amount *= multiplier);
            if (outputFluid != null)
            {
                outputFluid.amount *= multiplier;
            }
        }
        else
        {
            outputItems.add(recipe.getOutputStack());
            inputFluid = null;
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        if (isFluidMixing())
        {
            List<List<FluidStack>> inputs = new ArrayList<>();
            inputs.add(fluidIngredients); // Input fluids
            inputs.add(Collections.singletonList(inputFluid)); // Input container fluid
            ingredients.setInputLists(VanillaTypes.FLUID, inputs);
        }
        else
        {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(itemIngredients));
            ingredients.setInputLists(VanillaTypes.FLUID, Collections.singletonList(fluidIngredients));
        }
        if (outputItems.size() > 0)
        {
            ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(outputItems));
        }
        if (outputFluid != null)
        {
            ingredients.setOutput(VanillaTypes.FLUID, outputFluid);
        }
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        String text;
        if (recipe.getDuration() > 0)
        {
            if (recipe.getDuration() / ICalendar.TICKS_IN_HOUR < 25)
            {
                text = I18n.format("jei.tooltips.tfc.barrel.duration.short", recipe.getDuration() / ICalendar.TICKS_IN_HOUR);
            }
            else
            {
                text = I18n.format("jei.tooltips.tfc.barrel.duration", recipe.getDuration() / ICalendar.TICKS_IN_DAY);
            }
        }
        else
        {
            text = I18n.format("jei.tooltips.tfc.barrel.instant");
        }
        float x = 61f - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        float y = 46f;
        minecraft.fontRenderer.drawString(text, x, y, 0x000000, false);
    }

    public boolean isFluidMixing()
    {
        return this.inputFluid != null;
    }
}
