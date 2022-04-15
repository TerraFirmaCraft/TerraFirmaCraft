/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.Seat;
import net.dries007.tfc.common.entities.land.OviparousAnimal;
import net.dries007.tfc.util.Helpers;

public class FindNestGoal extends MoveToBlockGoal
{
    private final OviparousAnimal bird;

    public FindNestGoal(OviparousAnimal mob)
    {
        super(mob, 2.0f, 16);
        this.bird = mob;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (isReachedTarget())
        {
            Seat.sit(bird.level, bird.blockPosition(), bird);
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        return !bird.isPassenger() && super.canContinueToUse();
    }

    @Override
    public boolean canUse()
    {
        return bird.isReadyForAnimalProduct() && !bird.isPassenger() && super.canUse();
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos), TFCBlocks.NEST_BOX.get());
    }
}
