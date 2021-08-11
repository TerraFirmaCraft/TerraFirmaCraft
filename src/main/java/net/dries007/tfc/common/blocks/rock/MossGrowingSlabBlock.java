/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MossGrowingSlabBlock extends SlabBlock implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingSlabBlock(Properties properties, Supplier<? extends Block> mossy)
    {
        super(properties);

        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(Level worldIn, BlockPos pos, BlockState state, boolean needsWater)
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
