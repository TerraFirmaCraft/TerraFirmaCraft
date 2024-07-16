/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.gametest;

import java.util.Collection;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.common.blocks.TFCBlocks;

import static net.dries007.tfc.GameTestAssertions.*;


@GameTestHolder
public class AqueductTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return testGenerator();
    }

    @MyTest(structure = "aqueduct/u_bend_empty", timeoutTicks = 200)
    public void testAddingWaterInUBend()
    {
        at(1, 2, 1).setBlock(Blocks.WATER);
        succeedWhen(() -> at(1, 2, 3).is(Blocks.WATER));
    }

    @MyTest(structure = "aqueduct/u_bend_water")
    public void testRemovingWaterInUBend()
    {
        at(1, 2, 1).setBlock(Blocks.POLISHED_ANDESITE); // Clear the water source
        succeedWhen(() -> at(1, 2, 3).isAir());
    }

    @MyTest(structure = "aqueduct/u_bend_water")
    public void testRemovingAqueductInMiddleOfUBend()
    {
        at(3, 2, 2).destroyBlock();
        succeedWhen(() -> {
            at(1, 2, 3).isAir();
            at(3, 2, 2).is(state -> !state.getFluidState().isSource(), "Breaking an aqueduct should not leave a source block.");
        });
    }

    @MyTest(structure = "aqueduct/cascade_empty")
    public void testAddingWaterInCascade()
    {
        at(1, 4, 1).setBlock(Blocks.WATER);
        succeedWhen(() -> at(7, 2, 1).is(Blocks.WATER));
    }

    @MyTest(structure = "aqueduct/cascade_water")
    public void testRemovingWaterInCascade()
    {
        at(1, 4, 1).setBlock(Blocks.POLISHED_ANDESITE); // Clear the water source
        succeedWhen(() -> at(7, 2, 1).isAir());
    }

    @MyTest(structure = "aqueduct/line_empty")
    public void testAddingWaterInLine()
    {
        at(1, 2, 1).setBlock(Blocks.WATER);
        succeedWhen(() -> at(5, 2, 1).is(Blocks.WATER));
    }

    @MyTest(structure = "aqueduct/line_water")
    public void testRemovingAqueductInMiddleOfLine()
    {
        at(1, 2, 1).setBlock(Blocks.POLISHED_ANDESITE); // Clear the water source
        succeedWhen(() -> at(5, 2, 1).isAir());
    }

    @MyTest(structure = "aqueduct/line_water")
    public void testRemovingWaterInMiddleOfLine()
    {
        at(3, 2, 1).destroyBlock();
        succeedWhen(() -> {
            at(5, 2, 1).isAir();
            at(3, 2, 2).is(state -> !state.getFluidState().isSource(), "Breaking an aqueduct should not leave a source block.");
        });
    }

    @MyTest(structure = "aqueduct/loops_empty", timeoutTicks = 400)
    public void testAddingWaterInLoops()
    {
        at(1, 2, 4).setBlock(Blocks.WATER);
        succeedWhen(() -> at(1, 2, 2).is(Blocks.WATER));
    }

    @MyTest(structure = "aqueduct/loops_water")
    public void testRemovingWaterInLoops()
    {
        at(1, 2, 4).setBlock(Blocks.POLISHED_ANDESITE); // Clear the water source
        succeedWhen(() -> at(1, 2, 2).isAir());
    }

    @MyTest(structure = "aqueduct/line_empty")
    public void testAddingSaltWaterInLine()
    {
        at(1, 2, 1).setBlock(TFCBlocks.SALT_WATER.get());
        succeedWhen(() -> at(5, 2, 1).is(TFCBlocks.SALT_WATER.get()));
    }

    @MyTest(structure = "aqueduct/line_empty")
    public void testAddingSpringWaterInLine()
    {
        at(1, 2, 1).setBlock(TFCBlocks.SPRING_WATER.get());
        succeedWhen(() -> at(5, 2, 1).is(TFCBlocks.SPRING_WATER.get()));
    }

    @MyTest(structure = "aqueduct/corner_empty")
    public void testFlowingWaterDoesNotFillAqueducts()
    {
        at(1, 2, 3).setBlock(Blocks.WATER);
        runAfterDelay(100, () -> {
            at(3, 2, 3).isAir();
            succeed();
        });
    }

    @MyTest(structure = "aqueduct/move_with_piston")
    public void testMovingWithPistonDoesNotLeaveSourceBlocks()
    {
        at(3, 2, 4).pullLever();
        succeedWhen(() -> {
            at(1, 2, 1).isAir();
            at(3, 2, 1).isAir();
        });
    }

    @MyTest(structure = "aqueduct/pickup_with_bucket")
    public void testPickupWithBucketFromEnd()
    {
        at(6, 3, 0).pullLever();
        succeedWhen(() -> at(6, 2, 1).is(state -> state.getFluidState().is(Fluids.WATER), "Expected water"));
    }

    @MyTest(structure = "aqueduct/pickup_with_bucket")
    public void testPickupWithBucketFromMiddle()
    {
        at(3, 3, 0).pullLever();
        succeedWhen(() -> at(3, 2, 1).is(state -> state.getFluidState().is(Fluids.WATER), "Expected water"));
    }

    @MyTest(structure = "aqueduct/two_source")
    public void testAqueductCannotCreateNewSourceBlocks()
    {
        at(1, 2, 1).setBlock(Blocks.WATER);
        at(7, 2, 1).setBlock(Blocks.WATER);
        runAfterDelay(80, () -> {
            at(4, 2, 1).is(state -> state.getFluidState().is(Fluids.FLOWING_WATER), "Expected a non-source block of water");
            succeed();
        });
    }

    @MyTest(structure = "aqueduct/place_with_bucket")
    public void testPlaceWithBucket()
    {
        at(1, 3, 0).pullLever();
        runAfterDelay(80, () -> {
            at(4, 2, 1).isAir();
            succeed();
        });
    }
}
