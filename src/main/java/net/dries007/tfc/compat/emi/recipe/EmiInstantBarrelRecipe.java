/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.InstantBarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;

public class EmiInstantBarrelRecipe extends AutoLayoutRecipe<InstantBarrelRecipe>
{
    private static final int SEED_UNIQUE = 134217728;
    private final boolean isStatic;
    private final ItemStackProvider outputProvider;
    private @Nullable SlotWidget itemInputSlot;

    public EmiInstantBarrelRecipe(ResourceLocation id, InstantBarrelRecipe recipe)
    {
        super(EmiIntegration.BARREL, id, recipe);
        outputProvider = recipe.getOutputItem();
        isStatic = !outputProvider.dependsOnInput();
        init(recipe);
    }

    @Override
    protected void processRecipe(InstantBarrelRecipe recipe)
    {
        ItemStack outputStack = outputProvider.getEmptyStack();
        SizedIngredient inputItem = recipe.getInputItem();
        FluidStack fluidOut = recipe.getOutputFluid();
        inputs.add(isStatic ? EmiIngredient.of(inputItem.ingredient()) : new EmiSizedIngredient(inputItem));
        inputs.add(EmiHelpers.toIngredient(recipe.getInputFluid()));
        if (!outputStack.isEmpty() || !isStatic)
        {
            outputs.add(EmiHelpers.nonDecayStack(outputStack));
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
    protected SlotWidget generateInputSlot(EmiIngredient ingredient, int x, int y, int index)
    {
        if (index == 0 && !isStatic)
        {
            itemInputSlot = new CyclingSlotWidget(ingredient, SEED_UNIQUE, x, y);
            return itemInputSlot;
        }
        return super.generateInputSlot(ingredient, x, y, index);
    }

    @Override
    protected SlotWidget generateOutputSlot(EmiStack stack, int x, int y, int index)
    {
        if (!isStatic && index == 0 && itemInputSlot != null)
        {
            return new ItemStackProviderWidget(itemInputSlot, outputProvider, SEED_UNIQUE, x, y);
        }
        return super.generateOutputSlot(stack, x, y, index);
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

    @Override
    public boolean supportsRecipeTree()
    {
        return isStatic;
    }
}
