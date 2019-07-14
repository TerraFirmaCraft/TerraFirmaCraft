/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.dries007.tfc.util.calendar.CalendarTFC;

@ParametersAreNonnullByDefault
public abstract class EntityAnimalOviparous extends EntityAnimalTFC
{
    private static final long DEFAULT_TICKS_TO_LAY_EGGS = CalendarTFC.TICKS_IN_DAY;
    private long lastLaying; //The last time(in ticks) this oviparous female laid eggs

    @SuppressWarnings("unused")
    public EntityAnimalOviparous(World worldIn)
    {
        super(worldIn);
    }

    public EntityAnimalOviparous(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.lastLaying = -1;
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            if (lastLaying > -1 && lastLaying > CalendarTFC.INSTANCE.getCalendarTime())
            {
                //Calendar went backwards by command! this need to update
                this.lastLaying = CalendarTFC.INSTANCE.getCalendarTime();
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setLong("laying", lastLaying);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.lastLaying = nbt.getLong("laying");
    }

    /**
     * Check if this female is ready to lay eggs
     *
     * @return true if ready
     */
    public boolean isReadyToLayEggs()
    {
        return this.getGender() == Gender.FEMALE && !this.isChild() && this.getFamiliarity() > 0.15f && CalendarTFC.INSTANCE.getCalendarTime() >= this.lastLaying + getCooldownLaying();
    }

    /**
     * Get a list of items containing eggs(fertilized or not) from this entity
     *
     * @return NonNullList containing egg ItemStacks
     */
    public NonNullList<ItemStack> layEggs()
    {
        this.lastLaying = CalendarTFC.INSTANCE.getCalendarTime();
        return NonNullList.create();
    }

    /**
     * How many ticks is needed for this female to lay another egg?
     *
     * @return number of ticks needed in between egg laying
     */
    public long getCooldownLaying()
    {
        return DEFAULT_TICKS_TO_LAY_EGGS;
    }
}