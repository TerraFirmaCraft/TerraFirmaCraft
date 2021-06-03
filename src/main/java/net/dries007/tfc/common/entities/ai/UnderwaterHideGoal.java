/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import net.dries007.tfc.common.TFCTags;

public class UnderwaterHideGoal extends MoveToBlockGoal
{
    public UnderwaterHideGoal(CreatureEntity creature, double speedIn, int length)
    {
        super(creature, speedIn, length);
    }

    @Override
    protected boolean isValidTarget(IWorldReader worldIn, BlockPos pos)
    {
        BlockState state = worldIn.getBlockState(pos);
        return state.is(TFCTags.Blocks.PLANT) && !state.getFluidState().isEmpty();
    }
}
