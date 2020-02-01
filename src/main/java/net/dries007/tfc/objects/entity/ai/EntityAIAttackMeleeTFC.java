package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.ai.EntityAIAttackMelee;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;

public class EntityAIAttackMeleeTFC extends EntityAIAttackMelee
{
    public EntityAIAttackMeleeTFC(EntityAnimalTFC creature, double speedIn, boolean useLongMemory)
    {
        super(creature, speedIn, useLongMemory);
    }

    /*
     * Adults are aggressive, children won't attack **UNLESS** hit by target
     */
    @Override
    public boolean shouldExecute()
    {
        return (((EntityAnimalTFC) this.attacker).getAge() != IAnimalTFC.Age.CHILD || this.attacker.getRevengeTarget() != null) && super.shouldExecute();
    }
}
