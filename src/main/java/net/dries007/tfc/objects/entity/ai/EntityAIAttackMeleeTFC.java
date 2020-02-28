/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.ai.EntityAIAttackMelee;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;

public class EntityAIAttackMeleeTFC extends EntityAIAttackMelee
{
    protected AttackBehavior attackBehavior;

    @SuppressWarnings("unused")
    public EntityAIAttackMeleeTFC(EntityAnimalTFC creature, double speedIn, boolean useLongMemory)
    {
        this(creature, speedIn, useLongMemory, AttackBehavior.EVERYTIME);
    }

    public EntityAIAttackMeleeTFC(EntityAnimalTFC creature, double speedIn, boolean useLongMemory, AttackBehavior attackBehavior)
    {
        super(creature, speedIn, useLongMemory);
        this.attackBehavior = attackBehavior;
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
        return (((EntityAnimalTFC) this.attacker).getAge() != IAnimalTFC.Age.CHILD || this.attacker.getRevengeTarget() != null) && super.shouldExecute();
    }

    public enum AttackBehavior
    {
        DAYLIGHT_ONLY,
        NIGHTTIME_ONLY,
        EVERYTIME
    }
}
