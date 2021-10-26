/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class TFCAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T>
{
    private final TargetingConditions avoidEntityTargeting;

    public TFCAvoidEntityGoal(PathfinderMob mob, Class<T> avoidClass, float dist, double farSpeed, double nearSpeed)
    {
        super(mob, avoidClass, dist, farSpeed, nearSpeed);
        this.avoidEntityTargeting = TargetingConditions.forCombat().range(dist).selector(EntitySelector.NO_SPECTATORS::test);
    }

    @Override
    public boolean canUse()
    {
        // we copy over this method completely to avoid doubling the getNearestEntity call
        this.toAvoid = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.avoidClass, this.mob.getBoundingBox().inflate(this.maxDist, 3.0D, this.maxDist), (p_148078_) -> true), this.avoidEntityTargeting, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
        if (this.toAvoid == null)
        {
            return false;
        }
        else
        {
            if ((toAvoid.isSteppingCarefully() || toAvoid.getDeltaMovement().length() < 0.01D) && !toAvoid.isInWater()) return false; //tfc: stand still or sneak to avoid fish

            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vec3 == null)
            {
                return false;
            }
            else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob))
            {
                return false;
            }
            else
            {
                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }
    }
}
