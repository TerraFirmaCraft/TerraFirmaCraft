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
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;

@GameTestHolder
public class SluiceTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(structure = "sluice/empty")
    public void testSluicePlacesWaterBelow(GameTestHelper helper)
    {
        helper.setBlock(1, 3, 1, Blocks.WATER);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.WATER, 11, 1, 1));
    }

    @MyTest(structure = "sluice/water")
    public void testSluiceRemovesWaterWhenBottomBroken(GameTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(10, 2, 1));
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 11, 1, 1));
    }

    @MyTest(structure = "sluice/water")
    public void testSluiceRemovesWaterWhenTopBroken(GameTestHelper helper)
    {
        helper.destroyBlock(new BlockPos(9, 2, 1));
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 11, 1, 1));
    }

    @MyTest(structure = "sluice/water")
    public void testSluiceRemovesWaterWhenWaterStopsFlowing(GameTestHelper helper)
    {
        helper.setBlock(1, 3, 1, Blocks.POLISHED_ANDESITE);
        helper.succeedWhen(() -> helper.assertBlockPresent(Blocks.AIR, 11, 1, 1));
    }
}
