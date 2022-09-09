/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.amphibian;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;

public class AmphibianPlayDeadBehavior extends Behavior<AmphibiousAnimal>
{
    public static void update(AmphibiousAnimal animal)
    {
        Brain<AmphibiousAnimal> brain = animal.getBrain();
        brain.getMemory(MemoryModuleType.PLAY_DEAD_TICKS).ifPresent(integer -> {
            if (integer <= 0)
            {
                brain.eraseMemory(MemoryModuleType.PLAY_DEAD_TICKS);
                brain.eraseMemory(MemoryModuleType.HURT_BY_ENTITY);
                brain.useDefaultActivity();
            }
            else
            {
                brain.setMemory(MemoryModuleType.PLAY_DEAD_TICKS, integer - 1);
            }
        });
    }

    public AmphibianPlayDeadBehavior()
    {
        super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT), 200);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, AmphibiousAnimal animal, long time)
    {
        return animal.getBrain().hasMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS);
    }

    @Override
    protected void start(ServerLevel level, AmphibiousAnimal animal, long time)
    {
        Brain<AmphibiousAnimal> brain = animal.getBrain();
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        if (animal.isPlayingDeadEffective())
        {
            animal.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, false));
            animal.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2, false, false));
        }
    }
}
