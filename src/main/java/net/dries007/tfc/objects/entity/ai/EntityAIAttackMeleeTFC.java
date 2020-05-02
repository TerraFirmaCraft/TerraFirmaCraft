/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIWander;

import net.dries007.tfc.api.types.IAnimalTFC;

/**
 * Extends vanilla AI to add a bit more in-depth to predators
 * Additions:
 * - Day / Night hunting behaviors (Attack on one and only when provoked otherwise)
 * - Attack Reach
 * - Hunting Area via {@link EntityCreature#setHomePosAndDistance}
 */
public class EntityAIAttackMeleeTFC<T extends EntityCreature & IAnimalTFC> extends EntityAIAttackMelee
{
    protected AttackBehavior attackBehavior; // Day/Night behavior
    protected double attackReach; // Attack Reach, Vanilla is 2.0D
    protected EntityAIWander wander;

    @SuppressWarnings("unused")
    public EntityAIAttackMeleeTFC(T creature, double speed, double attackReach)
    {
        this(creature, speed, attackReach, AttackBehavior.EVERYTIME);
    }

    public EntityAIAttackMeleeTFC(T creature, double speed, double attackReach, AttackBehavior attackBehavior)
    {
        super(creature, speed, true);
        this.attackBehavior = attackBehavior;
        this.attackReach = attackReach;
        this.wander = null;
    }

    /**
     * Sets a wander AI to immediately move this AI to inside it's hunting area after it stops hunting a creature
     * (This is to make it not weird- The creature stopped in its tracks and is just looking at your face)
     *
     * @param wander the Wander AI
     * @return this object for convenience, to be used in the initialization process of creatures
     */
    public EntityAIAttackMeleeTFC<T> setWanderAI(EntityAIWander wander)
    {
        this.wander = wander;
        return this;
    }

    /*
     * Adults are aggressive, children won't attack **UNLESS** hit by target
     * Also handles hunter behaviors
     */
    @Override
    public boolean shouldExecute()
    {
        if (attackBehavior != AttackBehavior.EVERYTIME && this.attacker.getRevengeTarget() == null)
        {
            if (attackBehavior == AttackBehavior.DAYLIGHT_ONLY && !this.attacker.world.isDaytime())
            {
                return false;
            }
            else if (attackBehavior == AttackBehavior.NIGHTTIME_ONLY && this.attacker.world.isDaytime())
            {
                return false;
            }
        }
        if (((IAnimalTFC) this.attacker).getAge() != IAnimalTFC.Age.CHILD || this.attacker.getRevengeTarget() != null)
        {
            if (this.attacker.getRevengeTarget() != null)
            {
                // Updates hunting area, avoiding exploit (hit & run outside of it's reach (resetting aggro), get back and hit again)
                this.attacker.setHomePosAndDistance(this.attacker.getPosition(), 80);
            }
            EntityLivingBase target = this.attacker.getAttackTarget();

            if (target == null)
            {
                return false;
            }
            else if (!target.isEntityAlive())
            {
                return false;
            }
            else if (this.attacker.isWithinHomeDistanceFromPosition(target.getPosition())) // If target is inside the hunter's area
            {
                if (this.attacker.getNavigator().getPathToEntityLiving(target) != null)
                {
                    return true;
                }
                else
                {
                    return this.getAttackReachSqr(target) >= this.attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        boolean flag = super.shouldContinueExecuting();
        if (flag && this.attacker.isRiding())
        {
            this.attacker.dismountRidingEntity();
        }
        return flag;
    }

    @Override
    public void resetTask()
    {
        super.resetTask();
        if (this.wander != null)
        {
            wander.makeUpdate();
        }
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return Math.pow(this.attacker.width * attackReach, 2.0D) + attackTarget.width;
    }

    public enum AttackBehavior
    {
        DAYLIGHT_ONLY,
        NIGHTTIME_ONLY,
        EVERYTIME
    }
}
