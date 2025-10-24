/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.FillingArrowWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.compat.emi.stack.EmiSizedIngredient;
import net.dries007.tfc.compat.emi.widgets.CyclingSlotWidget;
import net.dries007.tfc.compat.emi.widgets.ItemStackProviderWidget;
import net.dries007.tfc.util.calendar.Calendars;

public class EmiSealedBarrelRecipe extends AutoLayoutRecipe<SealedBarrelRecipe>
{
    private static final int SEED_UNIQUE = 16777216;
    private final boolean infinite;
    private final int duration;
    private @Nullable ItemStackProvider outputProvider;
    private @Nullable ItemStackProvider onSeal;
    private @Nullable ItemStackProvider onUnseal;

    public EmiSealedBarrelRecipe(ResourceLocation id, SealedBarrelRecipe recipe)
    {
        super(EmiIntegration.BARREL, id, recipe);
        infinite = recipe.isInfinite();
        duration = recipe.getDuration();
        init(recipe);
    }

    @Override
    protected void processRecipe(SealedBarrelRecipe recipe)
    {
        onSeal = recipe.onSeal();
        onUnseal = recipe.onUnseal();

        SizedIngredient input = recipe.getInputItem();
        ItemStackProvider output = recipe.getOutputItem();
        boolean isStatic = !output.dependsOnInput() && onSeal == null && onUnseal == null;

        inputs.add(isStatic ? EmiIngredient.of(input.ingredient(), input.count()) : new EmiSizedIngredient(input));
        inputs.add(EmiHelpers.toIngredient(recipe.getInputFluid()));

        if (output.dependsOnInput())
        {
            outputProvider = output;
        }
        else
        {
            ItemStack stack = output.stack();
            if (!stack.isEmpty())
            {
                outputs.add(EmiHelpers.nonDecayStack(stack));
            }
        }

        FluidStack fluid = recipe.getOutputFluid();
        if (!fluid.isEmpty())
        {
            outputs.add(EmiStack.of(fluid.getFluid(), fluid.getAmount()));
        }
    }

    @Override
    protected List<Widget> generateWidgets(SealedBarrelRecipe recipe)
    {
        int y = getMargin() + getPaddingTop();
        WidgetLayout widgets = new WidgetLayout(new Bounds(getMargin() + getPaddingLeft(), getMargin() + getPaddingTop(), 0, 0));

        // Slot 0 (item input)
        SlotWidget itemInputSlot = makeItemInputSlot(widgets.last(Position.RIGHT), y);
        if (itemInputSlot != null)
        {
            widgets.add(itemInputSlot);
        }
        // Slot 1 (fluid input)
        widgets.add(new SlotWidget(inputs.getLast(), widgets.last(Position.RIGHT, 3), y));

        widgets.add(new FillingArrowWidget(widgets.last(Position.RIGHT, 3), y, 3000));

        SlotWidget sealSlot = null;
        if (itemInputSlot != null)
        {
            if (onSeal != null)
            {
                sealSlot = widgets.addWidget(new ItemStackProviderWidget(
                    itemInputSlot,
                    onSeal,
                    SEED_UNIQUE,
                    widgets.last(Position.RIGHT, 3),
                    y
                ).recipeContext(this).appendTooltip(Component.translatable("tfc.tooltip.while_sealed_description")));
                widgets.add(new FillingArrowWidget(widgets.last(Position.RIGHT, 3), y, 3000));
            }
            //TODO ok what is the actual order of this
            if (outputProvider != null)
            {
                widgets.add(new ItemStackProviderWidget(
                    itemInputSlot,
                    outputProvider,
                    SEED_UNIQUE,
                    widgets.last(Position.RIGHT, 3),
                    y
                ).recipeContext(this));
            }
            if (onUnseal != null)
            {
                widgets.add(new ItemStackProviderWidget(
                    sealSlot != null ? sealSlot : itemInputSlot,
                    onUnseal,
                    SEED_UNIQUE,
                    widgets.last(Position.RIGHT, 3),
                    y
                ).recipeContext(this));
            }
        }

        for (EmiStack out : outputs)
        {
            widgets.add(new SlotWidget(out, widgets.last(Position.RIGHT, 3), y).recipeContext(this));
        }

        return widgets;
    }

    @Override
    public void addWidgets(WidgetHolder widgets)
    {
        super.addWidgets(widgets);
        widgets.addText(getTimeText(), getDisplayWidth() / 2, getDisplayHeight() - 2, 0xffffffff, true)
            .verticalAlign(TextWidget.Alignment.END)
            .horizontalAlign(TextWidget.Alignment.CENTER);
    }

    private MutableComponent getTimeText()
    {
        if (infinite)
        {
            return Component.translatable("tfc.tooltip.barrel.infinite");
        }
        return Calendars.CLIENT.getTimeDelta(duration);
    }

    @Override
    protected int getPaddingBottom()
    {
        return 10;
    }

    @Override
    public boolean supportsRecipeTree()
    {
        return outputProvider == null && super.supportsRecipeTree();
    }

    private @Nullable SlotWidget makeItemInputSlot(int x, int y)
    {
        EmiIngredient ingredient = inputs.getFirst();
        if (ingredient.getEmiStacks().size() > 1 && (onSeal != null || outputProvider != null))
        {
            return new CyclingSlotWidget(ingredient, SEED_UNIQUE, x, y);
        }
        if (ingredient.isEmpty())
        {
            return null;
        }
        return new SlotWidget(ingredient, x, y);
    }

    @Override
    public int compareTo(EmiRecipe other)
    {
        if (other instanceof EmiSealedBarrelRecipe recipe)
        {
            if (infinite != recipe.infinite)
            {
                // Infinite recipes go last
                return infinite ? 1 : -1;
            }
            // Shorter duration recipes first
            int durationDiff = duration - recipe.duration;
            if (durationDiff != 0)
            {
                return durationDiff;
            }
            return super.compareTo(recipe);
        }
        // Go after all the instant recipe types
        return 1;
    }
}
