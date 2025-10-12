package net.dries007.tfc.compat.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.InstantBarrelRecipe;
import net.dries007.tfc.common.recipes.InstantFluidBarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;

public class EmiInstantFluidBarrelRecipe extends AutoLayoutRecipe<InstantFluidBarrelRecipe>
{
    public EmiInstantFluidBarrelRecipe(ResourceLocation id, InstantFluidBarrelRecipe recipe)
    {
        super(EmiIntegration.BARREL, id, recipe);
    }

    @Override
    protected void processRecipe()
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
        widgets.addText(Component.translatable("tfc.tooltip.barrel_instant"), getDisplayWidth() / 2, getDisplayHeight() - 2, 0xffffffff, true).verticalAlign(TextWidget.Alignment.END).horizontalAlign(TextWidget.Alignment.CENTER);
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
