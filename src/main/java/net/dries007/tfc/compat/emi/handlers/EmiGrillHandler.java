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

import net.dries007.tfc.common.blockentities.GrillBlockEntity;
import net.dries007.tfc.common.container.GrillContainer;
import net.dries007.tfc.compat.emi.recipe.EmiHeatingRecipe;

public class EmiGrillHandler implements StandardRecipeHandler<GrillContainer>
{
    @Override
    public List<Slot> getInputSources(GrillContainer handler)
    {
        List<Slot> slots = new ArrayList<>();
        int offset = GrillBlockEntity.SLOT_EXTRA_INPUT_END + 1;
        for (int i = offset; i < 36 + offset; i++)
        {
            slots.add(handler.getSlot(i));
        }
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(GrillContainer handler)
    {
        List<Slot> slots = new ArrayList<>();

        // By providing only empty slots EMI will try to use all available input slots instead of just replacing the first input slot
        for (int i = GrillBlockEntity.SLOT_EXTRA_INPUT_START; i <= GrillBlockEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            Slot slot = handler.getSlot(i);
            if (!slot.hasItem())
            {
                slots.add(slot);
            }
        }
        return slots;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe instanceof EmiHeatingRecipe heatingRecipe && heatingRecipe.hasSolidOutput();
    }
}
