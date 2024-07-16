/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.gametest;

import java.util.Collection;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.MyTest;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;

import static net.dries007.tfc.GameTestAssertions.*;

@GameTestHolder
public class LandslideTests
{
    @GameTestGenerator
    public Collection<TestFunction> generator()
    {
        return testGenerator();
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksTorch()
    {
        expectBreaksBlock(Blocks.TORCH, Blocks.TORCH);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksSlab()
    {
        expectBreaksBlock(Blocks.ANDESITE_SLAB, Blocks.ANDESITE_SLAB);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksFarmland()
    {
        expectBreaksBlock(Blocks.FARMLAND, Blocks.DIRT);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtBreaksPath()
    {
        expectBreaksBlock(Blocks.DIRT_PATH, Blocks.DIRT);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtDoesNotBreakCharcoal()
    {
        expectPopsOff(TFCBlocks.CHARCOAL_PILE.get());
    }

    @MyTest(structure = "5x5_platform")
    public void testCobbleBreaksCharcoal()
    {
        expectBreaksBlock(TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.COBBLE).get(), TFCBlocks.CHARCOAL_PILE.get(), Items.CHARCOAL);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtDoesNotBreakSoulSand()
    {
        expectStaysOnTop(Blocks.SOUL_SAND);
    }

    @MyTest(structure = "5x5_platform")
    public void testDirtDoesNotBreakMud()
    {
        expectStaysOnTop(TFCBlocks.SOIL.get(SoilBlockType.MUD).get(SoilBlockType.Variant.LOAM).get());
    }


    private void expectBreaksBlock(Block blockToFallOn, ItemLike expectedItem)
    {
        run(Blocks.DIRT, blockToFallOn, Blocks.DIRT, null, expectedItem);
    }

    private void expectBreaksBlock(Block blockToFall, Block blockToFallOn, ItemLike expectedItem)
    {
        run(blockToFall, blockToFallOn, blockToFall, null, expectedItem);
    }

    private void expectPopsOff(Block blockToFallOn)
    {
        run(Blocks.DIRT, blockToFallOn, blockToFallOn, null, Blocks.DIRT);
    }

    private void expectStaysOnTop(Block blockToFallOn)
    {
        run(Blocks.DIRT, blockToFallOn, blockToFallOn, Blocks.DIRT, null);
    }

    private void run(Block blockToDrop, Block blockToFallOn, Block expectBottomBlock, @Nullable Block expectBlockAbove, @Nullable ItemLike expectItem)
    {
        at(1, 2, 2).setBlock(blockToFallOn); // Landing Area
        at(2, 2, 1).setBlock(blockToFallOn);
        at(3, 2, 2).setBlock(blockToFallOn);
        at(2, 2, 3).setBlock(blockToFallOn);
        at(2, 2, 2).setBlock(blockToFallOn);
        at(2, 5, 2).setBlock(blockToDrop); // Falling Block
        at(2, 6, 2).setBlock(Blocks.POLISHED_ANDESITE_SLAB); // Triggers block update, causing the falling block to fall
        succeedWhen(() -> {
            at(2, 2, 2).is(expectBottomBlock);
            if (expectBlockAbove != null)
            {
                at(2, 3, 2).is(expectBlockAbove);
            }
            if (expectItem != null)
            {
                at(2, 2, 2).itemEntityIsPresent(expectItem.asItem(), 2);
            }
        });
    }
}
