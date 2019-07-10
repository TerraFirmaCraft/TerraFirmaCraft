/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nonnull;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import net.dries007.tfc.util.calendar.CalendarTFC;

public abstract class EntityAnimalTFC extends EntityAnimal
{
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.FLOAT);
    private long lastFed;
    private long lastFDecay;

    public EntityAnimalTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
        this.lastFed = -1;
        this.lastFDecay = CalendarTFC.INSTANCE.getTotalDays();
    }

    public Gender getGender()
    {
        return Gender.fromBool(this.dataManager.get(GENDER));
    }

    public void setGender(Gender gender)
    {
        this.dataManager.set(GENDER, gender.toBool());
    }

    public int getBirthDay()
    {
        return this.dataManager.get(BIRTHDAY);
    }

    public void setBirthDay(int value)
    {
        this.dataManager.set(BIRTHDAY, value);
    }

    public float getFamiliarity()
    {
        return this.dataManager.get(FAMILIARITY);
    }

    public void setFamiliarity(float value)
    {
        if (value < 0f) value = 0f;
        if (value > 1f) value = 1f;
        this.dataManager.set(FAMILIARITY, value);
    }

    public boolean getIsFedToday()
    {
        return this.lastFed == CalendarTFC.INSTANCE.getTotalDays();
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            //Is it time to decay familiarity?
            if (this.lastFDecay < CalendarTFC.INSTANCE.getTotalDays())
            {
                float familiarity = getFamiliarity();
                //If this entity was never fed(eg: new born, wild)
                //or wasn't fed yesterday(this is the starting of the second day)
                if (familiarity < 0.3f && (this.lastFed == -1 || this.lastFed - 1 < CalendarTFC.INSTANCE.getTotalDays()))
                {
                    familiarity -= 0.02 * (CalendarTFC.INSTANCE.getTotalDays() - this.lastFDecay);
                    this.lastFDecay = CalendarTFC.INSTANCE.getTotalDays();
                    if (familiarity < 0) familiarity = 0f;
                    this.setFamiliarity(familiarity);
                }
            }
            if (this.lastFDecay > CalendarTFC.INSTANCE.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.lastFDecay = CalendarTFC.INSTANCE.getTotalDays();
            }
            if (this.lastFed > -1 && this.lastFed > CalendarTFC.INSTANCE.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.lastFed = CalendarTFC.INSTANCE.getTotalDays();
            }
            if (this.getBirthDay() > CalendarTFC.INSTANCE.getTotalDays())
            {
                //Calendar went backwards by command! this need to update
                this.setBirthDay((int) CalendarTFC.INSTANCE.getTotalDays());
            }
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("gender", getGender().toBool());
        nbt.setInteger("birth", getBirthDay());
        nbt.setLong("fed", lastFed);
        nbt.setLong("decay", lastFDecay);
        nbt.setFloat("familiarity", getFamiliarity());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);
        this.setGender(Gender.fromBool(nbt.getBoolean("gender")));
        this.setBirthDay(nbt.getInteger("birth"));
        this.lastFed = nbt.getLong("fed");
        this.lastFDecay = nbt.getLong("decay");
        this.setFamiliarity(nbt.getFloat("familiarity"));

    }

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        //Todo, check for grain items
        return stack.getItem() == Items.WHEAT;
    }

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty())
        {
            if (this.isBreedingItem(itemstack) && player.isSneaking())
            {
                if (this.canFeed())
                {
                    if (!this.world.isRemote)
                    {
                        lastFed = CalendarTFC.INSTANCE.getTotalDays();
                        lastFDecay = lastFed; //No decay needed
                        this.consumeItemFromStack(player, itemstack);
                        this.setFamiliarity(this.getFamiliarity() + 0.06f);
                        world.playSound(null, this.getPosition(), SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.AMBIENT, 1.0F, 1.0F);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
        getDataManager().register(FAMILIARITY, 0f);
    }

    /**
     * Used by models renderer to scale the size of the animal
     *
     * @return float value between 0(birthday) to 1(full grown adult)
     */
    public abstract float getPercentToAdulthood();

    /**
     * Get this entity age, based on birth
     *
     * @return the Age enum of this entity
     */
    public abstract Age getAge();

    private boolean canFeed()
    {
        if (lastFed == -1) return true;
        return lastFed < CalendarTFC.INSTANCE.getTotalDays();
    }

    @Override
    public boolean isChild()
    {
        return this.getAge() == Age.CHILD;
    }

    public enum Age
    {
        CHILD, ADULT, OLD
    }

    public enum Gender
    {
        MALE, FEMALE;

        public static Gender fromBool(boolean value)
        {
            return value ? MALE : FEMALE;
        }

        public boolean toBool()
        {
            return this == MALE;
        }
    }
}
