/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.AutoGameTest;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.VesselLike;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;

@GameTestHolder(TerraFirmaCraft.MOD_ID)
public class HeatingBehaviorTest
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.unitTestGenerator();
    }

    @AutoGameTest
    public void checkPitKilnCanMeltFullSmallVessel(GameTestHelper helper)
    {
        final PitKilnBlockEntity pitKiln = pitKiln(helper);
        final IItemHandler pitKilnInventory = Helpers.getCapability(pitKiln, Capabilities.ITEM);

        // 4 x 16 x 35 mB = 2240 Copper
        final ItemStack copperOreStack = new ItemStack(TFCItems.GRADED_ORES.get(Ore.NATIVE_COPPER).get(Ore.Grade.RICH).get(), 16);
        final ItemStack vesselStack = vesselWithContents(copperOreStack, copperOreStack, copperOreStack, copperOreStack);
        final VesselLike vessel = VesselLike.get(vesselStack);

        TestAssertions.assertNotNull(vessel);
        TestAssertions.assertNotNull(pitKilnInventory);

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            pitKilnInventory.insertItem(0, vesselStack, false);

            tr.add(8000L);
            pitKiln.updateCache();
            pitKiln.cookContents();

            final ItemStack outputStack = pitKilnInventory.extractItem(0, 1, false);
            final VesselLike outputVessel = VesselLike.get(outputStack);

            // Assert the content is molten and at the expected temperature
            TestAssertions.assertNotNull(outputVessel);
            TestAssertions.assertTrue(outputVessel.isMolten());

            // Extract metal while molten
            final int expectedMoltenTicks = 1485;
            for (int i = 0; i < expectedMoltenTicks; i++)
            {
                tr.add(1);
                TestAssertions.assertTrue(outputVessel.isMolten(), "Not molten after " + i + " ticks, temperature is " + outputVessel.getTemperature());
                TestAssertions.assertEquals(new FluidStack(TFCFluids.METALS.get(Metal.Default.COPPER).getSource(), 1), outputVessel.drain(1, IFluidHandler.FluidAction.EXECUTE));
            }

            // Assert not molten anymore
            TestAssertions.assertFalse(!outputVessel.isMolten(), "Still molten after " + expectedMoltenTicks + " ticks, the temperature is " + outputVessel.getTemperature());
        }
    }

    @AutoGameTest
    public void checkPitKilnCanFirePottery(GameTestHelper helper)
    {
        final PitKilnBlockEntity pitKiln = pitKiln(helper);
        final IItemHandler pitKilnInventory = Helpers.getCapability(pitKiln, Capabilities.ITEM);

        TestAssertions.assertNotNull(pitKilnInventory);

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            pitKilnInventory.insertItem(0, new ItemStack(TFCItems.UNFIRED_VESSEL.get()), false);

            tr.add(8000L);
            pitKiln.updateCache();
            pitKiln.cookContents();

            final ItemStack outputStack = pitKilnInventory.extractItem(0, 1, false);

            // Can't compare NBT, as the new vessel will have heat
            TestAssertions.assertEquals(TFCItems.VESSEL.get(), outputStack.getItem());
            TestAssertions.assertEquals(1, outputStack.getCount());

            final IHeat outputHeat = Helpers.getCapability(outputStack, HeatCapability.CAPABILITY);

            TestAssertions.assertNotNull(outputHeat);
            TestAssertions.assertEquals(1600.0f, outputHeat.getTemperature());
        }
    }

    @AutoGameTest
    public void checkCookingStickWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(100, ticksRequiredToMelt(new ItemStack(Items.STICK)));
    }

    @AutoGameTest
    public void checkCookingStickBunchWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(600, ticksRequiredToMelt(new ItemStack(TFCItems.STICK_BUNCH.get())));
    }

    @AutoGameTest
    public void checkCookingSandWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(251, ticksRequiredToMelt(new ItemStack(TFCBlocks.SAND.get(SandBlockType.BLACK).get())));
    }

    @AutoGameTest
    public void checkCookingSandInVesselWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(251, ticksRequiredToMeltVessel(new ItemStack(TFCBlocks.SAND.get(SandBlockType.BLACK).get())));
    }

    @AutoGameTest
    public void checkCooking4SandInVesselWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(1001, ticksRequiredToMeltVessel(new ItemStack(TFCBlocks.SAND.get(SandBlockType.BLACK).get(), 4)));
    }

    @AutoGameTest
    public void checkCooking4SandSpreadOutInVesselWithHeat(GameTestHelper helper)
    {
        final ItemStack sand = new ItemStack(TFCBlocks.SAND.get(SandBlockType.BLACK).get());
        TestAssertions.assertEquals(251, ticksRequiredToMeltVessel(sand, sand, sand, sand));
    }

    @AutoGameTest
    public void checkCooking8SandInVesselWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(2001, ticksRequiredToMeltVessel(new ItemStack(TFCBlocks.SAND.get(SandBlockType.BLACK).get(), 8)));
    }

    @AutoGameTest
    public void checkCooking16SandInVesselWithHeat(GameTestHelper helper)
    {
        TestAssertions.assertEquals(4001, ticksRequiredToMeltVessel(new ItemStack(TFCBlocks.SAND.get(SandBlockType.BLACK).get(), 16)));
    }

    @AutoGameTest
    public void checkBronzeIngotTimeSpentWorkable(GameTestHelper helper)
    {
        TestAssertions.assertEquals(1086, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BRONZE).get(Metal.ItemType.INGOT).get())));
    }

    @AutoGameTest
    public void checkIronIngotTimeSpentWorkable(GameTestHelper helper)
    {
        TestAssertions.assertEquals(1755, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.INGOT).get())));
    }

    @AutoGameTest
    public void checkBronzeDoubleIngotTimeSpentWorkable(GameTestHelper helper)
    {
        TestAssertions.assertEquals(1086, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BRONZE).get(Metal.ItemType.DOUBLE_INGOT).get())));
    }

    @AutoGameTest
    public void checkIronDoubleIngotTimeSpentWorkable(GameTestHelper helper)
    {
        TestAssertions.assertEquals(1755, ticksRequiredToBeNotWorkable(new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.WROUGHT_IRON).get(Metal.ItemType.DOUBLE_INGOT).get())));
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

        TestAssertions.assertNotNull(vessel);
        TestAssertions.assertTrue(stacks.length <= 4);

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
        final IHeat heat = Helpers.getCapability(stack, HeatCapability.CAPABILITY);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(contents[0]);

        TestAssertions.assertNotNull(heat, "Heat missing for stack: " + heat);
        TestAssertions.assertNotNull(recipe, "Recipe missing for vessel content: " + stack);

        int ticks = 0;
        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            while (!recipe.isValidTemperature(heat.getTemperature()))
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);
                ticks += 1;

                TestAssertions.assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack) + " and recipe " + TestAssertions.wrap(recipe));
            }
        }

        return ticks;
    }

    private int ticksRequiredToMelt(ItemStack stack)
    {
        final IHeat heat = Helpers.getCapability(stack, HeatCapability.CAPABILITY);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

        TestAssertions.assertNotNull(heat, "Heat missing for stack: " + heat);
        TestAssertions.assertNotNull(recipe, "Recipe missing for stack: " + stack);

        int ticks = 0;
        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            while (!recipe.isValidTemperature(heat.getTemperature()))
            {
                HeatCapability.addTemp(heat, Heat.maxVisibleTemperature());
                tr.add(1);
                ticks += 1;

                TestAssertions.assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack) + " and recipe " + TestAssertions.wrap(recipe));
            }
        }

        return ticks;
    }

    private int ticksRequiredToBeNotWorkable(ItemStack stack)
    {
        final IHeat heat = Helpers.getCapability(stack, HeatCapability.CAPABILITY);
        final HeatingRecipe recipe = HeatingRecipe.getRecipe(stack);

        TestAssertions.assertNotNull(heat, "Heat missing for stack: " + heat);
        TestAssertions.assertNotNull(recipe, "Recipe missing for stack: " + stack);

        int ticks = 0;
        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            heat.setTemperature(recipe.getTemperature()); // Assume melting > working
            while (heat.getWorkingTemperature() < heat.getTemperature())
            {
                tr.add(1);
                ticks += 1;

                TestAssertions.assertNotEquals(ticks, 1_000_000, "Loop did not terminate with stack " + TestAssertions.wrap(stack) + " and recipe " + TestAssertions.wrap(recipe));
            }
        }

        return ticks;
    }
}
