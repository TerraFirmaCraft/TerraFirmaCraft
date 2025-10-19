/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.handlers;

import java.util.ArrayList;
import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import net.dries007.tfc.client.screen.SewingTableScreen;
import net.dries007.tfc.common.container.SewingTableContainer;
import net.dries007.tfc.compat.emi.recipe.EmiSewingRecipe;

public class EmiSewingHandler implements StandardRecipeHandler<SewingTableContainer>
{
    @Override
    public List<Slot> getInputSources(SewingTableContainer handler)
    {
        List<Slot> slots = new ArrayList<>();
        int offset = SewingTableContainer.SLOT_RESULT + 1;
        for (int i = offset; i < 36 + offset; i++)
        {
            slots.add(handler.getSlot(i));
        }
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(SewingTableContainer handler)
    {
        //TODO handle recipes that do not require one or more of these slots
        return List.of(
            handler.getSlot(SewingTableContainer.SLOT_INPUT_1),
            handler.getSlot(SewingTableContainer.SLOT_INPUT_2),
            handler.getSlot(SewingTableContainer.SLOT_YARN)
        );
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe instanceof EmiSewingRecipe;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<SewingTableContainer> context)
    {
        if (StandardRecipeHandler.super.craft(recipe, context))
        {
            AbstractContainerScreen<?> screen = context.getScreen();
            if (recipe instanceof EmiSewingRecipe sewingRecipe && screen instanceof SewingTableScreen sewingScreen)
            {
                sewingScreen.setRecipe(sewingRecipe.getRecipe());
            }
            return true;
        }
        return false;
    }
}
