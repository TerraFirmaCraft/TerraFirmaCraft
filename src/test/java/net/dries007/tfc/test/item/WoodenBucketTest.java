/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.items.TFCItems;

import static net.dries007.tfc.test.TestAssertions.*;

public class WoodenBucketTest
{
    @Test
    public void testFillAndDrain()
    {
        final ItemStack bucket = new ItemStack(TFCItems.WOODEN_BUCKET);
        final ItemStack before = bucket.copy();
        final IFluidHandlerItem handler = bucket.getCapability(Capabilities.FluidHandler.ITEM);

        assertNotNull(handler);
        assertEquals(before, handler.getContainer());
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank(0));
        assertEquals(FluidComponent.EMPTY, bucket.get(TFCComponents.FLUID));

        // Simulate fill(300 water)
        assertEquals(300, handler.fill(new FluidStack(Fluids.WATER, 300), IFluidHandler.FluidAction.SIMULATE));

        assertEquals(before, bucket);
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank(0));

        // Execute fill(300 water) -> total 300 water
        assertEquals(300, handler.fill(new FluidStack(Fluids.WATER, 300), IFluidHandler.FluidAction.EXECUTE));

        assertNotEquals(before, bucket);
        assertEquals(new FluidStack(Fluids.WATER, 300), handler.getFluidInTank(0));
        assertEquals(FluidComponent.of(Fluids.WATER, 300), bucket.get(TFCComponents.FLUID));

        // Simulate drain(100 water)
        assertEquals(new FluidStack(Fluids.WATER, 100), handler.drain(100, IFluidHandler.FluidAction.SIMULATE));

        assertNotEquals(before, bucket);
        assertEquals(new FluidStack(Fluids.WATER, 300), handler.getFluidInTank(0));
        assertEquals(FluidComponent.of(Fluids.WATER, 300), bucket.get(TFCComponents.FLUID));

        // Execute drain(100 water)
        assertEquals(new FluidStack(Fluids.WATER, 100), handler.drain(100, IFluidHandler.FluidAction.EXECUTE));

        assertNotEquals(before, bucket);
        assertEquals(new FluidStack(Fluids.WATER, 200), handler.getFluidInTank(0));
        assertEquals(FluidComponent.of(Fluids.WATER, 200), bucket.get(TFCComponents.FLUID));

        // Execute drain(500 water)
        assertEquals(new FluidStack(Fluids.WATER, 200), handler.drain(500, IFluidHandler.FluidAction.EXECUTE));

        assertEquals(before, bucket);
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank(0));
        assertEquals(FluidComponent.EMPTY, bucket.get(TFCComponents.FLUID));
    }
}
