package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.passive.EntityAnimal;

import net.dries007.tfc.api.types.IAnimalTFC;

//AI to avoid players unless familiarized. Must be applied to EntityAnimal that implements IAnimalTFC
public class EntityAITamableAvoidPlayer extends EntityAIAvoidEntity
{
    public EntityAITamableAvoidPlayer(EntityAnimal entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
    {
        super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
        if (!(entityIn instanceof IAnimalTFC))
        {
            throw new AssertionError();
        }
    }

    @Override
    public boolean shouldExecute()
    {
        if (((IAnimalTFC)this.entity).getFamiliarity() > 0)
        {
            return false;
        }
        return super.shouldExecute();
    }
}
