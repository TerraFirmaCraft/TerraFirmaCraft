/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import net.dries007.tfc.objects.entity.animal.AbstractHorseTFC;

public class EntityAIRunAroundLikeCrazyTFC extends EntityAIBase
{
    private final AbstractHorseTFC horseHost;
    private final double speed;
    private double targetX;
    private double targetY;
    private double targetZ;

    public EntityAIRunAroundLikeCrazyTFC(AbstractHorseTFC horse, double speedIn)
    {
        this.horseHost = horse;
        this.speed = speedIn;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        if (!this.horseHost.isTame() && this.horseHost.isBeingRidden())
        {
            Vec3d vec3d = RandomPositionGenerator.findRandomTarget(this.horseHost, 5, 4);

            if (vec3d == null)
            {
                return false;
            }
            else
            {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean shouldContinueExecuting()
    {
        return !this.horseHost.isTame() && !this.horseHost.getNavigator().noPath() && this.horseHost.isBeingRidden();
    }

    public void startExecuting()
    {
        this.horseHost.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    public void updateTask()
    {
        if (!this.horseHost.isTame() && this.horseHost.getRNG().nextInt(50) == 0)
        {
            Entity entity = this.horseHost.getPassengers().get(0);

            if (entity == null)
            {
                return;
            }

            if (entity instanceof EntityPlayer && this.horseHost.getFamiliarity() >= 0.3f)
            {
                int i = this.horseHost.getTemper();
                int j = this.horseHost.getMaxTemper();

                if (j > 0 && this.horseHost.getRNG().nextInt(j) < i && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(horseHost, (EntityPlayer) entity))
                {
                    this.horseHost.setTamedBy((EntityPlayer) entity);
                    return;
                }

                this.horseHost.increaseTemper(5);
            }

            this.horseHost.removePassengers();
            this.horseHost.makeMad();
            this.horseHost.world.setEntityState(this.horseHost, (byte) 6);
        }
    }
}
