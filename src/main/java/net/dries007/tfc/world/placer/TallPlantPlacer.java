/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TFCTallGrassBlock;

public class TallPlantPlacer extends BlockPlacer
{
    public static final Codec<TallPlantPlacer> CODEC = Codec.unit(new TallPlantPlacer());

    @Override
    public void place(LevelAccessor worldIn, BlockPos pos, BlockState state, Random random)
    {
        ((TFCTallGrassBlock) state.getBlock()).placeTwoHalves(worldIn, pos, 2, random);
    }

    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.TALL_PLANT.get();
    }
}
