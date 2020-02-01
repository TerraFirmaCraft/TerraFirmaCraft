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
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.recipes.heat.HeatRecipeMetalMelting;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.compat.jei.TFCJEIPlugin;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;

public class MetalHeatingRecipeWrapper implements IRecipeWrapper
{
    private List<ItemStack> ingredients;
    private ItemStack output;
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
        output = new ItemStack(ItemMold.get(Metal.ItemType.INGOT));
        IFluidHandler cap = output.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (cap instanceof IMoldHandler)
        {
            cap.fill(new FluidStack(FluidsTFC.getFluidFromMetal(recipe.getMetal()), 100), true);
        }
    }

    @Override
    public void getIngredients(IIngredients recipeIngredients)
    {
        List<List<ItemStack>> allInputs = new ArrayList<>();
        allInputs.add(ingredients);
        recipeIngredients.setInputLists(VanillaTypes.ITEM, allInputs);


        List<List<ItemStack>> allOutputs = new ArrayList<>();
        allOutputs.add(Lists.newArrayList(output));
        recipeIngredients.setOutputLists(VanillaTypes.ITEM, allOutputs);
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
