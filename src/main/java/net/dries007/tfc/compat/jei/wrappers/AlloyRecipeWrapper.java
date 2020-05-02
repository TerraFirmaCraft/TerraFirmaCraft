/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.recipes.AlloyRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.FluidsTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlloyRecipeWrapper implements IRecipeWrapper
{
    private final String[] slotContent = {"", "", "", ""};
    private final AlloyRecipe recipe;

    public AlloyRecipeWrapper(AlloyRecipe recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        int i = 0;
        List<List<FluidStack>> allInputs = new ArrayList<>();
        for (Metal metal : recipe.getMetals().keySet())
        {
            int min = (int) (recipe.getMetals().get(metal).getMin() * 100);
            int max = (int) (recipe.getMetals().get(metal).getMax() * 100);
            slotContent[i] = min + "-" + max + "%";
            FluidStack fluidInput = new FluidStack(FluidsTFC.getFluidFromMetal(metal), 1000);
            allInputs.add(Lists.newArrayList(fluidInput));
            i++;
        }
        ingredients.setInputLists(VanillaTypes.FLUID, allInputs);
        ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(FluidsTFC.getFluidFromMetal(recipe.getResult()), 1000));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        for (int i = 0; i < 4; i++)
        {
            int row = i / 2;
            int column = i % 2;
            float x = 20f + column * 60f;
            float y = 17f + row * 26f;
            String text = slotContent[i];
            minecraft.fontRenderer.drawString(text, x, y, 0x000000, false);
        }
    }
}
