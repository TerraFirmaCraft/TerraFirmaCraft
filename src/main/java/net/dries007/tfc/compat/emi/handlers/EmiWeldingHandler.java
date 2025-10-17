/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.handlers;

import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.Slot;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.compat.emi.recipe.EmiWeldingRecipe;

public class EmiWeldingHandler implements StandardRecipeHandler<AnvilContainer>
{
    @Override
    public List<Slot> getInputSources(AnvilContainer handler)
    {
        return List.copyOf(handler.slots);
    }

    @Override
    public List<Slot> getCraftingSlots(AnvilContainer handler)
    {
        return List.of(
            handler.getSlot(AnvilBlockEntity.SLOT_INPUT_MAIN),
            handler.getSlot(AnvilBlockEntity.SLOT_INPUT_SECOND),
            handler.getSlot(AnvilBlockEntity.SLOT_CATALYST)
        );
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe instanceof EmiWeldingRecipe;
    }
}
