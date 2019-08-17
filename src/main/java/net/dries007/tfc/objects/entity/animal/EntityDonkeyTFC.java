/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class EntityDonkeyTFC extends AbstractChestHorseTFC
{
    public static void registerFixesDonkeyTFC(DataFixer fixer)
    {
        AbstractChestHorseTFC.registerFixesAbstractChestHorseTFC(fixer, EntityDonkeyTFC.class);
    }

    public EntityDonkeyTFC(World worldIn)
    {
        super(worldIn);
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_DONKEY_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_DONKEY_DEATH;
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1; //one always
        for (int i = 0; i < numberOfChilds; i++)
        {
            AbstractHorseTFC baby = (AbstractHorseTFC) createChild(this);
            if (baby != null)
            {
                baby.setBirthDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
                baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                this.world.spawnEntity(baby);
            }
        }
    }

    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal == this)
        {
            return false;
        }
        else if (!(otherAnimal instanceof EntityDonkeyTFC) && !(otherAnimal instanceof EntityHorseTFC))
        {
            return false;
        }
        else
        {
            return this.canMate() && ((AbstractHorseTFC) otherAnimal).canMate() && super.canMateWith(otherAnimal);
        }
    }

    public EntityAgeable createChild(EntityAgeable ageable)
    {
        AbstractHorseTFC abstracthorse = (ageable instanceof EntityHorseTFC ? new EntityMuleTFC(this.world) : new EntityDonkeyTFC(this.world));
        this.setOffspringAttributes(ageable, abstracthorse);
        return abstracthorse;
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_DONKEY_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_HORSE;
    }
}
