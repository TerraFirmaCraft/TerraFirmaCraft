/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.gametest.GameTestHolder;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;

@GameTestHolder(TerraFirmaCraft.MOD_ID)
public class LandslideTests
{
    @GameTest(template = "fall_on_torch")
    public static void testDirtBreaksTorch(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(dirt(), 2, 2, 1);
            helper.assertItemEntityPresent(Items.TORCH, new BlockPos(2, 3, 2), 2);
        });
    }

    @GameTest(template = "fall_past_torch")
    public static void testDirtBreaksAdjacentTorch(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(dirt(), 2, 2, 1);
            helper.assertItemEntityPresent(Items.TORCH, new BlockPos(2, 3, 2), 2);
        });
    }

    @GameTest(template = "fall_on_slab")
    public static void testDirtBreaksSlab(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(dirt(), 2, 2, 1);
            helper.assertItemEntityPresent(slab().asItem(), new BlockPos(2, 3, 2), 2);
        });
    }

    @GameTest(template = "fall_past_slab")
    public static void testDirtBreaksAdjacentSlab(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(dirt(), 2, 2, 1);
            helper.assertItemEntityPresent(slab().asItem(), new BlockPos(2, 3, 2), 2);
        });
    }

    @GameTest(template = "fall_past_stair")
    public static void testDirtBreaksAdjacentStair(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(dirt(), 2, 2, 1);
            helper.assertItemEntityPresent(stair().asItem(), new BlockPos(2, 3, 2), 2);
        });
    }

    @GameTest(template = "fall_stopped_by_stair")
    public static void testDirtStoppedByAdjacentStair(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(dirt(), 2, 3, 2);
            helper.assertBlockPresent(stair(), 2, 3, 1);
        });
    }

    @GameTest(template = "fall_on_charcoal")
    public static void testDirtBreaksFallingOnCharcoal(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, dirt());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(TFCBlocks.CHARCOAL_PILE.get(), 2, 2, 1);
            helper.assertItemEntityPresent(dirt().asItem(), new BlockPos(2, 3, 2), 2);
        });
    }

    @GameTest(template = "fall_on_charcoal")
    public static void testCobbleDestroysCharcoal(GameTestHelper helper)
    {
        helper.setBlock(2, 3, 2, cobble());
        helper.setBlock(2, 3, 3, Blocks.TORCH); // Triggers neighbor update for falling block
        helper.succeedWhen(() -> {
            helper.assertBlockPresent(Blocks.AIR, 2, 3, 2);
            helper.assertBlockPresent(cobble(), 2, 2, 1);
            helper.assertItemEntityPresent(Items.CHARCOAL.asItem(), new BlockPos(2, 3, 2), 2);
        });
    }

    private static Block dirt()
    {
        return TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(SoilBlockType.Variant.LOAM).get();
    }

    private static Block cobble()
    {
        return TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.COBBLE).get();
    }

    private static Block slab()
    {
        return TFCBlocks.ROCK_DECORATIONS.get(Rock.GRANITE).get(Rock.BlockType.BRICKS).slab().get();
    }

    private static Block stair()
    {
        return TFCBlocks.ROCK_DECORATIONS.get(Rock.GRANITE).get(Rock.BlockType.BRICKS).stair().get();
    }
}
