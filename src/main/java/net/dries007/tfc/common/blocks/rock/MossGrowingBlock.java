package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidHelpers;

public class MossGrowingBlock extends Block implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingBlock(Properties properties, Supplier<? extends Block> mossy)
    {
        super(properties);
        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(World worldIn, BlockPos pos, BlockState state, boolean needsWater)
    {
        if (!needsWater || FluidHelpers.isSame(worldIn.getFluidState(pos.above()), Fluids.WATER))
        {
            worldIn.setBlock(pos, mossy.get().defaultBlockState(), 3);
        }
    }
}
