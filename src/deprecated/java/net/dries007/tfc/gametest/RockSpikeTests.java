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
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;

import static net.dries007.tfc.GameTestAssertions.*;

@GameTestHolder
public class RockSpikeTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return testGenerator();
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakSupportingBelowOfBottomSpike()
    {
        run(0, ".....123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakBaseOfBottomSpike()
    {
        run(1, "#....123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakMiddleOfBottomSpike()
    {
        run(2, "#....123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakTipOfBottomSpike()
    {
        run(3, "#32..123#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakTipOfTopSpike()
    {
        run(5, "#321..23#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakMiddleOfTopSpike()
    {
        run(6, "#......3#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakBaseOfTopSpike()
    {
        run(7, "#.......#");
    }

    @MyTest(structure = "rock_spike/column")
    public void testBreakSupportingAboveOfTopSpike()
    {
        run(8, "#........");
    }

    /**
     * @param expectedBlockColumn Expected blocks. '.' = air, '#' = andesite, anything else = spike. Bottom = left = index 0, Top = right.
     */
    private void run(int indexToBreak, String expectedBlockColumn)
    {
        at(0, 1 + indexToBreak, 0).destroyBlock();
        succeedWhen(() -> {
            for (int i = 0; i < expectedBlockColumn.length(); i++)
            {
                at(0, 1 + i, 0).is(expectedBlockColumn.charAt(i) == '.'
                    ? Blocks.AIR
                    : expectedBlockColumn.charAt(i) == '#'
                        ? Blocks.POLISHED_ANDESITE
                        : TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.SPIKE).get());
            }
        });
    }
}
