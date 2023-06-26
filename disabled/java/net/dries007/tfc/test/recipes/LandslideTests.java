/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.recipes;

import java.util.Collection;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.TestAssertions;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import org.jetbrains.annotations.Nullable;

@GameTestHolder
public class LandslideTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return TestAssertions.testGenerator();
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksTorch(GameTestHelper helper)
    {
        expectBreaksBlock(helper, Blocks.TORCH, Blocks.TORCH);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksSlab(GameTestHelper helper)
    {
        expectBreaksBlock(helper, Blocks.ANDESITE_SLAB, Blocks.ANDESITE_SLAB);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksFarmland(GameTestHelper helper)
    {
        expectBreaksBlock(helper, Blocks.FARMLAND, Blocks.DIRT);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksPath(GameTestHelper helper)
    {
        expectBreaksBlock(helper, Blocks.DIRT_PATH, Blocks.DIRT);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtDoesNotBreakCharcoal(GameTestHelper helper)
    {
        expectPopsOff(helper, TFCBlocks.CHARCOAL_PILE.get());
    }

    @MyTest(structure = "5x5_platform")
    public void testCobbleBreaksCharcoal(GameTestHelper helper)
    {
        expectBreaksBlock(helper, TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.COBBLE).get(), TFCBlocks.CHARCOAL_PILE.get(), Items.CHARCOAL);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtDoesNotBreakSoulSand(GameTestHelper helper)
    {
        expectStaysOnTop(helper, Blocks.SOUL_SAND);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtDoesNotBreakMud(GameTestHelper helper)
    {
        expectStaysOnTop(helper, TFCBlocks.SOIL.get(SoilBlockType.MUD).get(SoilBlockType.Variant.LOAM).get());
    }


    private void expectBreaksBlock(GameTestHelper helper, Block blockToFallOn, ItemLike expectedItem)
    {
        run(helper, Blocks.DIRT, blockToFallOn, Blocks.DIRT, null, expectedItem);
    }

    private void expectBreaksBlock(GameTestHelper helper, Block blockToFall, Block blockToFallOn, ItemLike expectedItem)
    {
        run(helper, blockToFall, blockToFallOn, blockToFall, null, expectedItem);
    }

    private void expectPopsOff(GameTestHelper helper, Block blockToFallOn)
    {
        run(helper, Blocks.DIRT, blockToFallOn, blockToFallOn, null, Blocks.DIRT);
    }

    private void expectStaysOnTop(GameTestHelper helper, Block blockToFallOn)
    {
        run(helper, Blocks.DIRT, blockToFallOn, blockToFallOn, Blocks.DIRT, null);
    }

    private void run(GameTestHelper helper, Block blockToDrop, Block blockToFallOn, Block expectBottomBlock, @Nullable Block expectBlockAbove, @Nullable ItemLike expectItem)
    {
        helper.setBlock(1, 2, 2, blockToFallOn); // Landing Area
        helper.setBlock(2, 2, 1, blockToFallOn);
        helper.setBlock(3, 2, 2, blockToFallOn);
        helper.setBlock(2, 2, 3, blockToFallOn);
        helper.setBlock(2, 2, 2, blockToFallOn);
        helper.setBlock(2, 5, 2, blockToDrop); // Falling Block
        helper.setBlock(2, 6, 2, Blocks.POLISHED_ANDESITE_SLAB); // Triggers block update, causing the falling block to fall
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(expectBottomBlock, 2, 2, 2);
            if (expectBlockAbove != null)
            {
                helper.assertBlockPresent(expectBlockAbove, 2, 3, 2);
            }
            if (expectItem != null)
            {
                helper.assertItemEntityPresent(expectItem.asItem(), new BlockPos(2, 2, 2), 2);
            }
        });
    }
}
