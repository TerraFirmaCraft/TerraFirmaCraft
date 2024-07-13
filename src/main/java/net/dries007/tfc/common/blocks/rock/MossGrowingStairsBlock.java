/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class MossGrowingStairsBlock extends StairBlock implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingStairsBlock(Supplier<BlockState> state, Properties properties, Supplier<? extends Block> mossy)
    {
        super(state.get(), properties);

        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(Level level, BlockPos pos, BlockState state, boolean needsWater)
    {
        if (!needsWater || FluidHelpers.isSame(state.getFluidState(), Fluids.WATER))
        {
            level.setBlockAndUpdate(pos, Helpers.copyProperties(mossy.get().defaultBlockState(), state));
        }
    }
}
