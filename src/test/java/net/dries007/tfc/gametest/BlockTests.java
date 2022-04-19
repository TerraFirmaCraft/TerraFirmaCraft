/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.ITallPlant;
import net.dries007.tfc.common.blocks.plant.Plant;

@PrefixGameTestTemplate(false)
@GameTestHolder(TerraFirmaCraft.MOD_ID)
public class BlockTests
{
    @GameTest(template = "3x3_platform")
    public static void setBlock(GameTestHelper helper)
    {
        Block block = TFCBlocks.PLAIN_ALABASTER.get();
        helper.setBlock(1, 2, 1, block);
        helper.succeedIf(() -> helper.assertBlock(new BlockPos(1, 2, 1), b -> b == block, "Expected plain alabaster"));
    }

    @GameTest(template = "3x3_platform")
    public static void doublePlantBreak(GameTestHelper helper)
    {
        BlockState rose = TFCBlocks.PLANTS.get(Plant.ROSE).get().defaultBlockState();
        helper.setBlock(1, 1, 1, TFCBlocks.PEAT_GRASS.get());
        helper.setBlock(1, 2, 1, rose.setValue(TFCBlockStateProperties.TALL_PLANT_PART, ITallPlant.Part.LOWER));
        helper.setBlock(1, 3, 1, rose.setValue(TFCBlockStateProperties.TALL_PLANT_PART, ITallPlant.Part.UPPER));

        helper.destroyBlock(new BlockPos(1, 2, 1));
        helper.succeedIf(() -> helper.assertBlockState(new BlockPos(1, 3, 1), BlockState::isAir, () -> "Expected air"));
    }
}
