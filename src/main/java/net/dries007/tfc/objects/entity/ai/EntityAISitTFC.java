/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

import net.dries007.tfc.objects.entity.animal.EntityTameableTFC;

public class EntityAISitTFC extends EntityAIBase
{
    private final EntityTameableTFC tameable;
    private boolean isSitting;

    public EntityAISitTFC(EntityTameableTFC entityIn)
    {
        this.tameable = entityIn;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!this.tameable.isTamed())
        {
            return false;
        }
        else if (this.tameable.isInWater())
        {
            return false;
        }
        else if (!this.tameable.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.tameable.getOwner();

            if (entitylivingbase == null)
            {
                return true;
            }
            else
            {
                return (!(this.tameable.getDistanceSq(entitylivingbase) < 144.0D) || entitylivingbase.getRevengeTarget() == null) && this.isSitting;
            }
        }
    }

    @Override
    public void startExecuting()
    {
        this.tameable.getNavigator().clearPath();
        this.tameable.setSitting(true);
    }

    @Override
    public void resetTask()
    {
        this.tameable.setSitting(false);
    }

    public void setSitting(boolean sitting)
    {
        this.isSitting = sitting;
    }
}
