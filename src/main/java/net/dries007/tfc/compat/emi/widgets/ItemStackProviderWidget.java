package net.dries007.tfc.compat.emi.widgets;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

public class ItemStackProviderWidget extends GeneratedSlotWidget
{
    public ItemStackProviderWidget(SlotWidget input, ItemStackProvider provider, int unique, int x, int y)
    {
        super(r -> {
            List<EmiStack> stacks = input.getStack().getEmiStacks();
            EmiStack stack = stacks.get(r.nextInt(stacks.size()));
            return EmiStack.of(provider.getSingleStack(stack.getItemStack()));
        }, unique, x, y);
    }

    public ItemStackProviderWidget(ItemStackProvider provider, int unique, int x, int y)
    {
        super(r -> EmiStack.of(provider.getEmptyStack()), unique, x, y);
    }
}
