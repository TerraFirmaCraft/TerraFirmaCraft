/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.KelpTreeFlowerBlock;

public class KelpTreePlacer extends BlockPlacer
{
    public static final Codec<KelpTreePlacer> CODEC = Codec.unit(new KelpTreePlacer());

    @Override
    public void place(LevelAccessor worldIn, BlockPos pos, BlockState state, Random random)
    {
        final FluidState fluidAt = worldIn.getFluidState(pos);
        final KelpTreeFlowerBlock flower = (KelpTreeFlowerBlock) state.getBlock();
        flower.generatePlant(worldIn, pos, random, 8, fluidAt.getType());
    }

    @Override
    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.KELP_TREE.get();
    }
}
