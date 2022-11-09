/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import java.util.List;
import java.util.Set;
import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.common.entities.ai.TFCBrain;

public class PackLeaderSensor extends Sensor<PackPredator>
{
    public PackLeaderSensor()
    {
        super(120);
    }

    @Override
    public Set<MemoryModuleType<?>> requires()
    {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    @Override
    protected void doTick(ServerLevel level, PackPredator predator)
    {
        final AABB aabb = predator.getBoundingBox().inflate(32.0D, 32.0D, 32.0D);
        final List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, aabb, entity -> entity.isAlive() && !entity.equals(predator) && entity.getType().equals(predator.getType()));
        this.setAlpha(predator, list);
    }

    private void setAlpha(PackPredator mob, List<LivingEntity> nearby)
    {
        int maxRespect = mob.getRespect();
        PackPredator alpha = mob;
        for (LivingEntity entity : nearby)
        {
            if (entity instanceof PackPredator predator && !predator.isBaby())
            {
                final int respect = predator.getRespect();
                if (respect == maxRespect)
                {
                    // prevent predators having equal respect, ie two alphas
                    if (respect > 0)
                    {
                        predator.addRespect(-1);
                    }
                    else
                    {
                        predator.addRespect(1);
                    }
                }
                if (respect > maxRespect)
                {
                    maxRespect = respect;
                    alpha = predator;
                }
            }
        }
        mob.getBrain().setMemory(TFCBrain.ALPHA.get(), alpha);
    }
}
