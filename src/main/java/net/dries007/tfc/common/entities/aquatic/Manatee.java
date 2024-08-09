/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.util.Helpers;

public class Manatee extends WaterAnimal implements AquaticMob
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractFish.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.4D).add(Attributes.MAX_HEALTH, 20d);
    }

    public Manatee(EntityType<? extends WaterAnimal> type, Level level)
    {
        super(type, level);
        moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.1F, 0.5F, true);
        lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(0, new PanicGoal(this, 1.2f));
        goalSelector.addGoal(2, new TFCAvoidEntityGoal<>(this, Player.class, 8.0F, 5.0D, 5.4D));
        goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 40));
    }

    protected PathNavigation createNavigation(Level level)
    {
        return new WaterBoundPathNavigation(this, level);
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(Fluids.WATER);
    }

    @Override
    public void aiStep()
    {
        if (!isInWater() && onGround() && verticalCollision)
        {
            setDeltaMovement(getDeltaMovement().add((random.nextFloat() * 2.0F - 1.0F) * 0.05F, 0.4F, (random.nextFloat() * 2.0F - 1.0F) * 0.05F));
            setOnGround(false);
            hasImpulse = true;
            playSound(getFlopSound(), getSoundVolume(), getVoicePitch());
        }
        super.aiStep();
    }

    @Override
    public float getVoicePitch()
    {
        return super.getVoicePitch() * 0.5f;
    }

    protected SoundEvent getFlopSound()
    {
        return TFCSounds.MANATEE.flop().get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return TFCSounds.MANATEE.ambient().get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.MANATEE.death().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return TFCSounds.MANATEE.hurt().get();
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level().getBlockState(blockPosition()), TFCTags.Blocks.ANIMAL_IGNORED_PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }
}
