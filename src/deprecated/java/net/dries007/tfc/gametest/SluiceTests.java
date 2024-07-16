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

import static net.dries007.tfc.GameTestAssertions.*;

@GameTestHolder
public class SluiceTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return testGenerator();
    }

    @MyTest(structure = "sluice/empty")
    public void testSluicePlacesWaterBelow()
    {
        at(1, 3, 1).setBlock(Blocks.WATER);
        succeedWhen(() -> at(11, 1, 1).is(Blocks.WATER));
    }

    @MyTest(structure = "sluice/water")
    public void testSluiceRemovesWaterWhenBottomBroken()
    {
        at(10, 2, 1).destroyBlock();
        succeedWhen(() -> at(11, 1, 1).isAir());
    }

    @MyTest(structure = "sluice/water")
    public void testSluiceRemovesWaterWhenTopBroken()
    {
        at(9, 2, 1).destroyBlock();
        succeedWhen(() -> at(11, 1, 1).isAir());
    }

    @MyTest(structure = "sluice/water")
    public void testSluiceRemovesWaterWhenWaterStopsFlowing()
    {
        at(1, 3, 1).setBlock(Blocks.POLISHED_ANDESITE);
        succeedWhen(() -> at(11, 1, 1).isAir());
    }
}
