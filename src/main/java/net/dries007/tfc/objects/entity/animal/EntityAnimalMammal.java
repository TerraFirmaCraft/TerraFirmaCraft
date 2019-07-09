package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import net.dries007.tfc.util.calendar.CalendarTFC;

public abstract class EntityAnimalMammal extends EntityAnimalTFC
{
    //No visual effect on client, no packet updates needed
    private long breedTime; //Controls pregnancy for females, cooldown to the next breeding for males
    private boolean pregnant;

    public EntityAnimalMammal(World worldIn, Gender gender, long birthTime)
    {
        super(worldIn, gender, birthTime);
        this.breedTime = 0;
        this.pregnant = false;
    }

    public boolean isPregnant()
    {
        return this.pregnant;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote && pregnant && CalendarTFC.INSTANCE.getCalendarTime() > breedTime + gestationTicks())
        {
            birthChildren();
            pregnant = false;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("pregnant", pregnant);
        nbt.setLong("breedTime", breedTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.pregnant = nbt.getBoolean("pregnant");
        this.breedTime = nbt.getLong("breedTime");
    }

    /**
     * Return the number of ticks for a full gestation
     *
     * @return long value in ticks
     */
    public abstract long gestationTicks();

    /**
     * Spawns children of this animal
     */
    public abstract void birthChildren();

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty())
        {
            if (this.isBreedingItem(itemstack))
            {
                if (!this.isInLove() && this.isReadyToMate())
                {
                    if (!this.world.isRemote)
                    {
                        breedTime = CalendarTFC.INSTANCE.getCalendarTime();
                        this.consumeItemFromStack(player, itemstack);
                        this.setInLove(player);
                    }
                    return true;
                }
                return false;
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    public boolean canMateWith(@Nonnull EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityAnimalMammal other = (EntityAnimalMammal) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable)
    {
        if (this.getGender() == Gender.FEMALE)
        {
            //Only females, duh
            this.pregnant = true;

            //please, don't reset male's love in this function. if this method is called first in males, the female's isn't called(don't get pregnant)
            this.resetInLove();
        }
        return null;
    }

    private boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT) return false;
        if (this.breedTime > 0 && CalendarTFC.INSTANCE.getCalendarTime() < this.breedTime + CalendarTFC.TICKS_IN_DAY)
            return false; //Can try mating once per day
        return this.getGender() != Gender.FEMALE || !this.isPregnant(); //Females can't mate while pregnant, duh
//todo add familiarity check
    }
}
