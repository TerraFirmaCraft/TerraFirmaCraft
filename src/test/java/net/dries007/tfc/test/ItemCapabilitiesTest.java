package net.dries007.tfc.test;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

import static net.dries007.tfc.test.TestAssertions.*;

public class ItemCapabilitiesTest implements TestSetup
{
    @Test
    public void testWoodenBucket()
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

    @Test
    public void testIngotMold()
    {
        final ItemStack stack = new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT));
        final IMold mold = IMold.get(stack);
        final IFluidHandlerItem fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        final Fluid copper = TFCFluids.METALS.get(Metal.COPPER).getSource();
        final FluidHeat copperHeat = FluidHeat.get(copper);

        assertNotNull(mold);
        assertNotNull(fluidHandler);
        assertNotNull(copperHeat);

        assertEquals(mold.getTemperature(), 0f);
        assertEquals(FluidStack.EMPTY, fluidHandler.getFluidInTank(0));

        mold.fill(new FluidStack(copper, 100), IFluidHandler.FluidAction.EXECUTE);
        mold.addTemperatureFromSourceWithHeatCapacity(1700f, copperHeat.heatCapacity(100));

        assertEquals(new FluidStack(copper, 100), mold.getFluidInTank(0));
        assertEquals(500f, mold.getTemperature());
        assertFalse(mold.isMolten());
    }
}
