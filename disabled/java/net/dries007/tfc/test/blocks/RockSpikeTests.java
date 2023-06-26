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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;

@GameTestHolder
public class RockSpikeTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakSupportingBelowOfBottomSpike(GameTestHelper helper)
    {
        run(helper, 0, ".....123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakBaseOfBottomSpike(GameTestHelper helper)
    {
        run(helper, 1, "#....123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakMiddleOfBottomSpike(GameTestHelper helper)
    {
        run(helper, 2, "#....123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakTipOfBottomSpike(GameTestHelper helper)
    {
        run(helper, 3, "#32..123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakTipOfTopSpike(GameTestHelper helper)
    {
        run(helper, 5, "#321..23#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakMiddleOfTopSpike(GameTestHelper helper)
    {
        run(helper, 6, "#......3#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakBaseOfTopSpike(GameTestHelper helper)
    {
        run(helper, 7, "#.......#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakSupportingAboveOfTopSpike(GameTestHelper helper)
    {
        run(helper, 8, "#........");
    }

    /**
     * @param expectedBlockColumn Expected blocks. '.' = air, '#' = andesite, anything else = spike. Bottom = left = index 0, Top = right.
     */
    private void run(GameTestHelper helper, int indexToBreak, String expectedBlockColumn)
    {
        helper.destroyBlock(new BlockPos(0, 1 + indexToBreak, 0));
        helper.succeedWhen(() -> {
            for (int i = 0; i < expectedBlockColumn.length(); i++)
            {
                final Block block = expectedBlockColumn.charAt(i) == '.' ? Blocks.AIR :
                    expectedBlockColumn.charAt(i) == '#' ? Blocks.POLISHED_ANDESITE :
                    TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.SPIKE).get();
                helper.assertBlockPresent(block, 0, 1 + i, 0);
            }
        });
    }
}
