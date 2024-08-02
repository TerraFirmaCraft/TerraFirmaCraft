/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.block;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.component.ComponentView;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.fluid.FluidContainer;
import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
import net.dries007.tfc.common.component.item.ItemComponent;
import net.dries007.tfc.common.component.item.ItemContainer;
import net.dries007.tfc.common.component.item.ItemContainerInfo;

/**
 * A simple merged fluid  handler for barrels, that respects the barrels {@link BarrelComponent#hasActiveRecipe()} - no modifications are
 * possible while that is present.
 */
public class Barrel extends ComponentView<BarrelComponent> implements FluidContainer, IFluidHandlerItem
{
    public Barrel(ItemStack stack)
    {
        super(stack, TFCComponents.BARREL, BarrelComponent.EMPTY);
    }

    @Override
    public ItemStack getContainer()
    {
        return stack;
    }

    @Override
    public FluidContainerInfo containerInfo()
    {
        return BarrelBlockEntity.BarrelInventory.INFO;
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return component.fluidContent();
    }

    @Override
    public int fill(FluidStack input, FluidAction action)
    {
        if (component.hasActiveRecipe()) return 0; // No interaction with an active recipe
        final var result = FluidComponent.fill(component.fluidContent(), input, containerInfo());
        if (action.execute()) apply(component.with(result.content()));
        return result.filled();
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (component.hasActiveRecipe()) return FluidStack.EMPTY;
        final var result = FluidComponent.drain(component.fluidContent(), maxDrain);
        if (action.execute()) apply(component.with(result.content()));
        return result.drained();
    }
}
