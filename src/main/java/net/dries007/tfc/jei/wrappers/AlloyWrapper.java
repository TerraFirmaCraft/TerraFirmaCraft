package net.dries007.tfc.jei.wrappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.dries007.tfc.jei.IJEIRecipeWrapper;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlloyWrapper extends TFCRecipeWrapper
{
    private String[] slotContent = {"", "", "", ""};

    public AlloyWrapper(IJEIRecipeWrapper recipe)
    {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        NonNullList<IIngredient<FluidStack>> fluidIngredients = getRecipeWrapper().getFluidIngredients();
        List<List<FluidStack>> allInputs = new ArrayList<>();
        for (int i = 0; i < fluidIngredients.size(); i += 2)
        {
            FluidStack min = fluidIngredients.get(i).getValidInputList().get(0);
            FluidStack max = fluidIngredients.get(i + 1).getValidInputList().get(0);
            NonNullList<FluidStack> input = NonNullList.create();
            slotContent[i / 2] = min.amount + "-" + max.amount + "%";
            input.add(min);
            allInputs.add(input);
        }
        ingredients.setInputLists(VanillaTypes.FLUID, allInputs);
        ingredients.setOutput(VanillaTypes.ITEM, getRecipeWrapper().getItemOutputs().get(0));
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        for (int i = 0; i < 4; i++)
        {
            float x = 14f + i * 60f;
            float y = 15f;
            String text = slotContent[i];
            x = x - minecraft.fontRenderer.getStringWidth(text) / 3.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            minecraft.fontRenderer.drawString(text, x, y, 0x000000, false);
            GlStateManager.popMatrix();
        }
    }
}
