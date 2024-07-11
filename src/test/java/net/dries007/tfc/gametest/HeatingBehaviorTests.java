/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.gametest;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.VesselLike;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.data.Metal;

import static org.junit.jupiter.api.Assertions.*;

@GameTestHolder
public class HeatingBehaviorTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(unitTest = true)
    public void checkTicksToHeat10mBCopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper10mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER).get());
        final ItemStack vessel = vesselWithContents(copper10mB);
        checkTicksToHeatInForge(vessel, 1468, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating ore
        clearTemperature(vessel);
        checkTicksToHeatInForge(vessel, 884, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating liquid
    }

    @MyTest(unitTest = true)
    public void checkTicksToHeat100mBCopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper100mB = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get(), 4);
        final ItemStack vessel = vesselWithContents(copper100mB);
        checkTicksToHeatInForge(vessel, 2850, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating ore
        clearTemperature(vessel);
        checkTicksToHeatInForge(vessel, 1262, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating liquid
    }

    @MyTest(unitTest = true)
    public void checkTicksToHeat560mBCopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper560mB = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        final ItemStack vessel = vesselWithContents(copper560mB);
        checkTicksToHeatInForge(vessel, 8478, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating ore
        clearTemperature(vessel);
        checkTicksToHeatInForge(vessel, 3194, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating liquid
    }

    @MyTest(unitTest = true)
    public void checkTicksToHeat2240mBCopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        final ItemStack vessel = vesselWithContents(copper16, copper16, copper16, copper16);
        checkTicksToHeatInForge(vessel, 30811, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating ore
        clearTemperature(vessel);
        checkTicksToHeatInForge(vessel, 10249, TFCConfig.SERVER.pitKilnTemperature.get()); // Heating liquid
    }

    @MyTest(unitTest = true)
    public void checkTicksToCool10mBCopperInSmallVessel(GameTestHelper helper)
    {
        checkTicksSpentMoltenAfterPitKiln(helper, 505, false, false, Metal.Default.COPPER, new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER).get()));
    }

    @MyTest(unitTest = true)
    public void checkTicksToCool100mBCopperInSmallVessel(GameTestHelper helper)
    {
        checkTicksSpentMoltenAfterPitKiln(helper, 720, false, false, Metal.Default.COPPER, new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get(), 4));
    }

    @MyTest(unitTest = true)
    public void checkTicksToCool560mBCopperInSmallVessel(GameTestHelper helper)
    {
        checkTicksSpentMoltenAfterPitKiln(helper, 1824, false, false, Metal.Default.COPPER, new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16));
    }

    @MyTest(unitTest = true)
    public void checkTicksToCool2240mBCopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        checkTicksSpentMoltenAfterPitKiln(helper, 5856, false, false, Metal.Default.COPPER, copper16, copper16, copper16, copper16);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMelt16CopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        checkTicksSpentMoltenAfterPitKiln(helper, 560, true, true, Metal.Default.COPPER, copper16);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMelt64CopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        checkTicksSpentMoltenAfterPitKiln(helper, 2219, false, true, Metal.Default.COPPER, copper16, copper16, copper16, copper16);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMelt100mBCopperInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper100mB = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.NORMAL).get(), 4);
        checkTicksSpentMoltenAfterPitKiln(helper, 100, true, true, Metal.Default.COPPER, copper100mB);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMelt100mBBronzeInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper90mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER).get(), 9);
        final ItemStack tin10mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.CASSITERITE).get(), 1);
        checkTicksSpentMoltenAfterPitKiln(helper, 100, true, true, Metal.Default.BRONZE, copper90mB, tin10mB);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMelt100mBBismuthBronzeInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper50mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER).get(), 5);
        final ItemStack zinc30mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.SPHALERITE).get(), 3);
        final ItemStack bismuth20mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.BISMUTHINITE).get(), 2);
        checkTicksSpentMoltenAfterPitKiln(helper, 100, true, true, Metal.Default.BISMUTH_BRONZE, copper50mB, zinc30mB, bismuth20mB);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMelt100mBBlackBronzeInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper60mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_COPPER).get(), 6);
        final ItemStack silver20mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_SILVER).get(), 2);
        final ItemStack gold20mB = new ItemStack(TFCBlocks.SMALL_ORES.get(Ore.NATIVE_GOLD).get(), 2);
        checkTicksSpentMoltenAfterPitKiln(helper, 100, true, true, Metal.Default.BLACK_BRONZE, copper60mB, silver20mB, gold20mB);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMeltBronzeInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        final ItemStack copper8 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 8);
        final ItemStack tin4 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.CASSITERITE).get(Ore.Grade.RICH).get(), 4);
        checkTicksSpentMoltenAfterPitKiln(helper, 1540, true, true, Metal.Default.BRONZE, copper16, copper16, copper8, tin4);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMeltBismuthBronzeInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        final ItemStack copper10 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 10);
        final ItemStack bismuth10 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.BISMUTHINITE).get(Ore.Grade.RICH).get(), 10);
        final ItemStack zinc14 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.SPHALERITE).get(Ore.Grade.RICH).get(), 14);
        checkTicksSpentMoltenAfterPitKiln(helper, 1750, true, true, Metal.Default.BISMUTH_BRONZE, copper16, copper10, bismuth10, zinc14);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanMeltBlackBronzeInSmallVessel(GameTestHelper helper)
    {
        final ItemStack copper16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        final ItemStack gold16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_GOLD).get(Ore.Grade.RICH).get(), 16);
        final ItemStack silver16 = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_SILVER).get(Ore.Grade.RICH).get(), 16);
        checkTicksSpentMoltenAfterPitKiln(helper, 2235, false, true, Metal.Default.BLACK_BRONZE, copper16, copper16, gold16, silver16);
    }

    @MyTest(unitTest = true)
    public void checkPitKilnCanFirePottery(GameTestHelper helper)
    {
        final PitKilnBlockEntity pitKiln = pitKiln(helper);
        final IItemHandler pitKilnInventory = Helpers.getCapability(pitKiln, Capabilities.ITEM);

        assertNotNull(pitKilnInventory);

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            pitKilnInventory.insertItem(0, new ItemStack(TFCItems.UNFIRED_VESSEL.get()), false);

            tr.add(8000L);
            pitKiln.updateCache();
            pitKiln.cookContents();

            final ItemStack outputStack = pitKilnInventory.extractItem(0, 1, false);

            // Can't compare NBT, as the new vessel will have heat
            assertEquals(TFCItems.VESSEL.get(), outputStack.getItem());
            assertEquals(1, outputStack.getCount());

            final IHeat outputHeat = HeatCapability.get(outputStack);

            assertNotNull(outputHeat);
            assertEquals(1400.0f, outputHeat.getTemperature(), "Pit kiln did not reach expected temperature, got: " + outputHeat.getTemperature());
        }
    }

    @MyTest(unitTest = true)
    public void checkCookingStickWithHeat(GameTestHelper helper)
    {
        assertEquals(76, ticksRequiredToMelt(new ItemStack(Items.STICK)));
    }

    @MyTest(unitTest = true)
    public void checkCookingStickBunchWithHeat(GameTestHelper helper)
    {
        assertEquals(601, ticksRequiredToMelt(new ItemStack(TFCItems.STICK_BUNCH.get())));
    }

    @MyTest(unitTest = true)
    public void checkCookingClayWithHeat(GameTestHelper helper)
    {
        assertEquals(350, ticksRequiredToMelt(new ItemStack(Blocks.CLAY)));
    }

    @MyTest(unitTest = true)
    public void checkCookingClayInVesselWithHeat(GameTestHelper helper)
    {
        assertEquals(1190, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY)));
    }

    @MyTest(unitTest = true)
    public void checkCooking4ClayInVesselWithHeat(GameTestHelper helper)
    {
        assertEquals(1924, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY, 4)));
    }

    @MyTest(unitTest = true)
    public void checkCooking4ClaySpreadOutInVesselWithHeat(GameTestHelper helper)
    {
        final ItemStack clay = new ItemStack(Blocks.CLAY);
        assertEquals(1924, ticksRequiredToMeltVessel(clay, clay, clay, clay));
    }

    @MyTest(unitTest = true)
    public void checkCooking8ClayInVesselWithHeat(GameTestHelper helper)
    {
        assertEquals(2903, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY, 8)));
    }

    @MyTest(unitTest = true)
    public void checkCooking16ClayInVesselWithHeat(GameTestHelper helper)
    {
        assertEquals(4862, ticksRequiredToMeltVessel(new ItemStack(Blocks.CLAY, 16)));
    }

    @MyTest(unitTest = true)
    public void checkBronzeIngotTimeSpentWorkable(GameTestHelper helper)
    {
        assertEquals(1358, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BRONZE).get(Metal.ItemType.INGOT).get())));
    }

    @MyTest(unitTest = true)
    public void checkBronzeDoubleIngotTimeSpentWorkable(GameTestHelper helper)
    {
        assertEquals(2715, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BRONZE).get(Metal.ItemType.DOUBLE_INGOT).get())));
    }

    @MyTest(unitTest = true)
    public void checkIronIngotTimeSpentWorkable(GameTestHelper helper)
    {
        assertEquals(2193, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get())));
    }

    @MyTest(unitTest = true)
    public void checkIronDoubleIngotTimeSpentWorkable(GameTestHelper helper)
    {
        assertEquals(4386, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.DOUBLE_INGOT).get())));
    }

    private void clearTemperature(ItemStack stack)
    {
        final IHeat heat = HeatCapability.get(stack);
        assertNotNull(heat);
        heat.setTemperature(0);
        assertEquals(0f, heat.getTemperature());
    }

    private void checkTicksToHeatInForge(ItemStack stack, int expectedTicks, float targetTemperature)
    {
        final IHeat heat = HeatCapability.get(stack);
        assertNotNull(heat);

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            int ticks = 0;
            while (heat.getTemperature() < targetTemperature)
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);
                ticks++;
                assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack));
            }

            assertEquals(expectedTicks, ticks, "Expected " + expectedTicks + " to heat " + TestAssertions.wrap(stack) + " to " + targetTemperature + "°C, got " + ticks);
        }
    }

    private void checkTicksSpentMoltenAfterPitKiln(GameTestHelper helper, int expectedTicks, boolean expectEmpty, boolean whileDraining, Metal.Default moltenMetal, ItemStack... contents)
    {
        final PitKilnBlockEntity pitKiln = pitKiln(helper);
        final IItemHandler pitKilnInventory = Helpers.getCapability(pitKiln, Capabilities.ITEM);

        final ItemStack vesselStack = vesselWithContents(contents);
        final VesselLike vessel = VesselLike.get(vesselStack);

        assertNotNull(vessel);
        assertNotNull(pitKilnInventory);

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            pitKilnInventory.insertItem(0, vesselStack, false);

            tr.add(8000L);
            pitKiln.updateCache();
            pitKiln.cookContents();

            final ItemStack outputStack = pitKilnInventory.extractItem(0, 1, false);
            final VesselLike outputVessel = VesselLike.get(outputStack);

            // Assert the content is molten and at the expected temperature
            assertNotNull(outputVessel);
            assertTrue(outputVessel.isMolten());

            // Extract metal while molten
            for (int i = 0; i < expectedTicks; i++)
            {
                tr.add(1);
                assertTrue(outputVessel.isMolten(), "Not molten after " + i + " ticks, temperature is " + outputVessel.getTemperature() + " and mode is " + outputVessel.mode());
                if (whileDraining)
                {
                    assertEquals(new FluidStack(TFCFluids.METALS.get(moltenMetal).getSource(), 1), outputVessel.drain(1, IFluidHandler.FluidAction.EXECUTE));
                }
            }

            tr.add(1);
            if (expectEmpty && whileDraining)
            {
                assertSame(outputVessel.mode(), VesselLike.Mode.INVENTORY, "Vessel still contains metal, expected empty, at temperature " + outputVessel.getTemperature());
            }
            else
            {
                // Assert not molten anymore
                assertSame(outputVessel.mode(), VesselLike.Mode.SOLID_ALLOY, "Still molten after " + expectedTicks + " ticks, the temperature is " + outputVessel.getTemperature() + " with content " + outputVessel.mode());
            }
        }
    }

    private PitKilnBlockEntity pitKiln(GameTestHelper helper)
    {
        final BlockPos pos = helper.absolutePos(new BlockPos(0, 1, 0));
        final PitKilnBlockEntity pitKiln = new PitKilnBlockEntity(pos, TFCBlocks.PIT_KILN.get().defaultBlockState());
        pitKiln.setLevel(helper.getLevel());
        return pitKiln;
    }

    private ItemStack vesselWithContents(ItemStack... stacks)
    {
        final ItemStack vesselStack = new ItemStack(TFCItems.VESSEL.get());
        final VesselLike vessel = VesselLike.get(vesselStack);

        assertNotNull(vessel);
        assertTrue(stacks.length <= 4);

        for (int i = 0; i < stacks.length; i++)
        {
            vessel.insertItem(i, stacks[i].copy(), false);
        }

        return vesselStack;
    }

    private int ticksRequiredToMeltVessel(ItemStack... contents)
    {
        // Only works if all the vessel contents are the same (and have the same recipe)
        final ItemStack stack = vesselWithContents(contents);
        final IHeat heat = HeatCapability.get(stack);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(contents[0]);

        assertNotNull(heat, "Heat missing for stack: " + heat);
        assertNotNull(recipe, "Recipe missing for vessel content: " + stack);

        int ticks = 0;
        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            while (!recipe.isValidTemperature(heat.getTemperature()))
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);
                ticks += 1;

                assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack) + " and recipe " + TestAssertions.wrap(recipe));
            }
        }

        return ticks;
    }

    private int ticksRequiredToMelt(ItemStack stack)
    {
        final IHeat heat = HeatCapability.get(stack);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

        assertNotNull(heat, "Heat missing for stack: " + heat);
        assertNotNull(recipe, "Recipe missing for stack: " + stack);

        int ticks = 0;
        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            while (!recipe.isValidTemperature(heat.getTemperature()))
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);
                ticks += 1;

                assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack) + " and recipe " + TestAssertions.wrap(recipe));
            }
        }

        return ticks;
    }

    private int ticksRequiredToBeNotWorkable(ItemStack stack)
    {
        final IHeat heat = HeatCapability.get(stack);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

        assertNotNull(heat, "Heat missing for stack: " + heat);
        assertNotNull(recipe, "Recipe missing for stack: " + stack);

        int ticks = 0;
        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            heat.setTemperature(recipe.getTemperature()); // Assume melting > working
            while (heat.getWorkingTemperature() < heat.getTemperature())
            {
                tr.add(1);
                ticks += 1;

                assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack) + " and recipe " + TestAssertions.wrap(recipe));
            }
        }

        return ticks;
    }
}
