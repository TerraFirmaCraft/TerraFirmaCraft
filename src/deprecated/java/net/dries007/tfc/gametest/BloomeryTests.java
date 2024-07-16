/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.gametest;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.GameTestAssertions.*;

@GameTestHolder
public class BloomeryTests
{
    private final BlockPos bloomeryPos = new BlockPos(1, 2, 2);
    private final BlockPos bloomPos = new BlockPos(2, 2, 2);
    private final BlockPos inputPos = new BlockPos(2, 4, 2);

    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return testGenerator();
    }

    @MyTest(structure = "bloomery", timeoutTicks = 150)
    public void testBloomeryRunsNotEnoughCharcoal()
    {
        runBloomery(1, 7, 0); // 1 Charcoal + 105 mB
    }

    @MyTest(structure = "bloomery", timeoutTicks = 150)
    public void testBloomeryRunsNotEnoughOre()
    {
        runBloomery(3, 6, 0); // 3 Charcoal + 90 mB
    }

    @MyTest(structure = "bloomery", timeoutTicks = 150)
    public void testBloomeryRunsTooMuchOre()
    {
        runBloomery(3, 20, 1); // 3 Charcoal + 300 mB = 1 Bloom
    }

    @MyTest(structure = "bloomery", timeoutTicks = 150)
    public void testBloomeryRunsTooMuchCharcoal()
    {
        runBloomery(16, 8, 1); // 16 Charcoal + 120 mB = 1 Bloom
    }

    @MyTest(structure = "bloomery", timeoutTicks = 150)
    public void testBloomeryRunsExactRightAmount()
    {
        runBloomery(6, 20, 3); // 6 Charcoal + 300 mB = 3 Blooms
    }

    @MyTest(structure = "bloomery")
    public void testBloomeryDropsContentsWhenStructureBroken()
    {
        addItems(0, 48);
        runAfterDelay(50, () -> at(3, 2, 2).destroyBlock());
        succeedWhen(() -> at(bloomeryPos).itemEntityIsPresent(oreItem(), 48, 1.0));
    }

    @MyTest(structure = "bloomery")
    public void testBloomeryDropsContentsWhenBloomeryBroken()
    {
        addItems(0, 48);
        runAfterDelay(50, () -> at(bloomeryPos).destroyBlock());
        succeedWhen(() -> at(bloomeryPos).itemEntityIsPresent(oreItem(), 48, 1.0));
    }

    @MyTest(structure = "bloomery")
    public void testBloomeryDropsSomeContentWhenChimneyLost()
    {
        addItems(8, 32);
        runAfterDelay(50, () -> at(1, 5, 2).destroyBlock());
        succeedWhen(() -> at(bloomeryPos).itemEntityIsPresent(Items.CHARCOAL, 8, 1.0));
    }

    @MyTest(structure = "bloomery")
    public void testBloomeryDoesNotConsumeOverCapacity()
    {
        addItems(0, 48);
        runAfterDelay(50, () -> addItems(0, 16));
        runAfterDelay(100, () -> {
            at(2, 2, 2).itemEntityIsPresent(oreItem(), 64, 2.0);
            succeed();
        });
    }

    private void runBloomery(int charcoal, int poorOre, int bloom)
    {
        addItems(charcoal, poorOre);
        light();
        thenFinish(bloom);
    }

    private void addItems(int charcoal, int poorOre)
    {
        at(inputPos)
            .spawnItem(oreItem(), poorOre)
            .spawnItem(Items.CHARCOAL, charcoal);
    }

    private void light()
    {
        runAfterDelay(50, () -> {
            try (final CalendarTransaction tr = Calendars.SERVER.transaction())
            {
                tr.add(-14950);
                at(bloomeryPos)
                    .getBlockEntity(TFCBlockEntities.BLOOMERY.get())
                    .light(at(bloomeryPos).getBlockState());
            }
        });
    }

    private void thenFinish(int bloom)
    {
        succeedWhen(() -> {
            at(bloomeryPos)
                .is(TFCBlocks.BLOOMERY.get())
                .is(BloomeryBlock.LIT, false);

            if (bloom > 0)
            {
                at(bloomPos)
                    .is(TFCBlocks.BLOOM.get())
                    .is(BloomBlock.LAYERS, bloom)
                    .is(TFCBlockEntities.BLOOM.get(), entity -> entity.getCount() == bloom, "Expected count to be " + bloom);
            }
            else
            {
                at(bloomPos).isAir();
            }
        });
    }

    private Item oreItem()
    {
        return TFCItems.GRADED_ORES.get(Ore.LIMONITE).get(Ore.Grade.POOR).get();
    }
}
