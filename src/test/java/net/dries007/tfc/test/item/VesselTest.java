package net.dries007.tfc.test.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.capabilities.ItemCapabilities;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.component.mold.Vessel;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.test.TestSetup;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.test.TestAssertions.*;

public class VesselTest implements TestSetup
{
    @Test
    public void testInsertAndExtractItems()
    {
        final ItemStack stack = new ItemStack(TFCItems.VESSEL);
        final ItemStack stick5 = new ItemStack(Items.STICK, 5);
        final ItemStack stick11 = new ItemStack(Items.STICK, 11);
        final ItemStack stick16 = new ItemStack(Items.STICK, 16);
        final ItemStack stick32 = new ItemStack(Items.STICK, 32);
        final Vessel vessel = Vessel.get(stack);

        assertNotNull(vessel);

        assertEquals(ItemStack.EMPTY, vessel.insertItem(0, stick5, true)); // Simulate insert less than capacity
        assertEquals(ItemStack.EMPTY, vessel.getStackInSlot(0));

        assertEquals(ItemStack.EMPTY, vessel.insertItem(0, stick5, false)); // Actually insert less than capacity
        assertEquals(stick5, vessel.getStackInSlot(0)); // The stack in slot should equal
        assertNotSame(stick5, vessel.getStackInSlot(0)); // But they should not be the same instance (mutable guarding)

        assertEquals(stick5, vessel.insertItem(0, stick16, true)); // Insert over capacity (with content inside)
        assertEquals(stick5, vessel.insertItem(0, stick16, false));
        assertEquals(stick16, vessel.getStackInSlot(0));
        assertNotSame(stick16, vessel.getStackInSlot(0));

        assertEquals(stick5, vessel.extractItem(0, 5, true)); // Extract less than contained
        assertEquals(stick5, vessel.extractItem(0, 5, false));
        assertEquals(stick11, vessel.getStackInSlot(0));

        assertEquals(stick11, vessel.extractItem(0, 64, true)); // Extract more than contained
        assertEquals(stick11, vessel.extractItem(0, 64, false));
        assertEquals(ItemStack.EMPTY, vessel.getStackInSlot(0));

        assertEquals(stick16, vessel.insertItem(1, stick32, true)); // Insert over capacity (with empty inside)
        assertEquals(stick16, vessel.insertItem(1, stick32, false));
        assertEquals(stick16, vessel.getStackInSlot(1));

        assertEquals(stick16, vessel.extractItem(1, 16, true)); // Extract exactly equal to contained
        assertEquals(stick16, vessel.extractItem(1, 16, false)); // Extract exactly equal to contained
        assertEquals(ItemStack.EMPTY, vessel.getStackInSlot(1));
    }

    @Test
    public void testVesselProvidesItemFluidMoldAndHeatCapabilities()
    {
        final ItemStack stack = new ItemStack(TFCItems.VESSEL);

        assertNotNull(stack.getCapability(Capabilities.ItemHandler.ITEM));
        assertNotNull(stack.getCapability(Capabilities.FluidHandler.ITEM));
        assertNotNull(stack.getCapability(ItemCapabilities.MOLD));
        assertNotNull(stack.getCapability(ItemCapabilities.HEAT));
    }

    @Test
    public void testItemHandlerLocksWhenHot()
    {
        final ItemStack stack = new ItemStack(TFCItems.VESSEL);
        final ItemStack stick = new ItemStack(Items.STICK, 5);
        final Vessel vessel = Vessel.get(stack);

        assertNotNull(vessel);

        vessel.insertItem(0, stick, false);
        vessel.setTemperature(10f); // The vessel is hot

        assertEquals(stick, vessel.getStackInSlot(0)); // The content should still be there
        assertEquals(ItemStack.EMPTY, vessel.extractItem(0, 64, true)); // But extraction is not allowed
        assertEquals(stick, vessel.insertItem(1, stick, true)); // And neither is insertion

        assertEquals(ItemStack.EMPTY, vessel.extractItem(0, 64, false)); // Even when not simulating
        assertEquals(stick, vessel.insertItem(1, stick, false));

        assertEquals(stick, vessel.getStackInSlot(0)); // The vessel should have been unmodified
        assertEquals(ItemStack.EMPTY, vessel.getStackInSlot(1));
    }

