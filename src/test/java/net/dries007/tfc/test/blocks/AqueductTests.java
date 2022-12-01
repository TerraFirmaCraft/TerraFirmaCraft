/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.blocks;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.blocks.TFCBlocks;

@GameTestHolder
public class AqueductTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(structure = "aqueduct/u_bend_empty", timeoutTicks = 200)
    public void testAddingWaterInUBend(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 1, 2, 3));
    }

    @MyTest(structure = "aqueduct/u_bend_water")
    public void testRemovingWaterInUBend(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 1, 2, 3));
    }

    @MyTest(structure = "aqueduct/u_bend_water")
    public void testRemovingAqueductInMiddleOfUBend(GameTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(3, 2, 2));
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 1, 2, 3);
            helper.assertBlockState(new BlockPos(3, 2, 2), b -> !b.getFluidState().isSource(), () -> "Breaking an aqueduct should not leave a source block.");
        });
    }

    @MyTest(structure = "aqueduct/cascade_empty")
    public void testAddingWaterInCascade(GameTestHelper helper)
    {
        helper.setBlock(1, 4, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 7, 2, 1));
    }

    @MyTest(structure = "aqueduct/cascade_water")
    public void testRemovingWaterInCascade(GameTestHelper helper)
    {
        helper.setBlock(1, 4, 1, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 7, 2, 1));
    }

    @MyTest(structure = "aqueduct/line_empty")
    public void testAddingWaterInLine(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 5, 2, 1));
    }

    @MyTest(structure = "aqueduct/line_water")
    public void testRemovingAqueductInMiddleOfLine(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 5, 2, 1));
    }

    @MyTest(structure = "aqueduct/line_water")
    public void testRemovingWaterInMiddleOfLine(GameTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(3, 2, 1));
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 5, 2, 1);
            helper.assertBlockState(new BlockPos(3, 2, 2), b -> !b.getFluidState().isSource(), () -> "Breaking an aqueduct should not leave a source block.");
        });
    }

    @MyTest(structure = "aqueduct/loops_empty", timeoutTicks = 400)
    public void testAddingWaterInLoops(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 4, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 1, 2, 2));
    }

    @MyTest(structure = "aqueduct/loops_water")
    public void testRemovingWaterInLoops(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 4, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 1, 2, 2));
    }

    @MyTest(structure = "aqueduct/line_empty")
    public void testAddingSaltWaterInLine(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, TFCBlocks.SALT_WATER.get());
        helper.succeedWhen(() -> helper.assertBlockPresent(TFCBlocks.SALT_WATER.get(), 5, 2, 1));
    }

    @MyTest(structure = "aqueduct/line_empty")
    public void testAddingSpringWaterInLine(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, TFCBlocks.SPRING_WATER.get());
        helper.succeedWhen(() -> helper.assertBlockPresent(TFCBlocks.SPRING_WATER.get(), 5, 2, 1));
    }

    @MyTest(structure = "aqueduct/corner_empty")
    public void testFlowingWaterDoesNotFillAqueducts(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 3, Blocks.WATER);
        helper.runAfterDelay(100, () -> {
            helper.assertBlockPresent(Blocks.AIR, 3, 2, 3);
            helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 3, 2, 3));
        });
    }

    @MyTest(structure = "aqueduct/move_with_piston")
    public void testMovingWithPistonDoesNotLeaveSourceBlocks(GameTestHelper helper)
    {
        helper.pullLever(3, 2, 4);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 1, 2, 1);
            helper.assertBlockPresent(Blocks.AIR, 3, 2, 1);
        });
    }

    @MyTest(structure = "aqueduct/pickup_with_bucket")
    public void testPickupWithBucketFromEnd(GameTestHelper helper)
    {
        helper.pullLever(6, 3, 0);
        helper.succeedWhen(() -> helper.assertBlockState(new BlockPos(6, 2, 1), state -> state.getFluidState().is(Fluids.WATER), () -> "Expected water"));
    }

    @MyTest(structure = "aqueduct/pickup_with_bucket")
    public void testPickupWithBucketFromMiddle(GameTestHelper helper)
    {
        helper.pullLever(3, 3, 0);
        helper.succeedWhen(() -> helper.assertBlockState(new BlockPos(3, 2, 1), state -> state.getFluidState().is(Fluids.WATER), () -> "Expected water"));
    }

    @MyTest(structure = "aqueduct/two_source")
    public void testAqueductCannotCreateNewSourceBlocks(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.WATER);
        helper.setBlock(7, 2, 1, Blocks.WATER);
        helper.runAfterDelay(80, () -> {
            helper.assertBlockState(new BlockPos(4, 2, 1), state -> state.getFluidState().is(Fluids.FLOWING_WATER), () -> "Expected a non-source block of water");
            helper.succeed();
        });
    }

    @MyTest(structure = "aqueduct/place_with_bucket")
    public void testPlaceWithBucket(GameTestHelper helper)
    {
        helper.pullLever(1, 3, 0);
        helper.runAfterDelay(80, () -> {
            helper.assertBlockPresent(Blocks.AIR, 4, 2, 1);
            helper.succeed();
        });
    }
}
