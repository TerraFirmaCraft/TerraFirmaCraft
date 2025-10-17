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

import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.container.CharcoalForgeContainer;
import net.dries007.tfc.compat.emi.recipe.EmiHeatingRecipe;

public class EmiForgeHandler implements StandardRecipeHandler<CharcoalForgeContainer>
{
    @Override
    public List<Slot> getInputSources(CharcoalForgeContainer handler)
    {
        List<Slot> slots = new ArrayList<>();
        int offset = CharcoalForgeBlockEntity.SLOT_EXTRA_MAX + 1;
        for (int i = offset; i < 36 + offset; i++)
        {
            slots.add(handler.getSlot(i));
        }
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(CharcoalForgeContainer handler)
    {
        List<Slot> slots = new ArrayList<>();

        // By providing only empty slots EMI will try to use all available input slots instead of just replacing the first input slot
        for (int i = CharcoalForgeBlockEntity.SLOT_INPUT_MIN; i <= CharcoalForgeBlockEntity.SLOT_INPUT_MAX; i++)
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
        return recipe instanceof EmiHeatingRecipe;
    }
}