    @Test
    public void testMovingInAndOutOfVesselDoesNotChangeDecayDate()
    {
        final ItemStack foodStack = new ItemStack(TFCItems.FOOD.get(Food.BANANA));
        final ItemStack vesselStack = new ItemStack(TFCItems.VESSEL);
        final Vessel vessel = Vessel.get(vesselStack);
        final IFood initialFood = FoodCapability.get(foodStack);

        assertNotNull(vessel);
        assertNotNull(initialFood);

        final long initialExpiryDate = initialFood.getRottenDate();
        final ItemStack excess = vessel.insertItem(0, foodStack, false);

        assertTrue(excess.isEmpty());

        final ItemStack insideFoodStack = vessel.getStackInSlot(0);
        final IFood insideFood = FoodCapability.get(insideFoodStack);

        assertNotNull(insideFood);

        final long insideExpiryDate = insideFood.getRottenDate();

        assertTrue(insideExpiryDate > initialExpiryDate);

        final ItemStack afterExtractFoodStack = vessel.extractItem(0, 64, false);
        final IFood afterExtractFood = FoodCapability.get(afterExtractFoodStack);

        assertTrue(vessel.getStackInSlot(0).isEmpty());
        assertNotNull(afterExtractFood);

        final long afterExtractExpiryDate = afterExtractFood.getRottenDate();

        assertEquals(initialExpiryDate, afterExtractExpiryDate);
    }

