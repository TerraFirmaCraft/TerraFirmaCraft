package net.dries007.tfc.world.placer;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.blockplacer.BlockPlacer;
import net.minecraft.world.gen.blockplacer.BlockPlacerType;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.KelpTreeFlowerBlock;

public class KelpTreePlacer extends BlockPlacer
{
    public static final Codec<KelpTreePlacer> CODEC = Codec.unit(new KelpTreePlacer());

    @Override
    public void place(IWorld worldIn, BlockPos pos, BlockState state, Random random)
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
