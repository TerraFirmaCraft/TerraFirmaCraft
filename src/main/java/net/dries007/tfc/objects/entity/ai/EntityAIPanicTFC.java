/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;

/**
 * Improves Panic AI by making this entity runs whenever it receives damage
 */
public class EntityAIPanicTFC extends EntityAIPanic
{
    protected int timer;

    public EntityAIPanicTFC(EntityCreature creature, double speedIn)
    {
        super(creature, speedIn);
    }

    @Override
    public boolean shouldExecute()
    {
        if (this.creature.hurtTime > 0)
        {
            timer = 80;
            return this.findRandomPosition();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return super.shouldContinueExecuting() && timer > 0;
    }
}
