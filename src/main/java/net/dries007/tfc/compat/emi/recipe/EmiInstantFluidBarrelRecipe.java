/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.recipes.InstantFluidBarrelRecipe;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;

public class EmiInstantFluidBarrelRecipe extends AutoLayoutRecipe<InstantFluidBarrelRecipe>
{
    public EmiInstantFluidBarrelRecipe(ResourceLocation id, InstantFluidBarrelRecipe recipe)
    {
        super(EmiIntegration.BARREL, id, recipe);
        init(recipe);
    }

    @Override
    protected void processRecipe(InstantFluidBarrelRecipe recipe)
    {
        SizedFluidIngredient fluidA = recipe.getInputFluid();
        SizedFluidIngredient fluidB = recipe.getAddedFluid();
        FluidStack fluidOut = recipe.getOutputFluid();
        ItemStack itemOut = recipe.getResultItem();

        inputs.add(EmiHelpers.toIngredient(fluidA));
        inputs.add(EmiHelpers.toIngredient(fluidB));
        if (!itemOut.isEmpty())
        {
            outputs.add(EmiStack.of(itemOut));
        }
        if (!fluidOut.isEmpty())
        {
            outputs.add(EmiStack.of(fluidOut.getFluid(), fluidOut.getAmount()));
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        super.addWidgets(widgets);
        widgets.addText(Component.translatable("tfc.tooltip.barrel.instant"), getDisplayWidth() / 2, getDisplayHeight() - 2, 0xffffffff, true)
            .verticalAlign(TextWidget.Alignment.END)
            .horizontalAlign(TextWidget.Alignment.CENTER);
    }

    @Override
    protected int getPaddingBottom()
    {
        return 10;
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiSealedBarrelRecipe)
        {
            return -1;
        }
        return super.compareTo(other);
    }
}
