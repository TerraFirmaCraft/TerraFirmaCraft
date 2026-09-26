/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.widgets;

import java.util.List;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.compat.emi.EmiHelpers;

public class ItemStackProviderWidget extends GeneratedSlotWidget
{
    public ItemStackProviderWidget(SlotWidget input, ItemStackProvider provider, int unique, int x, int y)
    {
        super(r -> {
            //TODO needs to use RecipeHelpers#setCraftingInput??
            List<EmiStack> stacks = input.getStack().getEmiStacks();
            EmiStack stack = stacks.get(r.nextInt(stacks.size()));
            List<ItemStack> displayInputs = provider.displayInputs(stack.getItemStack());
            if (displayInputs.isEmpty())
            {
                return EmiStack.of(ItemStack.EMPTY);
            }
            return EmiHelpers.nonDecayStack(provider.getSingleStackDisplayOnly(displayInputs.get(r.nextInt(displayInputs.size()))));
        }, unique, x, y);
    }

    public ItemStackProviderWidget(ItemStackProvider provider, int unique, int x, int y)
    {
        super(r -> EmiStack.of(provider.getEmptyStack()), unique, x, y);
    }
}
