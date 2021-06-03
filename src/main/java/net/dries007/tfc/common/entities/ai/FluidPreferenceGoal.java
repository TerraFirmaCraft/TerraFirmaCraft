/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class FluidPreferenceGoal extends MoveToBlockGoal
{
    private final Fluid fluid;

    public FluidPreferenceGoal(CreatureEntity creature, double speedIn, int length, Fluid fluid)
    {
        super(creature, speedIn, length);
        this.fluid = fluid;
    }

    @Override
    public boolean canUse()
    {
        return super.canUse() && mob.level.getFluidState(mob.blockPosition()).getType() != fluid;
    }

    @Override
    protected boolean isValidTarget(IWorldReader worldIn, BlockPos pos)
    {
        return worldIn.getFluidState(pos).getType() == fluid;
    }
}
