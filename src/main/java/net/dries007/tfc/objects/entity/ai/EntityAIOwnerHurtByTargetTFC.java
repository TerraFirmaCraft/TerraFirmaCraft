package net.dries007.tfc.objects.entity.ai;

import net.dries007.tfc.objects.entity.animal.EntityTameableTFC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIOwnerHurtByTargetTFC  extends EntityAITarget
{
    EntityTameableTFC tameable;
    EntityLivingBase attacker;
    private int timestamp;

    public EntityAIOwnerHurtByTargetTFC(EntityTameableTFC theDefendingTameableIn)
    {
        super(theDefendingTameableIn, false);
        this.tameable = theDefendingTameableIn;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        if (!this.tameable.isTamed())
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.tameable.getOwner();

            if (entitylivingbase == null)
            {
                return false;
            }
            else
            {
                this.attacker = entitylivingbase.getRevengeTarget();
                int i = entitylivingbase.getRevengeTimer();
                return i != this.timestamp && this.isSuitableTarget(this.attacker, false) && this.tameable.shouldAttackEntity(this.attacker, entitylivingbase);
            }
        }
    }

    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase entitylivingbase = this.tameable.getOwner();

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getRevengeTimer();
        }

        super.startExecuting();
    }
}
