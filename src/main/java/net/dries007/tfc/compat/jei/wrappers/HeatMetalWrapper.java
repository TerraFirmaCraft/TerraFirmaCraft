/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.recipes.heat.HeatRecipeMetalMelting;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.metal.ItemIngot;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

@ParametersAreNonnullByDefault
public class HeatMetalWrapper implements IRecipeWrapper
{
    private HeatRecipeMetalMelting recipe;

    public HeatMetalWrapper(HeatRecipeMetalMelting recipe)
    {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        Metal metal = recipe.getMetal();
        List<List<ItemStack>> allInputs = new ArrayList<>();
        NonNullList<ItemStack> possibleSmeltable = NonNullList.create();
        possibleSmeltable.add(new ItemStack(ItemIngot.get(metal, Metal.ItemType.INGOT)));
        for (Ore ore : TFCRegistries.ORES.getValuesCollection())
        {
            if (ore.getMetal() == metal)
            {
                possibleSmeltable.add(new ItemStack(ItemOreTFC.get(ore)));
            }
        }
        allInputs.add(possibleSmeltable);
        ingredients.setInputLists(VanillaTypes.ITEM, allInputs);

        ItemStack mold = new ItemStack(ItemMold.get(Metal.ItemType.INGOT));
        IFluidHandler cap = mold.getCapability(FLUID_HANDLER_CAPABILITY, null);

        if (cap instanceof IMoldHandler)
        {
            IMoldHandler moldHandler = (IMoldHandler) cap;
            moldHandler.fill(new FluidStack(FluidsTFC.getMetalFluid(metal), 100), true);
        }
        ingredients.setOutput(VanillaTypes.ITEM, mold);
    }

    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        float x = 60f;
        float y = 4f;
        String text = Heat.getTooltip(recipe.getMetal().getMeltTemp());
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
    }

}
