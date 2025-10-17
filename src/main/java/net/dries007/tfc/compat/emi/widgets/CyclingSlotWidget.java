/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.widgets;

import java.util.List;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;

public class CyclingSlotWidget extends GeneratedSlotWidget
{
    public CyclingSlotWidget(EmiIngredient ingredient, int unique, int x, int y)
    {
        super((r) -> {
            List<EmiStack> stacks = ingredient.getEmiStacks();
            return stacks.get(r.nextInt(stacks.size()));
        }, unique, x, y);
    }

    public CyclingSlotWidget(List<? extends EmiIngredient> ingredients, int unique, int x, int y)
    {
        super((r) -> ingredients.get(r.nextInt(ingredients.size())), unique, x, y);
    }
}
