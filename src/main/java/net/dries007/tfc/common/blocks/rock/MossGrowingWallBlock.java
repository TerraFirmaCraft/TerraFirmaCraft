/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class MossGrowingWallBlock extends WallBlock implements IMossGrowingBlock
{
    private final Supplier<? extends Block> mossy;

    public MossGrowingWallBlock(Properties properties, Supplier<? extends Block> mossy)
    {
        super(properties);

        this.mossy = mossy;
    }

    @Override
    public void convertToMossy(World worldIn, BlockPos pos, BlockState state, boolean needsWater)
    {
        if (!needsWater || FluidHelpers.isSame(worldIn.getFluidState(pos), Fluids.WATER))
        {
            worldIn.setBlockAndUpdate(pos, Helpers.copyProperties(mossy.get().defaultBlockState(), state));
        }
    }
}
