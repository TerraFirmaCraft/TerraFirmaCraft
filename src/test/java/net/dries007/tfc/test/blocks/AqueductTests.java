/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import net.dries007.tfc.TerraFirmaCraft;

@PrefixGameTestTemplate(false)
@GameTestHolder(TerraFirmaCraft.MOD_ID)
public class AqueductTests
{
    @GameTest(template = "aqueduct/u_bend_empty", timeoutTicks = 200)
    public void testAddingWaterInUBend(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 1, 2, 3));
    }

    @GameTest(template = "aqueduct/u_bend_water")
    public void testRemovingWaterInUBend(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 1, 2, 3));
    }

    @GameTest(template = "aqueduct/u_bend_water")
    public void testRemovingAqueductInMiddleOfUBend(GameTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(3, 2, 2));
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 1, 2, 3);
            helper.assertBlockState(new BlockPos(3, 2, 2), b -> !b.getFluidState().isSource(), () -> "Breaking an aqueduct should not leave a source block.");
        });
    }

    @GameTest(template = "aqueduct/cascade_empty")
    public void testAddingWaterInCascade(GameTestHelper helper)
    {
        helper.setBlock(1, 4, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 7, 2, 1));
    }

    @GameTest(template = "aqueduct/cascade_water")
    public void testRemovingWaterInCascade(GameTestHelper helper)
    {
        helper.setBlock(1, 4, 1, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 7, 2, 1));
    }

    @GameTest(template = "aqueduct/line_empty")
    public void testAddingWaterInLine(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 5, 2, 1));
    }

    @GameTest(template = "aqueduct/line_water")
    public void testRemovingAqueductInMiddleOfLine(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 1, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 5, 2, 1));
    }

    @GameTest(template = "aqueduct/line_water")
    public void testRemovingWaterInMiddleOfLine(GameTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(3, 2, 1));
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 5, 2, 1);
            helper.assertBlockState(new BlockPos(3, 2, 2), b -> !b.getFluidState().isSource(), () -> "Breaking an aqueduct should not leave a source block.");
        });
    }

    @GameTest(template = "aqueduct/loops_empty", timeoutTicks = 400)
    public void testAddingWaterInLoops(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 4, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 1, 2, 2));
    }

    @GameTest(template = "aqueduct/loops_water")
    public void testRemovingWaterInLoops(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 4, Blocks.POLISHED_ANDESITE); // Clear the water source
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 1, 2, 2));
    }

    @GameTest(template = "aqueduct/corner_empty")
    public void testFlowingWaterDoesNotFillAqueducts(GameTestHelper helper)
    {
        helper.setBlock(1, 2, 3, Blocks.WATER);
        helper.runAfterDelay(100, () -> {
            helper.assertBlockPresent(Blocks.AIR, 3, 2, 3);
            helper.succeed();
        });
    }

    @GameTest(template = "aqueduct/move_with_piston")
    public void testMovingWithPistonDoesNotLeaveSourceBlocks(GameTestHelper helper)
    {
        helper.pullLever(3, 2, 4);
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 1, 2, 1);
            helper.assertBlockPresent(Blocks.AIR, 3, 2, 1);
        });
    }
}
