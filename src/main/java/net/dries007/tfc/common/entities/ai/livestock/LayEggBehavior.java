/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.livestock;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.Seat;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.land.OviparousAnimal;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

public class LayEggBehavior extends Behavior<OviparousAnimal>
{
    @Nullable
    private BlockPos targetPos;
    private int remainingTimeToReach;

    public LayEggBehavior()
    {
        super(ImmutableMap.of(TFCBrain.NEST_BOX_MEMORY.get(), MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, OviparousAnimal animal)
    {
        return animal.isReadyForAnimalProduct() && !animal.isPassenger();
    }

    @Override
    protected void start(ServerLevel level, OviparousAnimal animal, long time)
    {
        super.start(level, animal, time);
        getNearestNestBox(animal).ifPresent(pos -> {
            targetPos = pos;
            remainingTimeToReach = 100;
            startWalkingTowardsBed(animal, pos);
        });
    }

    @Override
    protected void stop(ServerLevel level, OviparousAnimal animal, long time)
    {
        super.stop(level, animal, time);
        targetPos = null;
        remainingTimeToReach = 0;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, OviparousAnimal animal, long time)
    {
        return targetPos != null && !animal.isPassenger() && animal.isReadyForAnimalProduct() && isBoxAt(level, targetPos) && !tiredOfWalking(level, animal);
    }

    @Override
    protected boolean timedOut(long time)
    {
        return false; // we implement our own stopping function
    }

    @Override
    protected void tick(ServerLevel level, OviparousAnimal animal, long time)
    {
        if (!onBox(level, animal))
        {
            --remainingTimeToReach;
        }
        else
        {
            Seat.sit(animal.level, animal.blockPosition(), animal);
            animal.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            animal.getBrain().eraseMemory(TFCBrain.NEST_BOX_MEMORY.get());
        }
    }

    private boolean onBox(ServerLevel level, OviparousAnimal animal)
    {
        return isBoxAt(level, animal.blockPosition());
    }

    private Optional<BlockPos> getNearestNestBox(OviparousAnimal animal)
    {
        return animal.getBrain().getMemory(TFCBrain.NEST_BOX_MEMORY.get());
    }

    private void startWalkingTowardsBed(OviparousAnimal animal, BlockPos pos)
    {
        animal.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        animal.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1f, 0));
    }

    private boolean isBoxAt(ServerLevel level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos), TFCBlocks.NEST_BOX.get());
    }

    private boolean tiredOfWalking(ServerLevel level, OviparousAnimal animal)
    {
        return !onBox(level, animal) && remainingTimeToReach <= 0;
    }
}
