/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.item;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.fluid.FluidContainerInfo;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.test.TestSetup;

import static net.dries007.tfc.test.TestAssertions.*;


public class FluidComponentTest implements TestSetup
{
    final FluidContainerInfo info = new FluidContainerInfo() {
        @Override
        public boolean canContainFluid(Fluid input)
        {
            return input == Fluids.WATER || input == TFCFluids.SALT_WATER.getSource();
        }

        @Override
        public int fluidCapacity()
        {
            return 1000;
        }
    };

    @Test
    public void testFillFromEmpty()
    {
        fill(
            FluidComponent.EMPTY,
            Fluids.WATER, 100,
            Fluids.WATER, 100,
            100
        );
    }

    @Test
    public void testFillFromEmptyTooMuch()
    {
        fill(
            FluidComponent.EMPTY,
            Fluids.WATER, 1500,
            Fluids.WATER, 1000,
            1000
        );
    }

    @Test
    public void testFillFromPartial()
    {
        fill(
            FluidComponent.of(Fluids.WATER, 300),
            Fluids.WATER, 100,
            Fluids.WATER, 400,
            100
        );
    }

    @Test
    public void testFillFromPartialTooMuch()
    {
        fill(
            FluidComponent.of(Fluids.WATER, 300),
            Fluids.WATER, 1000,
            Fluids.WATER, 1000,
            700
        );
    }

    @Test
    public void testFillFromFull()
    {
        fill(
            FluidComponent.of(Fluids.WATER, 1000),
            Fluids.WATER, 400,
            Fluids.WATER, 1000,
            0
        );
    }

    @Test
    public void testFillWithInvalidFluidFromEmpty()
    {
        fill(
            FluidComponent.EMPTY,
            Fluids.LAVA, 300,
            Fluids.EMPTY, 0,
            0
        );
    }

    @Test
    public void testFillWithInvalidFluidFromPartial()
    {
        fill(
            FluidComponent.of(Fluids.WATER, 300),
            Fluids.LAVA, 1000,
            Fluids.WATER, 300,
            0
        );
    }

    @Test
    public void testFillWithNotMatchingFluidFromPartial()
    {
        fill(
            FluidComponent.of(Fluids.WATER, 300),
            TFCFluids.SALT_WATER.getSource(), 400,
            Fluids.WATER, 300,
            0
        );
    }

    @Test
    public void testDrainFromEmpty()
    {
        drain(
            FluidComponent.EMPTY,
            300,
            Fluids.EMPTY, 0,
            Fluids.EMPTY, 0
        );
    }

    @Test
    public void testDrainSomeFromPartial()
    {
        drain(
            FluidComponent.of(Fluids.WATER, 300),
            100,
            Fluids.WATER, 200,
            Fluids.WATER, 100
        );
    }

    @Test
    public void testDrainExactFromPartial()
    {
        drain(
            FluidComponent.of(Fluids.WATER, 300),
            300,
            Fluids.EMPTY, 0,
            Fluids.WATER, 300
        );
    }

    @Test
    public void testDrainMoreFromPartial()
    {
        drain(
            FluidComponent.of(Fluids.WATER, 300),
            600,
            Fluids.EMPTY, 0,
            Fluids.WATER, 300
        );
    }

    @Test
    public void testDrainSomeFromFull()
    {
        drain(
            FluidComponent.of(Fluids.WATER, 1000),
            600,
            Fluids.WATER, 400,
            Fluids.WATER, 600
        );
    }

    @Test
    public void testDrainMoreFromFull()
    {
        drain(
            FluidComponent.of(Fluids.WATER, 1000),
            1600,
            Fluids.EMPTY, 0,
            Fluids.WATER, 1000
        );
    }

    private void fill(FluidComponent before, Fluid fill, int fillAmount, Fluid after, int afterAmount, int filled)
    {
        final var result = FluidComponent.fill(before.content(), new FluidStack(fill, fillAmount), info);
        assertEquals(new FluidStack(after, afterAmount), result.content());
        assertEquals(filled, result.filled());
    }

    private void drain(FluidComponent before, int drainAmount, Fluid after, int afterAmount, Fluid drain, int drainedAmount)
    {
        final var result = FluidComponent.drain(before.content(), drainAmount);
        assertEquals(new FluidStack(after, afterAmount), result.content());
        assertEquals(new FluidStack(drain, drainedAmount), result.drained());
    }
}
