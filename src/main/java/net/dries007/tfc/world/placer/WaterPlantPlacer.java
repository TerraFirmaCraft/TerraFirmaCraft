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
            if (setState.getValue(block.getFluidProperty()).getFluid() == Fluids.EMPTY)
                return;
            worldIn.setBlock(pos, setState.setValue(TFCBlockStateProperties.AGE_3, random.nextInt(4)), 2);
        }
    }

    protected BlockPlacerType<?> type()
    {
        return TFCBlockPlacers.WATER_PLANT.get();
    }
}

