/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public class TFCMagmaBlock extends MagmaBlock
{
    public TFCMagmaBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState faceState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facing == Direction.UP && TFCBubbleColumnBlock.canExistIn(faceState.getFluidState().getType()))
        {
            level.scheduleTick(currentPos, this, 20);
        }
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        TFCBubbleColumnBlock.updateColumnForFluid(level, pos);
    }
}
