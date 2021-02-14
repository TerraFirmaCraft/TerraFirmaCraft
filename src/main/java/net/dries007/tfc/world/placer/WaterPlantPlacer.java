/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class WaterPlantPlacer extends BlockPlacer
{
    public static final Codec<WaterPlantPlacer> CODEC = Codec.unit(new WaterPlantPlacer());

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
    {
        if (state.getBlock() instanceof IFluidLoggable)
        {
            IFluidLoggable block = (IFluidLoggable) state.getBlock();
            BlockState setState = block.getStateWithFluid(state, worldIn.getFluidState(pos).getType());
            if (setState.get(block.getFluidProperty()).getFluid() == Fluids.EMPTY)
                return;
            worldIn.setBlockState(pos, setState.with(TFCBlockStateProperties.AGE_3, random.nextInt(4)), 2);
        }
    }

    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.WATER_PLANT.get();
    }
}

