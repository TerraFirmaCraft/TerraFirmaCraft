/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.livestock;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.Seat;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.pet.MoveOntoBlockBehavior;
import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import net.dries007.tfc.util.Helpers;

public class LayEggBehavior extends MoveOntoBlockBehavior<OviparousAnimal>
{
    public LayEggBehavior()
    {
        super(TFCBrain.NEST_BOX_MEMORY.get(), true);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, OviparousAnimal animal)
    {
        return animal.isReadyForAnimalProduct() && super.checkExtraStartConditions(level, animal);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, OviparousAnimal animal, long time)
    {
        return animal.isReadyForAnimalProduct() && super.canStillUse(level, animal, time);
    }

    @Override
    protected void afterReached(OviparousAnimal mob)
    {
        Seat.sit(mob.level, mob.blockPosition(), mob);
    }

    @Override
    protected Optional<BlockPos> getNearestTarget(OviparousAnimal mob)
    {
        return mob.getBrain().getMemory(TFCBrain.NEST_BOX_MEMORY.get());
    }

    @Override
    protected boolean isTargetAt(ServerLevel level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos), TFCBlocks.NEST_BOX.get());
    }
}
