/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.predator;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;

public class HealBehavior extends Behavior<LivingEntity>
{
    private final int amount;

    public HealBehavior(int amount)
    {
        super(ImmutableMap.of());
        this.amount = amount;
    }

    @Override
    protected void start(ServerLevel level, LivingEntity entity, long time)
    {
        entity.heal(amount);
    }
}
