package net.dries007.tfc.objects.entity.ai;

import net.dries007.tfc.objects.entity.animal.EntityWolfTFC;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityAIBegTFC extends EntityAIBase
{
    private final EntityWolfTFC wolf;
    private EntityPlayer player;
    private final World world;
    private final float minPlayerDistance;
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
        this.player = this.world.getClosestPlayerToEntity(this.wolf, (double)this.minPlayerDistance);
        return this.player == null ? false : this.hasTemptationItemInHand(this.player);
    }

    public boolean shouldContinueExecuting()
    {
        if (!this.player.isEntityAlive())
        {
            return false;
        }
        else if (this.wolf.getDistanceSq(this.player) > (double)(this.minPlayerDistance * this.minPlayerDistance))
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
        this.wolf.getLookHelper().setLookPosition(this.player.posX, this.player.posY + (double)this.player.getEyeHeight(), this.player.posZ, 10.0F, (float)this.wolf.getVerticalFaceSpeed());
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
