/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.recipe;

import java.util.List;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextWidget;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.compat.emi.EmiIntegration;
import net.dries007.tfc.util.AlloyRange;
import net.dries007.tfc.util.FluidAlloy;

public class EmiAlloyingRecipe extends AutoLayoutRecipe<AlloyRecipe>
{
    private static final int COLUMN_SPACING = 70;
    private static final int Y_SPACING = 2;

    public EmiAlloyingRecipe(ResourceLocation id, AlloyRecipe recipe)
    {
        super(EmiIntegration.ALLOYING, id, recipe);
        init(recipe);
    }

    @Override
    protected void processRecipe(AlloyRecipe recipe)
    {
        List<AlloyRange> ranges = recipe.contents();

        //TODO this will be inaccurate for recipes that require high precision inputs

        // Generate an example alloying input, so that the recipe tree works
        double min = 0;
        for (AlloyRange range : ranges)
        {
            min += range.min();
        }
        // Distribute the remaining first come, first served
        double remaining = 1 - min;
        for (AlloyRange range : ranges)
        {
            double consumed = Math.min(range.max() - range.min(), remaining);
            double amount = range.min() + consumed;
            remaining -= consumed;
            inputs.add(EmiStack.of(range.fluid(), Math.round(amount * 100)));
        }

        outputs.add(EmiStack.of(recipe.result(), inputs.stream().mapToLong(EmiIngredient::getAmount).sum()));
    }

    @Override
    protected List<Widget> generateWidgets(AlloyRecipe recipe)
    {
        int firstColumn = getMargin() + getPaddingLeft();
        int secondColumn = firstColumn + COLUMN_SPACING;
        int startY = getMargin() + getPaddingTop();
        WidgetLayout widgets = new WidgetLayout();

        List<AlloyRange> ranges = recipe.contents();
        for (int i = 0; i < inputs.size(); i++)
        {
            AlloyRange range = ranges.get(i);
            if (i == 0)
            {
                widgets.add(new SlotWidget(inputs.get(i), firstColumn, startY));
            }
            else if (i % 2 == 0)
            {
                widgets.add(new SlotWidget(inputs.get(i), firstColumn, widgets.last(Position.BOTTOM, 6)));
            }
            else
            {
                widgets.add(new SlotWidget(inputs.get(i), secondColumn, widgets.index(widgets.size() - Y_SPACING, Position.TOP)));
            }
            int x = widgets.last(Position.RIGHT, 4);
            int y = widgets.last(Position.Y, widgets.last(Position.HEIGHT) / 2);
            widgets.add(new TextWidget(formatRange(range), x, y, 0xff000000, false).verticalAlign(TextWidget.Alignment.CENTER));
        }
        widgets.add(new SlotWidget(outputs.getFirst(), secondColumn + COLUMN_SPACING, widgets.max(Position.BOTTOM) / 2 - (9 - Y_SPACING)).recipeContext(this));
        return widgets;
    }

    protected FormattedCharSequence formatRange(AlloyRange range)
    {
        // Min and max are (roughly) equal, so just so one number to display
        if (Math.abs(range.max() - range.min()) < FluidAlloy.EPSILON)
        {
            return Component.literal(String.format("%.0f%%", range.max() * 100)).getVisualOrderText();
        }
        return Component.literal(String.format("%.0f-%.0f%%", range.min() * 100, range.max() * 100)).getVisualOrderText();
    }
}
