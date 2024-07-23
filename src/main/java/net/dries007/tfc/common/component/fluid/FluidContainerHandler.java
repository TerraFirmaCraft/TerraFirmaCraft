/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.fluid;

import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.component.ComponentView;
import net.dries007.tfc.common.component.TFCComponents;

/**
 * This is a capability implemented on pure fluid containers.
 */
public class FluidContainerHandler extends ComponentView<FluidComponent> implements FluidContainer
{
    private final FluidContainerInfo containerInfo;

    public FluidContainerHandler(ItemStack stack, FluidContainerInfo containerInfo)
    {
        super(stack, TFCComponents.FLUID, FluidComponent.EMPTY);
        this.containerInfo = containerInfo;
    }

    @Override
    public FluidContainerInfo fluidContainerInfo()
    {
        return containerInfo;
    }

    @Override
    public ItemStack getContainer()
    {
        return stack;
    }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return component.content();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        final var remainder = component.fill(resource, containerInfo);
        if (action.execute()) apply(new FluidComponent(remainder.content()));
        return remainder.filled();
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        final var remainder = component.drain(maxDrain);
        if (action.execute()) apply(new FluidComponent(remainder.content()));
        return remainder.drained();
    }
}
