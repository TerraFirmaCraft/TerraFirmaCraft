/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Dynamic;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.AnimationState;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.WildAnimal;
import net.dries007.tfc.common.entities.ai.prey.PreyAi;

public class Prey extends WildAnimal
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    public static AttributeSupplier.Builder createLargeAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public final AnimationState walkingAnimation = new AnimationState();

    public Prey(EntityType<? extends WildAnimal> type, Level level, TFCSounds.EntitySound sounds)
    {
        super(type, level, sounds);
    }

    @Override
    protected Brain.Provider<? extends Prey> brainProvider()
    {
        return Brain.provider(PreyAi.MEMORY_TYPES, PreyAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return PreyAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level, this);
        PreyAi.updateActivity(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<Prey> getBrain()
    {
        return (Brain<Prey>) super.getBrain();
    }

    @Override
    public boolean hurt(DamageSource src, float amount)
    {
        final boolean hurt = super.hurt(src, amount);
        if (this.level.isClientSide)
        {
            return false;
        }
        else
        {
            if (hurt && src.getEntity() instanceof LivingEntity living)
            {
                PreyAi.wasHurtBy(this, living);
            }
            return hurt;
        }
    }

    @Override
    public void tick()
    {
        if (level.isClientSide)
        {
            EntityHelpers.startOrStop(walkingAnimation, EntityHelpers.isMovingOnLand(this), tickCount);
        }
        super.tick();
    }
}
