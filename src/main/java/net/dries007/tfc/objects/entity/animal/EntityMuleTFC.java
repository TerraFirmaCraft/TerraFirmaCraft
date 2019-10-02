/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.LootTablesTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityMuleTFC extends AbstractChestHorseTFC
{
    public static void registerFixesMuleTFC(DataFixer fixer)
    {
        AbstractChestHorseTFC.registerFixesAbstractChestHorseTFC(fixer, EntityMuleTFC.class);
    }

    public EntityMuleTFC(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        super.getHurtSound(damageSourceIn);
        return SoundEvents.ENTITY_MULE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return SoundEvents.ENTITY_MULE_DEATH;
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

    @Override
    public EntityAgeable createChild(EntityAgeable ageable)
    {
        AbstractHorseTFC abstracthorse = (new EntityMuleTFC(this.world));
        this.setOffspringAttributes(ageable, abstracthorse);
        return abstracthorse;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return SoundEvents.ENTITY_MULE_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return LootTablesTFC.ANIMALS_HORSE;
    }

    @Override
    protected void playChestEquipSound()
    {
        this.playSound(SoundEvents.ENTITY_MULE_CHEST, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }
}
