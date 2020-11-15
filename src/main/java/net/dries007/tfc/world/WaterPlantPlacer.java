package net.dries007.tfc.world;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class WaterPlantPlacer extends BlockPlacer
{
    public static final Codec<WaterPlantPlacer> CODEC = Codec.unit(WaterPlantPlacer::new);

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
    {
        worldIn.setBlock(pos, state.setValue(TFCBlockStateProperties.WATER, TFCBlockStateProperties.WATER.keyFor(worldIn.getFluidState(pos).getType())).setValue(TFCBlockStateProperties.AGE_3, random.nextInt(4)), 2);
    }

    protected BlockPlacerType<?> type() {
        return TFCBlockPlacerTypes.WATER_PLANT_PLACER.get();
    }
}
