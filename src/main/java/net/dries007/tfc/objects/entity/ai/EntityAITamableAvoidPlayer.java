package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;

import net.dries007.tfc.api.types.IAnimalTFC;

//AI to avoid players unless familiarized. Must be applied to EntityAnimal that implements IAnimalTFC
public class EntityAITamableAvoidPlayer<T extends EntityCreature & IAnimalTFC> extends EntityAIAvoidEntity<EntityPlayer>
{
    public EntityAITamableAvoidPlayer(T entityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        super(entityIn, EntityPlayer.class, avoidDistanceIn, farSpeedIn, nearSpeedIn);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityCreature animal = this.entity;
        if (((IAnimalTFC) animal).getFamiliarity() > 0 || isBegging(animal)) //since AITempt actually changes movement, and begging just turns the head
        {
            return false;
        }
        return super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (isBegging(this.entity))
        {
            return false;
        }
        return super.shouldContinueExecuting();
    }

    public boolean isBegging(EntityCreature wolf)
    {
        return wolf instanceof EntityWolf && ((EntityWolf) wolf).isBegging();
    }
}
