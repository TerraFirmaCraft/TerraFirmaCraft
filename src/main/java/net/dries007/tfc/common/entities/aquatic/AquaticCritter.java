/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.function.Predicate;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;

import net.minecraft.world.entity.AnimationState;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;


public class AquaticCritter extends WaterAnimal implements AquaticMob
{
    public static AquaticCritter salty(EntityType<? extends WaterAnimal> type, Level level)
    {
        return new AquaticCritter(type, level, true);
    }

    public static AquaticCritter fresh(EntityType<? extends WaterAnimal> type, Level level)
    {
        return new AquaticCritter(type, level, false);
    }

    public final AnimationState idleAnimation = new AnimationState();
    public final AnimationState hurtAnimation = new AnimationState();
    private final Predicate<Fluid> fluidTest;

    public AquaticCritter(EntityType<? extends WaterAnimal> type, Level level, boolean salty)
    {
        super(type, level);
        this.fluidTest = salty ? f -> f.isSame(TFCFluids.SALT_WATER.getSource()) : f -> f.isSame(Fluids.WATER);
    }

    @Override
    public void tick()
    {
        if (level().isClientSide)
        {
            EntityHelpers.startOrStop(idleAnimation, getDeltaMovement().lengthSqr() < 1.0E-6D, tickCount);
            EntityHelpers.startOrStop(hurtAnimation, hurtTime > 0, tickCount);
        }
        super.tick();
    }

    @Override
    public void registerGoals()
    {
        super.registerGoals();
        //Will avoid the player if attacked
        goalSelector.addGoal(1, new CritterEscapeGoal<>(this, Player.class, 8.0F, 2.0D, 2.0D));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, WaterAnimal.class, 8f, 5f, 5.4f, e -> Helpers.isEntity(e, TFCTags.Entities.OCEAN_PREDATORS)));
        // don't have the ability to swim, but will path randomly anyway, resulting in them walking around the seafloor.
        goalSelector.addGoal(3, new RandomSwimmingGoal(this, 1.0F, 30));
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluidTest.test(fluid);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel)
    {
        return new WaterBoundPathNavigation(this, pLevel);
    }

    static class CritterEscapeGoal<T extends LivingEntity> extends TFCAvoidEntityGoal<T>
    {
        public CritterEscapeGoal(PathfinderMob mob, Class<T> avoidClass, float maxDist, double walkSpeedModifier, double sprintSpeedModifier)
        {
            super(mob, avoidClass, maxDist, walkSpeedModifier, sprintSpeedModifier);
        }

        public boolean shouldEscape()
        {
            return mob.getLastHurtByMob() != null;
        }

        @Override
        public boolean canUse()
        {
            return shouldEscape() && super.canUse();
        }

        @Override
        public boolean canContinueToUse()
        {
            return toAvoid != null && (!pathNav.isDone() || mob.distanceToSqr(toAvoid) > 49.0D);
        }

    }

}
