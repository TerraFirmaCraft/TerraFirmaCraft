/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.predator;


import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.entities.ai.TFCClimberNavigation;

public class FelinePredator extends Predator
{
    public final double crouchSpeedMod;
    public final double sprintSpeedMod;
    public final double attackDistanceSquared;

    public static final EntityDataAccessor<Boolean> DATA_CLIMBING = SynchedEntityData.defineId(FelinePredator.class, EntityDataSerializers.BOOLEAN);

    public static FelinePredator createCougar(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.8, 1.5, TFCSounds.COUGAR);
    }

    public static FelinePredator createLion(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.8, 1.5, TFCSounds.LION);
    }

    public static FelinePredator createSabertooth(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.8, 1.5, TFCSounds.SABERTOOTH);
    }

    public static FelinePredator createTiger(EntityType<? extends Predator> type, Level level)
    {
        return new FelinePredator(type, level, false, 36, 0.8, 1.5, TFCSounds.TIGER);
    }

    public FelinePredator(EntityType<? extends Predator> type, Level level, boolean diurnal, double attackDistanceSquared, double crouchSpeedMod, double sprintSpeedMod, TFCSounds.EntityId sounds)
    {
        super(type, level, diurnal, sounds);

        this.crouchSpeedMod = crouchSpeedMod;
        this.sprintSpeedMod = sprintSpeedMod;
        this.attackDistanceSquared = attackDistanceSquared;
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_CLIMBING, false);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 30).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_KNOCKBACK, 1).add(Attributes.ATTACK_DAMAGE, 7).add(Attributes.KNOCKBACK_RESISTANCE, 0.25);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!level().isClientSide)
        {
            setClimbing(horizontalCollision);
        }
    }

    @Override
    public void tickAnimationStates()
    {
        if (isSleeping())
        {
            if (getRandom().nextInt(10) == 0)
            {
                level().addParticle(TFCParticles.SLEEP.get(), getX(), getY() + getEyeHeight(), getZ(), 0.01, 0.05, 0.01);
            }
            sleepingAnimation.startIfStopped(tickCount);
        }
        else
        {
            sleepingAnimation.stop();
        }
    }



    @Override
    protected PathNavigation createNavigation(Level level)
    {
        return new TFCClimberNavigation(this, level);
    }

    public void setClimbing(boolean climbing)
    {
        entityData.set(DATA_CLIMBING, climbing);
    }

    public boolean isClimbing()
    {
        return entityData.get(DATA_CLIMBING);
    }

    @Override
    public boolean onClimbable()
    {
        return isClimbing();
    }
}