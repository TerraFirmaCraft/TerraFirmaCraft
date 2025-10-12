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
}
