/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.fluids.FluidHelpers;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class MossGrowingBlock extends Block implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingBlock(Properties properties, Supplier<? extends Block> mossy)
    {
        super(properties);
        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(Level worldIn, BlockPos pos, BlockState state, boolean needsWater)
    {
        if (!needsWater || FluidHelpers.isSame(worldIn.getFluidState(pos.above()), Fluids.WATER))
        {
            worldIn.setBlock(pos, mossy.get().defaultBlockState(), 3);
        }
    }
}
