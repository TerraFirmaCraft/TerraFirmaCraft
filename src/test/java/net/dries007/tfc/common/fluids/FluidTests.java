/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

import com.alcatrazescapee.mcjunitlib.framework.IntegrationTest;
import com.alcatrazescapee.mcjunitlib.framework.IntegrationTestClass;
import com.alcatrazescapee.mcjunitlib.framework.IntegrationTestHelper;
import net.dries007.tfc.common.blocks.TFCBlocks;

@IntegrationTestClass("fluids")
public class FluidTests
{
    @IntegrationTest("dispenser_salt_water")
    public void testDispenserPlaceSaltWater(IntegrationTestHelper helper)
    {
        helper.pushButton(new BlockPos(1, 2, 2));
        helper.assertBlockAt(new BlockPos(1, 1, 1), TFCBlocks.SALT_WATER.get(), "Salt water should have been dispensed");
    }

    @IntegrationTest("dispenser_empty")
    public void testDispenserPickupSaltWater(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(1, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        helper.runAfter(10, () -> helper.pushButton(new BlockPos(1, 2, 2)));
        helper.assertAirAt(new BlockPos(1, 1, 1), "Salt water should have been bucketed");
    }

    @IntegrationTest("dispenser_spring_water")
    public void testDispenserPlaceSpringWater(IntegrationTestHelper helper)
    {
        helper.pushButton(new BlockPos(1, 2, 2));
        helper.assertBlockAt(new BlockPos(1, 1, 1), TFCBlocks.SPRING_WATER.get(), "Spring water should have been dispensed");
    }

    @IntegrationTest("dispenser_empty")
    public void testDispenserPickupSpringWater(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(1, 1, 1), sourceBlock(TFCFluids.SPRING_WATER));
        helper.runAfter(10, () -> helper.pushButton(new BlockPos(1, 2, 2)));
        helper.assertAirAt(new BlockPos(1, 1, 1), "Spring water should have been bucketed");
    }

    @IntegrationTest("create_sources")
    public void testVanillaCreateSources(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(1, 1, 1), waterSource());
        helper.setBlockState(new BlockPos(3, 1, 1), waterSource());
        helper.assertFluidAt(new BlockPos(2, 1, 1), FluidState::isSource, "Vanilla water should create source blocks");
    }

    @IntegrationTest("create_sources")
    public void testSaltWaterCreateSources(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(1, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        helper.setBlockState(new BlockPos(3, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        helper.assertFluidAt(new BlockPos(2, 1, 1), FluidState::isSource, "Salt water should create source blocks");
    }

    @IntegrationTest("create_sources")
    public void testSpringWaterCannotCreateSources(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(1, 1, 1), sourceBlock(TFCFluids.SPRING_WATER));
        helper.setBlockState(new BlockPos(3, 1, 1), sourceBlock(TFCFluids.SPRING_WATER));
        helper.assertFluidAt(new BlockPos(2, 1, 1), state -> !state.isSource(), "Spring water should not create source blocks");
    }

    @IntegrationTest("create_sources_blocked")
    public void testSaltWaterCreatesSourcesReplacingSpringWater(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(2, 1, 5), sourceBlock(TFCFluids.SPRING_WATER));
        helper.runAfter(60, () -> {
            helper.setBlockState(new BlockPos(1, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
            helper.setBlockState(new BlockPos(3, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        });
        helper.assertFluidAt(new BlockPos(2, 1, 1), state -> state.isSource() && state.getType() == TFCFluids.SALT_WATER.getSource(), "Salt water should replace spring water and create source");
    }

    @IntegrationTest("create_sources_blocked")
    public void testSaltWaterCreatesSourcesReplacingVanillaWater(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(2, 1, 5), waterSource());
        helper.runAfter(60, () -> {
            helper.setBlockState(new BlockPos(1, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
            helper.setBlockState(new BlockPos(3, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        });
        helper.assertFluidAt(new BlockPos(2, 1, 1), state -> state.isSource() && state.getType() == TFCFluids.SALT_WATER.getSource(), "Salt water should replace spring water and create source");
    }

    @IntegrationTest("create_sources_blocked")
    public void testVanillaWaterCreatesSourcesReplacingSaltWater(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(2, 1, 5), sourceBlock(TFCFluids.SALT_WATER));
        helper.runAfter(60, () -> {
            helper.setBlockState(new BlockPos(1, 1, 1), waterSource());
            helper.setBlockState(new BlockPos(3, 1, 1), waterSource());
        });
        helper.assertFluidAt(new BlockPos(2, 1, 1), state -> state.isSource() && state.getType() == Fluids.WATER.getSource(), "Salt water should replace spring water and create source");
    }

    @IntegrationTest("fluid_mixing")
    public void testSaltWaterAndSpringWaterMixing(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(4, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        helper.runAfter(60, () -> helper.setBlockState(new BlockPos(1, 1, 3), sourceBlock(TFCFluids.SPRING_WATER)));
        helper.assertFluidAt(new BlockPos(1, 1, 1), TFCFluids.SPRING_WATER.getFlowing(), "Expected spring water to replace salt water");
        helper.assertFluidAt(new BlockPos(2, 1, 2), TFCFluids.SPRING_WATER.getFlowing(), "Expected spring water to replace salt water");
        helper.assertFluidAt(new BlockPos(3, 1, 3), TFCFluids.SPRING_WATER.getFlowing(), "Expected spring water to replace salt water");
    }

    @IntegrationTest("fluid_mixing")
    public void testSaltWaterAndVanillaWaterMixing(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(4, 1, 1), sourceBlock(TFCFluids.SALT_WATER));
        helper.runAfter(60, () -> helper.setBlockState(new BlockPos(1, 1, 3), waterSource()));
        helper.assertFluidAt(new BlockPos(1, 1, 1), Fluids.WATER.getFlowing(), "Expected vanilla water to replace salt water");
        helper.assertFluidAt(new BlockPos(2, 1, 2), Fluids.WATER.getFlowing(), "Expected vanilla water to replace salt water");
        helper.assertFluidAt(new BlockPos(3, 1, 3), Fluids.WATER.getFlowing(), "Expected vanilla water to replace salt water");
    }

    @IntegrationTest("fluid_mixing")
    public void testVanillaWaterAndSaltWaterMixing(IntegrationTestHelper helper)
    {
        helper.setBlockState(new BlockPos(4, 1, 1), waterSource());
        helper.runAfter(60, () -> helper.setBlockState(new BlockPos(1, 1, 3), sourceBlock(TFCFluids.SALT_WATER)));
        helper.assertFluidAt(new BlockPos(1, 1, 1), TFCFluids.SALT_WATER.getFlowing(), "Expected salt water to replace vanilla water");
        helper.assertFluidAt(new BlockPos(2, 1, 2), TFCFluids.SALT_WATER.getFlowing(), "Expected salt water to replace vanilla water");
        helper.assertFluidAt(new BlockPos(3, 1, 3), TFCFluids.SALT_WATER.getFlowing(), "Expected salt water to replace vanilla water");
    }

    private BlockState sourceBlock(TFCFluids.FluidPair<? extends FlowingFluid> fluidPair)
    {
        return fluidPair.getSource().getSource(false).createLegacyBlock();
    }

    private BlockState waterSource()
    {
        return Fluids.WATER.getSource(false).createLegacyBlock();
    }
}
