/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.entities.AquaticMob;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public class AquaticCritter extends WaterAnimal implements AquaticMob
{
    public AquaticCritter(EntityType<? extends WaterAnimal> type, Level level)
    {
        super(type, level);
    }


    @Override
    public void registerGoals()
    {
        super.registerGoals();
        //Will avoid the player if attacked
        goalSelector.addGoal(1, new CritterEscapeGoal(this, Player.class, 8.0F, 2.0D, 2.0D));
        // don't have the ability to swim, but will path randomly anyway, resulting in them walking around the seafloor.
        goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0F, 30));
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(TFCFluids.SALT_WATER.getSource());
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel)
    {
        return new WaterBoundPathNavigation(this, pLevel);
    }

    static class CritterEscapeGoal extends AvoidEntityGoal {

        public CritterEscapeGoal(PathfinderMob mob, Class avoidClass, float maxDist, double walkSpeedModifier, double sprintSpeedModifier) {
            super(mob, avoidClass, maxDist, walkSpeedModifier, sprintSpeedModifier);
        }

        public boolean shouldEscape() {
            return this.mob.getLastHurtByMob() != null;
        }

        @Override
        public boolean canUse() {
            if (shouldEscape()) {
                this.toAvoid = this.mob.getLastHurtByMob();

                Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
                if (vec3 == null) {
                    return false;
                } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob)) {
                    return false;
                } else {
                    this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                    return this.path != null;
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return (!this.pathNav.isDone() || this.mob.distanceToSqr(this.toAvoid) > 49.0D);
        }

    }

}
