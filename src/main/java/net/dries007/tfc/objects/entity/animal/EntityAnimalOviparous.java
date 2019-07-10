/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.dries007.tfc.util.calendar.CalendarTFC;

public abstract class EntityAnimalOviparous extends EntityAnimalTFC
{
    //No visual effect on client, no packet updates needed
    private long eggTime; //Controls last time this entity laid eggs for females, for males, this controls last time it tried fertilizing females
    private boolean fertilized; //Controls if the next egg laying will be fertilized

    public EntityAnimalOviparous(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.eggTime = -1;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            if (CalendarTFC.INSTANCE.getTotalDays() >= eggTime + eggDaysNeeded())
            {
                //TODO make ai lay eggs in place
            }
            if (this.getGender() == Gender.MALE && this.getIsFedToday() && CalendarTFC.INSTANCE.getTotalTime() >= eggTime + CalendarTFC.TICKS_IN_HOUR * 2 && this.isReadyToMate())
            {
                //Rooster, fertilize our chickens!
                this.setInLove(null);
                this.eggTime = CalendarTFC.INSTANCE.getTotalTime();
            }
            if (eggTime > CalendarTFC.INSTANCE.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.eggTime = (int) CalendarTFC.INSTANCE.getTotalDays();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("fertilized", fertilized);
        nbt.setLong("eggTime", eggTime);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.fertilized = nbt.getBoolean("fertilized");
        this.eggTime = nbt.getLong("eggTime");
    }

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand)
    {
        if (super.processInteract(player, hand))
        {
            if (this.isReadyToMate() && this.getGender() == Gender.MALE)
            {
                this.setInLove(player); //Force rooster to breed chickens
                this.eggTime = CalendarTFC.INSTANCE.getTotalTime();
            }
            return true;
        }
        return false;
    }

    public boolean isFertilized()
    {
        return this.fertilized;
    }

    /**
     * How many days is needed for this entity to lay another egg?
     *
     * @return number of days needed in between egg laying
     */
    public abstract int eggDaysNeeded();

    /**
     * Get a list of items containing eggs(fertilized or not) from this entity
     *
     * @return NonNullList containing egg ItemStacks
     */
    public abstract NonNullList<ItemStack> layEggs();

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable other)
    {
        if (this.getGender() == Gender.MALE && other.getClass() == this.getClass())
        {
            ((EntityAnimalOviparous) other).fertilized = true;
            this.resetInLove();
        }
        return null;
    }

    @Override
    public boolean canMateWith(@Nonnull EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityAnimalOviparous other = (EntityAnimalOviparous) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isReadyToMate();
    }

    private boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f) return false;
        return this.getGender() != Gender.FEMALE || !this.fertilized;
    }
}