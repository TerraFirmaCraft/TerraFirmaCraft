/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi.handlers;

import java.util.ArrayList;
import java.util.List;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.container.BarrelContainer;
import net.dries007.tfc.compat.emi.recipe.EmiSealedBarrelRecipe;

//TODO make this actually move items, will require a custom fluid handler transfer packet
public class EmiBarrelHandler implements StandardRecipeHandler<BarrelContainer>
{
    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe instanceof EmiSealedBarrelRecipe;
    }

    @Override
    public List<Slot> getInputSources(BarrelContainer handler)
    {
        List<Slot> slots = new ArrayList<>();
        int offset = BarrelBlockEntity.SLOTS;
        for (int i = offset; i < 36 + offset; i++)
        {
            slots.add(handler.getSlot(i));
        }
        return slots;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<BarrelContainer> screen)
    {
        BarrelContainer barrel = screen.getMenu();
        FluidStack fluid = barrel.getBlockEntity().getInventory().getFluidInTank(0);
        List<EmiStack> stacks = new ArrayList<>(getInputSources(barrel).stream().map(Slot::getItem).map(EmiStack::of).toList());
        stacks.add(EmiStack.of(fluid.getFluid(), fluid.getAmount()));
        return new EmiPlayerInventory(stacks);
    }

    @Override
    public List<Slot> getCraftingSlots(BarrelContainer handler)
    {
        return List.of(
            handler.getSlot(BarrelBlockEntity.SLOT_FLUID_CONTAINER_IN),
            handler.getSlot(BarrelBlockEntity.SLOT_ITEM)
        );
    }
}
