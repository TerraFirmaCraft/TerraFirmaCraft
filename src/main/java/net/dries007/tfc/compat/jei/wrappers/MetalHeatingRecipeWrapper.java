/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.FluidsTFC;

public class MetalHeatingRecipeWrapper implements IRecipeWrapper
{
    private final ItemStack stack;
    private final FluidStack output;
    private final float meltingTemp;

    public MetalHeatingRecipeWrapper(ItemStack stack, Metal metal, int amount, float meltingTemp)
    {
        this.meltingTemp = meltingTemp;
        this.stack = stack;
        output = new FluidStack(FluidsTFC.getFluidFromMetal(metal), amount);
    }

    @Override
    public void getIngredients(IIngredients recipeIngredients)
    {
        recipeIngredients.setInput(VanillaTypes.ITEM, stack);
        recipeIngredients.setOutput(VanillaTypes.FLUID, output);
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
