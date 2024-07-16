/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.common.capabilities.ItemStackFluidHandler;

import static org.junit.jupiter.api.Assertions.*;

public class ItemStackFluidHandlerTests extends TestHelper
{
    @Test
    public void testFill()
    {
        final ItemStackFluidHandler handler = create();

        assertEquals(123, handler.fill(new FluidStack(Fluids.WATER, 123), IFluidHandler.FluidAction.SIMULATE));
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank());
        assertEquals(321, handler.fill(new FluidStack(Fluids.WATER, 321), IFluidHandler.FluidAction.EXECUTE));
        assertEquals(new FluidStack(Fluids.WATER, 321), handler.getFluidInTank());
    }

    @Test
    public void testFillWithEmpty()
    {
        final ItemStackFluidHandler handler = create();

        assertEquals(0, handler.fill(FluidStack.EMPTY, IFluidHandler.FluidAction.EXECUTE));
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank());
    }

    @Test
    public void testFillWithNotAllowedFluid()
    {
        final ItemStackFluidHandler handler = create();

        assertEquals(0, handler.fill(new FluidStack(Fluids.LAVA, 100), IFluidHandler.FluidAction.EXECUTE));
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank());
    }

    @Test
    public void testFillMoreThanCapacity()
    {
        final ItemStackFluidHandler handler = create();

        assertEquals(1000, handler.fill(new FluidStack(Fluids.WATER, 1234), IFluidHandler.FluidAction.EXECUTE));
        assertEquals(new FluidStack(Fluids.WATER, 1000), handler.getFluidInTank());
    }

    @Test
    public void testDrain()
    {
        final ItemStackFluidHandler handler = createFilled();

        assertEquals(new FluidStack(Fluids.WATER, 123), handler.drain(123, IFluidHandler.FluidAction.SIMULATE));
        assertEquals(new FluidStack(Fluids.WATER, 1000), handler.getFluidInTank());
        assertEquals(new FluidStack(Fluids.WATER, 321), handler.drain(321, IFluidHandler.FluidAction.EXECUTE));
        assertEquals(new FluidStack(Fluids.WATER, 679), handler.getFluidInTank());
    }

    @Test
    public void testDrainMoreThanAvailable()
    {
        final ItemStackFluidHandler handler = createFilled();

        assertEquals(new FluidStack(Fluids.WATER, 1000), handler.drain(1234, IFluidHandler.FluidAction.EXECUTE));
        assertEquals(FluidStack.EMPTY, handler.getFluidInTank());
    }

    @Test
    public void testDrainEmpty()
    {
        final ItemStackFluidHandler handler = createFilled();

        assertEquals(FluidStack.EMPTY, handler.drain(new FluidStack(Fluids.EMPTY, 1000), IFluidHandler.FluidAction.EXECUTE));
        assertEquals(new FluidStack(Fluids.WATER, 1000), handler.getFluidInTank());
    }

    @Test
    public void testDrainIncorrectContent()
    {
        final ItemStackFluidHandler handler = createFilled();

        assertEquals(FluidStack.EMPTY, handler.drain(new FluidStack(Fluids.LAVA, 123), IFluidHandler.FluidAction.EXECUTE));
        assertEquals(new FluidStack(Fluids.WATER, 1000), handler.getFluidInTank());
    }

    @Test
    public void testSaveAndLoad()
    {
        final ItemStackFluidHandler handler0 = createFilled();
        final ItemStackFluidHandler handler1 = new ItemStackFluidHandler(handler0.getContainer().copy(), fluid -> fluid == Fluids.WATER, () -> 1000)
        {{
            load();
        }};

        assertEquals(handler0.getFluidInTank(), handler1.getFluidInTank());
    }

    private ItemStackFluidHandler createFilled()
    {
        final ItemStackFluidHandler handler = create();
        handler.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE);
        return handler;
    }

    private ItemStackFluidHandler create()
    {
        return new ItemStackFluidHandler(new ItemStack(Items.APPLE), fluid -> fluid == Fluids.WATER, () -> 1000);
    }
}
