/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import net.dries007.tfc.objects.entity.animal.EntityWolfTFC;

public class EntityAIBegTFC extends EntityAIBase
{
    private final EntityWolfTFC wolf;
    private final World world;
    private final float minPlayerDistance;
    private EntityPlayer player;
    private int timeoutCounter;

    public EntityAIBegTFC(EntityWolfTFC wolf, float minDistance)
    {
        this.wolf = wolf;
        this.world = wolf.world;
        this.minPlayerDistance = minDistance;
        this.setMutexBits(2);
    }

    public boolean shouldExecute()
    {
        this.player = this.world.getClosestPlayerToEntity(this.wolf, this.minPlayerDistance);
        return this.player != null && this.hasTemptationItemInHand(this.player);
    }

    public boolean shouldContinueExecuting()
    {
        if (!this.player.isEntityAlive())
        {
            return false;
        }
        else if (this.wolf.getDistanceSq(this.player) > (double) (this.minPlayerDistance * this.minPlayerDistance))
        {
            return false;
        }
        else
        {
            return this.timeoutCounter > 0 && this.hasTemptationItemInHand(this.player);
        }
    }

    public void startExecuting()
    {
        this.wolf.setBegging(true);
        this.timeoutCounter = 40 + this.wolf.getRNG().nextInt(40);
    }

    public void resetTask()
    {
        this.wolf.setBegging(false);
        this.player = null;
    }

    public void updateTask()
    {
        this.wolf.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double) this.player.getEyeHeight(), this.player.posZ, 10.0F, (float) this.wolf.getVerticalFaceSpeed());
        --this.timeoutCounter;
    }

    private boolean hasTemptationItemInHand(EntityPlayer player)
    {
        for (EnumHand enumhand : EnumHand.values())
        {
            ItemStack itemstack = player.getHeldItem(enumhand);

            if (this.wolf.isTamed() && itemstack.getItem() == Items.BONE)
            {
                return true;
            }

            if (this.wolf.isBreedingItem(itemstack))
            {
                return true;
            }
        }

        return false;
    }
}
