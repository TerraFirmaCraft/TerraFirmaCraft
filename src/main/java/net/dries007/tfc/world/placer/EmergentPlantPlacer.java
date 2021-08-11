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
import net.dries007.tfc.common.blocks.plant.TallWaterPlantBlock;

public class EmergentPlantPlacer extends BlockPlacer
{
    public static final Codec<EmergentPlantPlacer> CODEC = Codec.unit(new EmergentPlantPlacer());

    @Override
    public void place(LevelAccessor worldIn, BlockPos pos, BlockState state, Random random)
    {
        ((TallWaterPlantBlock) state.getBlock()).placeTwoHalves(worldIn, pos, 2, random);
    }

    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.EMERGENT_PLANT.get();
    }
}
