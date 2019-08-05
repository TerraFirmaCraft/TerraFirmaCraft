package net.dries007.tfc.objects.entity.ai;

import com.google.common.base.Predicate;
import net.dries007.tfc.objects.entity.animal.EntityTameableTFC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class EntityAITargetNonTamedTFC<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T>
{
    private final EntityTameableTFC tameable;

    public EntityAITargetNonTamedTFC(EntityTameableTFC entityIn, Class<T> classTarget, boolean checkSight, Predicate<? super T > targetSelector)
    {
        super(entityIn, classTarget, 10, checkSight, false, targetSelector);
        this.tameable = entityIn;
    }

    public boolean shouldExecute()
    {
        return !this.tameable.isTamed() && super.shouldExecute();
    }
}