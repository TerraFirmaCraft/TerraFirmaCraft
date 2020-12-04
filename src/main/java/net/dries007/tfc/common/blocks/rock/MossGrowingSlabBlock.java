package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class MossGrowingSlabBlock extends SlabBlock implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingSlabBlock(Properties properties, Supplier<? extends Block> mossy)
    {
        super(properties);

        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(World worldIn, BlockPos pos, BlockState state, boolean needsWater)
    {
        if (state.getValue(TYPE) == SlabType.DOUBLE)
        {
            // Double slabs convert when the block above is fluid
            if (!needsWater || FluidHelpers.isSame(worldIn.getFluidState(pos.above()), Fluids.WATER))
            {
                worldIn.setBlockAndUpdate(pos, Helpers.copyProperties(mossy.get().defaultBlockState(), state));
            }
        }
        else
        {
            // Single slabs convert only when they are fluid logged
            if (!needsWater || FluidHelpers.isSame(worldIn.getFluidState(pos), Fluids.WATER))
            {
                worldIn.setBlockAndUpdate(pos, Helpers.copyProperties(mossy.get().defaultBlockState(), state));
            }
        }
    }
}
