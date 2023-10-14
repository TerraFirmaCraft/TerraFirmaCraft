/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import com.mojang.serialization.Dynamic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.prey.PreyAi;
import net.dries007.tfc.common.entities.ai.prey.RammingPreyAi;

public class RammingPrey extends WildAnimal
{

    private boolean isTelegraphingAttack;
    private int telegraphAttackTick;

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    public static AttributeSupplier.Builder createLargeAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.ATTACK_DAMAGE, 6.0D);
    }

    public final AnimationState walkingAnimation = new AnimationState();

    public RammingPrey(EntityType<? extends RammingPrey> type, Level level, TFCSounds.EntitySound sounds)
    {
        super(type, level, sounds);
    }

    @Override
    protected Brain.Provider<? extends RammingPrey> brainProvider()
    {
        return Brain.provider(RammingPreyAi.MEMORY_TYPES, RammingPreyAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return RammingPreyAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    protected void customServerAiStep()
    {
        getBrain().tick((ServerLevel) level(), this);
        RammingPreyAi.updateActivity(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<RammingPrey> getBrain()
    {
        return (Brain<RammingPrey>) super.getBrain();
    }

    @Override
    public boolean hurt(DamageSource src, float amount)
    {
        final boolean hurt = super.hurt(src, amount);
        if (this.level().isClientSide)
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
        if (level().isClientSide)
        {
            EntityHelpers.startOrStop(walkingAnimation, EntityHelpers.isMovingOnLand(this), tickCount);
        }
        super.tick();
    }

    @Override
    public void handleEntityEvent(byte toggle) {
        if (toggle == 58) {
            this.isTelegraphingAttack = true;
        } else if (toggle == 59) {
            this.isTelegraphingAttack = false;
        } else {
            super.handleEntityEvent(toggle);
        }

    }

    @Override
    public void aiStep() {
        if (this.isTelegraphingAttack) {
            ++this.telegraphAttackTick;
        } else {
            this.telegraphAttackTick -= 2;
        }

        //TODO: This needs to be a custom length animation/charge up period, as different animals will have different animations
        this.telegraphAttackTick = Mth.clamp(this.telegraphAttackTick, 0, 20);
        super.aiStep();
    }

    //TODO: Un-hardcode the 20
    //Returns progress in the telegraph animation from 0 to 1
    public float getTelegraphAniamtionProgress()
    {
        return this.telegraphAttackTick / 20f;
    }
}
