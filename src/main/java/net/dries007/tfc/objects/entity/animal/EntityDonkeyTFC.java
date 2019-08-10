package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import net.dries007.tfc.util.calendar.CalendarTFC;

public class EntityDonkeyTFC extends AbstractChestHorseTFC
{
    public EntityDonkeyTFC(World worldIn)
    {
        super(worldIn);
    }

    public static void registerFixesDonkeyTFC(DataFixer fixer)
    {
        AbstractChestHorseTFC.registerFixesAbstractChestHorseTFC(fixer, EntityDonkeyTFC.class);
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_DONKEY;
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_DONKEY_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_DONKEY_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_DONKEY_HURT;
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
            return this.canMate() && ((AbstractHorseTFC)otherAnimal).canMate() && super.canMateWith(otherAnimal);
        }
    }

    @Override
    public void birthChildren()
    {
        int numberOfChilds = 1; //one always
        for (int i = 0; i < numberOfChilds; i++)
        {
            AbstractHorseTFC baby = (AbstractHorseTFC)createChild(this);
            if (baby != null)
            {
                baby.setBirthDay((int) CalendarTFC.PLAYER_TIME.getTotalDays());
                baby.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                this.world.spawnEntity(baby);
            }
        }
    }

    public EntityAgeable createChild(EntityAgeable ageable)
    {
        AbstractHorseTFC abstracthorse = (AbstractHorseTFC)(ageable instanceof EntityHorseTFC ? new EntityMuleTFC(this.world) : new EntityDonkeyTFC(this.world));
        this.setOffspringAttributes(ageable, abstracthorse);
        return abstracthorse;
    }
}
