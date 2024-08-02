/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.livestock;

import java.util.Optional;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;

import net.dries007.tfc.common.entities.BrainAnimalBehavior;

/**
 * {@link net.minecraft.world.entity.ai.behavior.AnimalMakeLove}
 */
public class BreedBehavior<T extends Animal & BrainAnimalBehavior> extends Behavior<T>
{
    private final float speedModifier;

    private long spawnChildAtTime;
    private long nextAttemptTime = -1L;

    public BreedBehavior(float speed)
    {
        super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), 110);
        this.speedModifier = speed;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, T animal)
    {
        if (level.getGameTime() > nextAttemptTime)
        {
            nextAttemptTime = level.getGameTime() + 200L;
            return animal.isMale() && this.findValidBreedPartner(animal).isPresent();
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, T animal, long time)
    {
        AgeableMob target = this.findValidBreedPartner(animal).get();
        animal.getBrain().setMemory(MemoryModuleType.BREED_TARGET, target);
        target.getBrain().setMemory(MemoryModuleType.BREED_TARGET, animal);
        BehaviorUtils.lockGazeAndWalkToEachOther(animal, target, speedModifier, 2); // todo 1.21 what distance parameter?
        this.spawnChildAtTime = time + 60 + animal.getRandom().nextInt(50);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, T animal, long time)
    {
        if (!this.hasValidBreedPartner(animal))
        {
            return false;
        }
        else
        {
            AgeableMob target = animal.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
            return target.isAlive() && target instanceof Animal targetAnimal && animal.canMate(targetAnimal) && BehaviorUtils.entityIsVisible(animal.getBrain(), target) && time <= this.spawnChildAtTime;
        }
    }

    @Override
    protected void tick(ServerLevel level, T animal, long time)
    {
        AgeableMob target = animal.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
        BehaviorUtils.lockGazeAndWalkToEachOther(animal, target, speedModifier, 2);
        if (animal.closerThan(target, 3.0D) && time >= this.spawnChildAtTime)
        {
            target.getBreedOffspring(level, animal);
            animal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            target.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        }
    }

    @Override
    protected void stop(ServerLevel level, T animal, long speed)
    {
        animal.setLastMatedNow();
        animal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        animal.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        animal.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        this.spawnChildAtTime = 0L;
    }

    private boolean hasValidBreedPartner(T animal)
    {
        Brain<?> brain = animal.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.BREED_TARGET))
        {
            AgeableMob target = brain.getMemory(MemoryModuleType.BREED_TARGET).get();
            return animal.getType() == target.getType() && target instanceof BrainAnimalBehavior behavior && !behavior.isMale();
        }
        return false;
    }

    private Optional<AgeableMob> findValidBreedPartner(T animal)
    {
        return animal.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get().findClosest(target ->
            target.getType() == animal.getType() && target instanceof Animal targetAnimal && animal.canMate(targetAnimal)
        ).map(t -> (AgeableMob) t);
    }
}
