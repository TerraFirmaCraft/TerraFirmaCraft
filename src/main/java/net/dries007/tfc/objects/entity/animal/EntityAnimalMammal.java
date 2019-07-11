/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public abstract class EntityAnimalMammal extends EntityAnimalTFC
{
    //No visual effect on client, no packet updates needed
    private long breedTime; //Controls pregnancy for females, cooldown to the next breeding for males
    private boolean pregnant;

    public EntityAnimalMammal(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn, gender, birthDay);
        this.breedTime = -1;
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
        if (!this.world.isRemote)
        {
            if (pregnant && CalendarTFC.INSTANCE.getTotalDays() >= breedTime + gestationDays())
            {
                birthChildren();
                pregnant = false;
            }
            if (breedTime > CalendarTFC.INSTANCE.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.breedTime = (int) CalendarTFC.INSTANCE.getTotalDays();
            }
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

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand)
    {
        if (super.processInteract(player, hand)) return true; //If familiarity was done, cancel this

        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty())
        {
            if (this.isBreedingItem(itemstack) && player.isSneaking())
            {
                if (!this.isInLove() && this.isReadyToMate())
                {
                    if (!this.world.isRemote)
                    {
                        breedTime = CalendarTFC.INSTANCE.getTotalDays();
                        this.consumeItemFromStack(player, itemstack);
                        this.setInLove(player);
                    }
                    return true;
                }
                else
                {
                    if (!this.world.isRemote && !this.isInLove())
                    {
                        //Return chat message indicating why this entity isn't mating
                        if (this.getAge() == Age.OLD)
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.old"));
                        }
                        else if (this.getFamiliarity() < 0.3f)
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.lowfamiliarity"));
                        }
                        else if (this.isPregnant())
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.pregnant"));
                        }
                        else if (CalendarTFC.INSTANCE.getTotalDays() <= this.breedTime)
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.resting"));
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Spawns children of this animal
     */
    public abstract void birthChildren();

    /**
     * Return the number of days for a full gestation
     *
     * @return long value in days
     */
    public abstract long gestationDays();

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
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f) return false;
        if (this.breedTime > -1 && CalendarTFC.INSTANCE.getTotalDays() <= this.breedTime)
            return false; //Can try mating once per day
        return this.getGender() != Gender.FEMALE || !this.isPregnant(); //Females can't mate while pregnant, duh
    }
}
