/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.livestock;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Animal;

import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.calendar.Calendars;

/**
 * {@link net.minecraft.world.entity.ai.behavior.AnimalMakeLove}
 */
public class BreedBehavior extends Behavior<TFCAnimal>
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
    protected boolean checkExtraStartConditions(ServerLevel level, TFCAnimal animal)
    {
        if (level.getGameTime() > nextAttemptTime)
        {
            nextAttemptTime = level.getGameTime() + 200L;
            return animal.getGender() == TFCAnimalProperties.Gender.MALE && this.findValidBreedPartner(animal).isPresent();
        }
        return false;
    }

    @Override
    protected void start(ServerLevel level, TFCAnimal animal, long time)
    {
        TFCAnimal target = this.findValidBreedPartner(animal).get();
        animal.getBrain().setMemory(MemoryModuleType.BREED_TARGET, target);
        target.getBrain().setMemory(MemoryModuleType.BREED_TARGET, animal);
        BehaviorUtils.lockGazeAndWalkToEachOther(animal, target, this.speedModifier);
        this.spawnChildAtTime = time + 60 + animal.getRandom().nextInt(50);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, TFCAnimal animal, long time)
    {
        if (!this.hasValidBreedPartner(animal))
        {
            return false;
        }
        else
        {
            TFCAnimal target = this.getBreedTarget(animal);
            return target.isAlive() && animal.canMate(target) && BehaviorUtils.entityIsVisible(animal.getBrain(), target) && time <= this.spawnChildAtTime;
        }
    }

    @Override
    protected void tick(ServerLevel level, TFCAnimal animal, long time)
    {
        Animal target = this.getBreedTarget(animal);
        BehaviorUtils.lockGazeAndWalkToEachOther(animal, target, this.speedModifier);
        if (animal.closerThan(target, 3.0D) && time >= this.spawnChildAtTime)
        {
            target.getBreedOffspring(level, animal);
            animal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            target.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        }
    }

    @Override
    protected void stop(ServerLevel level, TFCAnimal animal, long speed)
    {
        animal.setMated(Calendars.get(level).getTicks());
        animal.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        animal.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        animal.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        this.spawnChildAtTime = 0L;
    }

    private TFCAnimal getBreedTarget(TFCAnimal animal)
    {
        return (TFCAnimal) animal.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasValidBreedPartner(TFCAnimal animal)
    {
        Brain<?> brain = animal.getBrain();
        if (brain.hasMemoryValue(MemoryModuleType.BREED_TARGET))
        {
            TFCAnimal target = (TFCAnimal) brain.getMemory(MemoryModuleType.BREED_TARGET).get();
            return animal.getType() == target.getType() && target.getGender() == TFCAnimalProperties.Gender.FEMALE;
        }
        return false;
    }

    private Optional<? extends TFCAnimal> findValidBreedPartner(TFCAnimal animal)
    {
        return animal.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get().findClosest(target ->
            target.getType() == animal.getType() && target instanceof TFCAnimal targetTFCAnimal && animal.canMate(targetTFCAnimal)
        ).map(TFCAnimal.class::cast);
    }
}
