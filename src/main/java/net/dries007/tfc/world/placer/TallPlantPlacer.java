package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.TFCTallGrassBlock;

public class TallPlantPlacer extends BlockPlacer
{
    public static final Codec<TallPlantPlacer> CODEC = Codec.unit(new TallPlantPlacer());

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
    {
        ((TFCTallGrassBlock)state.getBlock()).placeTwoHalves(worldIn, pos, 2, random);
    }

    protected BlockPlacerType<?> type() {
        return TFCBlockPlacers.TALL_PLANT.get();
    }
}
