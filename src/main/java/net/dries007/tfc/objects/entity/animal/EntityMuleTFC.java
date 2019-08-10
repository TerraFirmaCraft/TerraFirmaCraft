package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import net.dries007.tfc.util.calendar.CalendarTFC;

public class EntityMuleTFC extends AbstractChestHorseTFC
{
    public EntityMuleTFC(World worldIn)
    {
        super(worldIn);
    }

    public static void registerFixesMuleTFC(DataFixer fixer)
    {
        AbstractChestHorseTFC.registerFixesAbstractChestHorseTFC(fixer, EntityMuleTFC.class);
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
        AbstractHorseTFC abstracthorse = (AbstractHorseTFC)(new EntityMuleTFC(this.world));
        this.setOffspringAttributes(ageable, abstracthorse);
        return abstracthorse;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTableList.ENTITIES_MULE;
    }

    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_MULE_AMBIENT;
    }

    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_MULE_DEATH;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_MULE_HURT;
    }

    protected void playChestEquipSound()
    {
        this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }
}
