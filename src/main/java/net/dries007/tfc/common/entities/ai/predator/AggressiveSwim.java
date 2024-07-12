/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;


import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;

import net.dries007.tfc.common.entities.predator.Predator;

public class AggressiveSwim extends Behavior<Predator>
{
    private final float jumpChance;

    public AggressiveSwim(float jumpChance)
    {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.REGISTERED));
        this.jumpChance = jumpChance;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Predator predator)
    {
        return predator.isInWater() && predator.getFluidTypeHeight(NeoForgeMod.WATER_TYPE.value()) > predator.getFluidJumpThreshold() || predator.isInLava();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Predator entity, long gameTime)
    {
        return this.checkExtraStartConditions(level, entity);
    }

    @Override
    protected void tick(ServerLevel level, Predator predator, long gameTime)
    {
        if (predator.getRandom().nextFloat() < this.jumpChance)
        {
            predator.getJumpControl().jump();
            final Brain<Predator> brain = predator.getBrain();
            brain.getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(entity -> {
                final Vec3 dist = entity.position().subtract(predator.position());
                if (dist.lengthSqr() > 3f * 3f && entity.getY() - 3 <= predator.getY())
                {
                    Vec3 scaled = dist.normalize().scale(0.25f);
                    predator.setDeltaMovement(predator.getDeltaMovement().add(scaled.x, 0.1, scaled.z));
                }
            });
        }
    }
}
