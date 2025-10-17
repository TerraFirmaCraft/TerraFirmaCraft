/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.handlers;

import java.util.List;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.compat.emi.recipe.EmiAnvilRecipe;
import net.dries007.tfc.network.SelectAnvilPlanPacket;

public class EmiAnvilHandler implements StandardRecipeHandler<AnvilContainer>
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
            handler.getSlot(AnvilBlockEntity.SLOT_INPUT_MAIN)
        );
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe instanceof EmiAnvilRecipe;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<AnvilContainer> context)
    {
        if (StandardRecipeHandler.super.craft(recipe, context))
        {
            ResourceLocation id = recipe.getId();
            if (id != null)
            {
                PacketDistributor.sendToServer(new SelectAnvilPlanPacket(id));
            }
            return true;
        }
        return false;
    }
}
