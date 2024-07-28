package net.dries007.tfc.test.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.capabilities.TFCCapabilities;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.fluid.FluidComponent;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.test.TestSetup;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

import static net.dries007.tfc.test.TestAssertions.*;

public class MoldTest implements TestSetup
{
    @Test
    public void testMoldProvidesHeatFluidAndMoldCapabilities()
    {
        final ItemStack stack = new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT));

        assertNotNull(stack.getCapability(Capabilities.FluidHandler.ITEM));
        assertNotNull(stack.getCapability(TFCCapabilities.HEAT));
        assertNotNull(stack.getCapability(TFCCapabilities.MOLD));
    }

    @Test
    public void testFillAndDrainWhileCold()
    {
        final ItemStack stack = new ItemStack(TFCItems.MOLDS.get(Metal.ItemType.INGOT));
        final IMold mold = IMold.get(stack);
        final FluidStack copper100 = new FluidStack(TFCFluids.METALS.get(Metal.COPPER).getSource(), 100);

        assertNotNull(mold);
        assertEquals(100, mold.fill(copper100, IFluidHandler.FluidAction.SIMULATE)); // Simulate
        assertEquals(FluidStack.EMPTY, mold.getFluidInTank(0));

        assertEquals(100, mold.fill(copper100, IFluidHandler.FluidAction.EXECUTE)); // Execute
        assertEquals(copper100, mold.getFluidInTank(0)); // Should contain fluid
        assertNotSame(copper100, mold.getFluidInTank(0)); // But not be the same reference

        assertFalse(mold.isMolten());
        assertEquals(mold.getTemperature(), 0f);

        assertEquals(FluidStack.EMPTY, mold.drain(100, IFluidHandler.FluidAction.SIMULATE)); // Cannot drain from cold mold
        assertEquals(FluidStack.EMPTY, mold.drain(100, IFluidHandler.FluidAction.EXECUTE));
        assertEquals(copper100, mold.getFluidInTank(0));

        assertEquals(copper100, mold.drainIgnoringTemperature(100, IFluidHandler.FluidAction.SIMULATE)); // Unless ignoring temperature
        assertEquals(copper100, mold.drainIgnoringTemperature(100, IFluidHandler.FluidAction.EXECUTE));
        assertEquals(FluidStack.EMPTY, mold.getFluidInTank(0));
    }
}
