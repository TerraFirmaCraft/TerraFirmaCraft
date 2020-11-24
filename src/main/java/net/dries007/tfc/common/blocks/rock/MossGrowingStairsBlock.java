package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class MossGrowingStairsBlock extends StairsBlock implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingStairsBlock(Supplier<BlockState> state, Properties properties, Supplier<? extends Block> mossy)
    {
        super(state, properties);

        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(World worldIn, BlockPos pos, BlockState state)
    {
        if (FluidHelpers.isSame(worldIn.getFluidState(pos), Fluids.WATER))
        {
            worldIn.setBlockAndUpdate(pos, Helpers.copyProperties(mossy.get().defaultBlockState(), state));
        }
    }
}
