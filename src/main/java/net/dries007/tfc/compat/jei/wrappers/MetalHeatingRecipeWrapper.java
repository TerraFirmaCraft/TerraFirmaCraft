/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.recipes.heat.HeatRecipeMetalMelting;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.compat.jei.TFCJEIPlugin;
import net.dries007.tfc.objects.fluids.FluidsTFC;

public class MetalHeatingRecipeWrapper implements IRecipeWrapper
{
    private List<ItemStack> ingredients;
    private FluidStack output;
    private float meltingTemp;

    public MetalHeatingRecipeWrapper(HeatRecipeMetalMelting recipe)
    {
        this.meltingTemp = recipe.getTransformTemp();
        ingredients = new ArrayList<>();
        // Although this looks resource-intensive, it's done one time only
        TFCJEIPlugin.getAllIngredients().forEach(stack -> {
            if (recipe.isValidInput(stack, Metal.Tier.TIER_VI))
            {
                ingredients.add(stack);
            }
        });
        output = new FluidStack(FluidsTFC.getFluidFromMetal(recipe.getMetal()), 1000);
    }

    @Override
    public void getIngredients(IIngredients recipeIngredients)
    {
        List<List<ItemStack>> allInputs = new ArrayList<>();
        allInputs.add(ingredients);
        recipeIngredients.setInputLists(VanillaTypes.ITEM, allInputs);


        List<List<FluidStack>> allOutputs = new ArrayList<>();
        allOutputs.add(Lists.newArrayList(output));
        recipeIngredients.setOutputLists(VanillaTypes.FLUID, allOutputs);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        float x = 60f;
        float y = 4f;
        String text = Heat.getTooltipAlternate(meltingTemp);
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
    }
}
