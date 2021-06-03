/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.vector.Vector3d;

public class TFCAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T>
{
    public TFCAvoidEntityGoal(AbstractFishEntity fish, Class<T> avoidClass, float maxDistance, double farSpeedIn, double nearSpeedIn)
    {
        super(fish, avoidClass, maxDistance, farSpeedIn, nearSpeedIn, EntityPredicates.NO_SPECTATORS::test);
    }

    @Override
    public boolean canUse()
    {
        toAvoid = mob.level.getNearestLoadedEntity(avoidClass, new EntityPredicate().range(maxDist).selector(EntityPredicates.NO_SPECTATORS::test), mob, mob.getX(), mob.getY(), mob.getZ(), mob.getBoundingBox().inflate(maxDist, 4.0D, maxDist));
        if (toAvoid == null)
        {
            return false;
        }
        else
        {
            if (toAvoid.isSteppingCarefully() && !toAvoid.isInWater()) return false; //tfc: sneaking helps

            Vector3d vector3d = RandomPositionGenerator.getPosAvoid(mob, 16, 7, toAvoid.position());
            if (vector3d == null)
            {
                return false;
            }
            else if (toAvoid.distanceToSqr(vector3d.x, vector3d.y, vector3d.z) < toAvoid.distanceToSqr(mob))
            {
                return false;
            }
            else
            {
                path = pathNav.createPath(vector3d.x, vector3d.y, vector3d.z, 0);
                return path != null;
            }
        }
    }
}
