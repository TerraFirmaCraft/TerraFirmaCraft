/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.entities.TFCFishingHook;
import net.dries007.tfc.util.Helpers;

public class TFCAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T>
{
    private final TargetingConditions avoidEntityTargeting;

    public TFCAvoidEntityGoal(PathfinderMob mob, Class<T> avoidClass, float dist, double farSpeed, double nearSpeed, TagKey<EntityType<?>> tag)
    {
        super(mob, avoidClass, dist, farSpeed, nearSpeed);
        avoidEntityTargeting = TargetingConditions.forCombat().range(dist).selector(e -> Helpers.isEntity(e, tag));
    }

    public TFCAvoidEntityGoal(PathfinderMob mob, Class<T> avoidClass, float dist, double farSpeed, double nearSpeed)
    {
        super(mob, avoidClass, dist, farSpeed, nearSpeed);
        avoidEntityTargeting = TargetingConditions.forCombat().range(dist).selector(EntitySelector.NO_SPECTATORS::test);
    }

    @Override
    public boolean canContinueToUse()
    {
        return super.canContinueToUse() && !isHooked();
    }

    @Override
    public boolean canUse()
    {
        // we copy over this method completely to avoid doubling the getNearestEntity call
        toAvoid = mob.level.getNearestEntity(mob.level.getEntitiesOfClass(avoidClass, mob.getBoundingBox().inflate(maxDist, 3.0D, maxDist), (p_148078_) -> true), avoidEntityTargeting, mob, mob.getX(), mob.getY(), mob.getZ());
        if (toAvoid == null)
        {
            return false;
        }
        else
        {
            if ((toAvoid.isSteppingCarefully() || toAvoid.getDeltaMovement().length() < 0.01D) && !toAvoid.isInWater()) return false; //tfc: stand still or sneak to avoid fish
            if (isHooked()) return false;

            Vec3 vec3 = DefaultRandomPos.getPosAway(mob, 16, 7, toAvoid.position());
            if (vec3 == null)
            {
                return false;
            }
            else if (toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < toAvoid.distanceToSqr(mob))
            {
                return false;
            }
            else
            {
                path = pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return path != null;
            }
        }
    }

    private boolean isHooked()
    {
        return toAvoid != null && toAvoid instanceof Player player && player.fishing instanceof TFCFishingHook hook && hook.getHookedIn() == mob;
    }
}