    @Test
    public void checkTicksToHeat10mBCopperInSmallVessel()
    {
        final ItemStack copper10mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER));
        final ItemStack vessel = vesselOf(copper10mB);

        checkTimeToHeat(vessel, 1467); // Heating ore
        clearHeat(vessel);
        checkTimeToHeat(vessel, 883); // Heating liquid
    }

    @Test
    public void checkTicksToHeat100mBCopperInSmallVessel()
    {
        final ItemStack copper100mB = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL), 4);
        final ItemStack vessel = vesselOf(copper100mB);

        checkTimeToHeat(vessel, 2850); // Heating ore
        clearHeat(vessel);
        checkTimeToHeat(vessel, 1261); // Heating liquid
    }

    @Test
    public void checkTicksToHeat560mBCopperInSmallVessel()
    {
        final ItemStack copper560mB = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        final ItemStack vessel = vesselOf(copper560mB);

        checkTimeToHeat(vessel, 8476); // Heating ore
        clearHeat(vessel);
        checkTimeToHeat(vessel, 3192); // Heating liquid
    }

    @Test
    public void checkTicksToHeat2240mBCopperInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        final ItemStack vessel = vesselOf(copper16, copper16, copper16, copper16);

        checkTimeToHeat(vessel, 30827); // Heating ore
        clearHeat(vessel);
        checkTimeToHeat(vessel, 10248); // Heating liquid
    }

    @Test
    public void checkTicksToCool10mBCopperInSmallVessel()
    {
        checkTimeSpentMoltenAfterPitKiln(504, false, false, Metal.COPPER, new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER)));
    }

    @Test
    public void checkTicksToCool100mBCopperInSmallVessel()
    {
        checkTimeSpentMoltenAfterPitKiln(719, false, false, Metal.COPPER, new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL), 4));
    }

    @Test
    public void checkTicksToCool560mBCopperInSmallVessel()
    {
        checkTimeSpentMoltenAfterPitKiln(1824, false, false, Metal.COPPER, new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16));
    }

    @Test
    public void checkTicksToCool2240mBCopperInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        checkTimeSpentMoltenAfterPitKiln(5851, false, false, Metal.COPPER, copper16, copper16, copper16, copper16);
    }

    @Test
    public void checkPitKilnCanMelt16CopperInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        checkTimeSpentMoltenAfterPitKiln(560, true, true, Metal.COPPER, copper16);
    }

    @Test
    public void checkPitKilnCanMelt64CopperInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        checkTimeSpentMoltenAfterPitKiln(2219, false, true, Metal.COPPER, copper16, copper16, copper16, copper16);
    }

    @Test
    public void checkPitKilnCanMelt100mBCopperInSmallVessel()
    {
        final ItemStack copper100mB = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL), 4);
        checkTimeSpentMoltenAfterPitKiln(100, true, true, Metal.COPPER, copper100mB);
    }

    @Test
    public void checkPitKilnCanMelt100mBBronzeInSmallVessel()
    {
        final ItemStack copper90mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER), 9);
        final ItemStack tin10mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.CASSITERITE), 1);
        checkTimeSpentMoltenAfterPitKiln(100, true, true, Metal.BRONZE, copper90mB, tin10mB);
    }

    @Test
    public void checkPitKilnCanMelt100mBBismuthBronzeInSmallVessel()
    {
        final ItemStack copper50mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER), 5);
        final ItemStack zinc30mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.SPHALERITE), 3);
        final ItemStack bismuth20mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.BISMUTHINITE), 2);
        checkTimeSpentMoltenAfterPitKiln(100, true, true, Metal.BISMUTH_BRONZE, copper50mB, zinc30mB, bismuth20mB);
    }

    @Test
    public void checkPitKilnCanMelt100mBBlackBronzeInSmallVessel()
    {
        final ItemStack copper60mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER), 6);
        final ItemStack silver20mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_SILVER), 2);
        final ItemStack gold20mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_GOLD), 2);
        checkTimeSpentMoltenAfterPitKiln(100, true, true, Metal.BLACK_BRONZE, copper60mB, silver20mB, gold20mB);
    }

    @Test
    public void checkPitKilnCanMeltBronzeInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        final ItemStack copper8 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 8);
        final ItemStack tin4 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.CASSITERITE).get(Ore.Grade.RICH), 4);
        checkTimeSpentMoltenAfterPitKiln(1540, true, true, Metal.BRONZE, copper16, copper16, copper8, tin4);
    }

    @Test
    public void checkPitKilnCanMeltBismuthBronzeInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        final ItemStack copper10 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 10);
        final ItemStack bismuth10 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.BISMUTHINITE).get(Ore.Grade.RICH), 10);
        final ItemStack zinc14 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.SPHALERITE).get(Ore.Grade.RICH), 14);
        checkTimeSpentMoltenAfterPitKiln(1750, true, true, Metal.BISMUTH_BRONZE, copper16, copper10, bismuth10, zinc14);
    }

    @Test
    public void checkPitKilnCanMeltBlackBronzeInSmallVessel()
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH), 16);
        final ItemStack gold16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_GOLD).get(Ore.Grade.RICH), 16);
        final ItemStack silver16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_SILVER).get(Ore.Grade.RICH), 16);
        checkTimeSpentMoltenAfterPitKiln(2235, false, true, Metal.BLACK_BRONZE, copper16, copper16, gold16, silver16);
    }
    
    @Test
    public void checkCookingClayWithHeat()
    {
        assertEquals(350, ticksRequiredToMelt(new ItemStack(Blocks.CLAY)));
    }

    @Test
    public void checkCookingClayInVesselWithHeat()
    {
        assertEquals(1190, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY)));
    }

    @Test
    public void checkCooking4ClayInVesselWithHeat()
    {
        assertEquals(1924, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY, 4)));
    }

    @Test
    public void checkCooking4ClaySpreadOutInVesselWithHeat()
    {
        final ItemStack clay = new ItemStack(Blocks.CLAY);
        assertEquals(1924, ticksRequiredToMeltVessel(clay, clay, clay, clay));
    }

    @Test
    public void checkCooking8ClayInVesselWithHeat()
    {
        assertEquals(2903, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY, 8)));
    }

    @Test
    public void checkCooking16ClayInVesselWithHeat()
    {
        assertEquals(4862, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY, 16)));
    }

    @Test
    public void checkBronzeIngotTimeSpentWorkable()
    {
        assertEquals(1358, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.INGOT))));
    }

    @Test
    public void checkBronzeDoubleIngotTimeSpentWorkable()
    {
        assertEquals(2715, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.BRONZE).get(Metal.ItemType.DOUBLE_INGOT))));
    }

    @Test
    public void checkIronIngotTimeSpentWorkable()
    {
        assertEquals(2193, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.INGOT))));
    }

    @Test
    public void checkIronDoubleIngotTimeSpentWorkable()
    {
        assertEquals(4386, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.DOUBLE_INGOT))));
    }

    private ItemStack vesselOf(ItemStack... stacks)
    {
        final ItemStack vesselStack = new ItemStack(TFCItems.VESSEL);
        final Vessel vessel = Vessel.get(vesselStack);

        assertNotNull(vessel);
        assertTrue(stacks.length <= 4);

        for (int i = 0; i < stacks.length; i++)
        {
            vessel.insertItem(i, stacks[i], false);
        }

        return vesselStack;
    }
    
    private void clearHeat(ItemStack stack)
    {
        HeatCapability.setTemperature(stack, 0f);
        final IHeat heat = HeatCapability.get(stack);
        
        assertNotNull(heat);
        assertEquals(heat.getTemperature(), 0f);
    }

    private void checkTimeToHeat(ItemStack stack, int expectedTicks)
    {
        final float targetTemperature = TFCConfig.SERVER.pitKilnTemperature.get();
        final IHeat heat = HeatCapability.get(stack);
        assertNotNull(heat);

        try (CalendarTransaction tr = Calendars.get().transaction())
        {
            while (heat.getTemperature() < targetTemperature)
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);
                assertNotEquals(tr.ticks(), 1_000_000, "Loop did not terminate with stack " + stack);
            }

            assertEquals(expectedTicks, tr.ticks(), "Expected " + expectedTicks + " to heat " + stack + " to " + targetTemperature + "C, got " + tr.ticks());
        }
    }

    private void checkTimeSpentMoltenAfterPitKiln(int expectedTicks, boolean expectEmpty, boolean whileDraining, Metal moltenMetal, ItemStack... contents)
    {
        final ItemStack vesselStack = vesselOf(contents);
        final Vessel vessel = Vessel.get(vesselStack);
        assertNotNull(vessel);

        try (CalendarTransaction tr = Calendars.get().transaction())
        {
            vessel.setTemperature(TFCConfig.SERVER.pitKilnTemperature.get()); // Heat immediately up to pit kiln temperature
            assertTrue(vessel.isMolten());

            // Extract metal while molten
            for (int i = 0; i < expectedTicks; i++)
            {
                tr.add(1);
                assertTrue(vessel.isMolten(), "Not molten after " + i + " ticks, temperature is " + vessel.getTemperature());
                if (whileDraining)
                {
                    assertEquals(new FluidStack(TFCFluids.METALS.get(moltenMetal).getSource(), 1), vessel.drain(1, IFluidHandler.FluidAction.EXECUTE));
                }
            }

            tr.add(1);
            if (expectEmpty && whileDraining)
            {
                assertTrue(vessel.isEmpty(), "Vessel still contains metal, expected empty, at temperature " + vessel.getTemperature());
            }
            else
            {
                // Assert not molten anymore
                assertFalse(vessel.isEmpty(), "Expected non-empty after " + expectedTicks + " ticks");
                assertFalse(vessel.isMolten(), "Expected non-molten after " + expectedTicks + " ticks");
            }
        }
    }

    private long ticksRequiredToMeltVessel(ItemStack... contents)
    {
        // Only works if all the vessel contents are the same (and have the same recipe)
        return ticksRequiredToMelt(vesselOf(contents), HeatingRecipe.getRecipe(contents[0]));
    }

    private long ticksRequiredToMelt(ItemStack stack)
    {
        return ticksRequiredToMelt(stack, HeatingRecipe.getRecipe(stack));
    }

    private long ticksRequiredToMelt(ItemStack stack, @Nullable HeatingRecipe recipe)
    {
        final IHeat heat = HeatCapability.get(stack);

        assertNotNull(heat, "Heat missing for stack: " + heat);
        assertNotNull(recipe, "Recipe missing for stack: " + stack);

        try (CalendarTransaction tr = Calendars.get().transaction())
        {
            while (!recipe.isValidTemperature(heat.getTemperature()))
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);

                assertNotEquals(tr.ticks(), 1_000_000, "Loop did not terminate with stack " + stack);
            }
            return tr.ticks();
        }
    }

    private long ticksRequiredToBeNotWorkable(ItemStack stack)
    {
        final IHeat heat = HeatCapability.get(stack);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

        assertNotNull(heat, "Heat missing for stack: " + heat);
        assertNotNull(recipe, "Recipe missing for stack: " + stack);

        try (CalendarTransaction tr = Calendars.get().transaction())
        {
            heat.setTemperature(recipe.getTemperature()); // Assume melting > working
            while (heat.getWorkingTemperature() < heat.getTemperature())
            {
                tr.add(1);
                assertNotEquals(tr.ticks(), 1_000_000, "Loop did not terminate with stack " + stack);
            }
            return tr.ticks();
        }
    }
}
