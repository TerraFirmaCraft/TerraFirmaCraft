/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import net.dries007.tfc.common.TFCTags;

public class TFCBushBlock extends BushBlock
{
    public TFCBushBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return super.isValidGround(state, worldIn, pos) || TFCTags.Blocks.BUSH_PLANTABLE_ON.contains(state.getBlock());
    }
}
