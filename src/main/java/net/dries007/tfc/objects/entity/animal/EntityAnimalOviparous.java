/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
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
    private long eggTime; //The last time this entity laid eggs
    private boolean fertilized; //is the next egg laying fertilized?
    private long matingTime; //The last time this male tried fertilizing a female

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
            if (this.getGender() == Gender.MALE && this.getIsFedToday() && this.isReadyToMate())
            {
                //Rooster, fertilize our chickens!
                this.setInLove(null);
                forceFemalesMating();
                this.matingTime = CalendarTFC.INSTANCE.getTotalHours();
            }
            if (eggTime > CalendarTFC.INSTANCE.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.eggTime = (int) CalendarTFC.INSTANCE.getTotalDays();
            }
            if (matingTime > CalendarTFC.INSTANCE.getTotalHours())
            {
                //Calendar went backwards by command! this need to update
                this.matingTime = (int) CalendarTFC.INSTANCE.getTotalHours();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("fertilized", fertilized);
        nbt.setLong("eggTime", eggTime);
        nbt.setLong("matingTime", matingTime);
    }

    public boolean isReadyToLayEggs()
    {
        return this.getGender() == Gender.FEMALE && !this.isChild() && this.getFamiliarity() > 0.15f && CalendarTFC.INSTANCE.getTotalDays() >= eggTime + eggDaysNeeded();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.fertilized = nbt.getBoolean("fertilized");
        this.eggTime = nbt.getLong("eggTime");
        this.matingTime = nbt.getLong("matingTime");
    }

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand)
    {
        if (super.processInteract(player, hand))
        {
            if (this.isReadyToMate() && this.getGender() == Gender.MALE)
            {
                this.setInLove(player); //Force rooster to breed chickens
                this.eggTime = CalendarTFC.INSTANCE.getTotalHours();
            }
            return true;
        }
        return false;
    }

    public void setFertilized(boolean value)
    {
        this.fertilized = value;
    }

    public boolean isFertilized()
    {
        return this.fertilized;
    }

    /**
     * Get a list of items containing eggs(fertilized or not) from this entity
     *
     * @return NonNullList containing egg ItemStacks
     */
    public NonNullList<ItemStack> layEggs()
    {
        this.eggTime = CalendarTFC.INSTANCE.getTotalDays();
        return NonNullList.create();
    }

    /**
     * How many days is needed for this entity to lay another egg?
     *
     * @return number of days needed in between egg laying
     */
    public abstract int eggDaysNeeded();

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable other)
    {
        if (this.getGender() == Gender.FEMALE)
        {
            this.fertilized = true;
            this.resetInLove();
        }
        return null;
    }

    private void forceFemalesMating()
    {
        List<EntityAnimalOviparous> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getEntityBoundingBox().grow(8.0D));
        for (EntityAnimalOviparous ent : list)
        {
            if (ent.getGender() == Gender.FEMALE && !ent.isInLove() && ent.isReadyToMate())
            {
                ent.setInLove(null);
            }
        }
    }

    private boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f) return false;
        if (this.getGender() == Gender.FEMALE) return !this.fertilized;
        return CalendarTFC.INSTANCE.getTotalHours() > this.matingTime + 2;
    }
}