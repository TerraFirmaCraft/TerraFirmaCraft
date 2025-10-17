/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.handlers;

import java.util.ArrayList;
import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.Slot;

import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.container.FirepitContainer;
import net.dries007.tfc.compat.emi.recipe.EmiHeatingRecipe;

public class EmiFirepitHandler implements StandardRecipeHandler<FirepitContainer>
{
    @Override
    public List<Slot> getInputSources(FirepitContainer handler)
    {
        List<Slot> slots = new ArrayList<>();
        int offset = FirepitBlockEntity.SLOT_OUTPUT_2 + 1;
        for (int i = offset; i < 36 + offset; i++)
        {
            slots.add(handler.getSlot(i));
        }
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(FirepitContainer handler)
    {
        return List.of(handler.getSlot(FirepitBlockEntity.SLOT_ITEM_INPUT));
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe instanceof EmiHeatingRecipe heatingRecipe && heatingRecipe.hasSolidOutput();
    }
}
