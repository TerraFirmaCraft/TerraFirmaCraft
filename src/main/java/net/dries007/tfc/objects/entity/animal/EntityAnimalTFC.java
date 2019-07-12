/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public abstract class EntityAnimalTFC extends EntityAnimal
{
    private static final long DEFAULT_TICKS_COOLDOWN_MATING = CalendarTFC.TICKS_IN_HOUR * 2;
    protected static final Set<Item> DEFAULT_BREEDING_ITEMS = Sets.newHashSet(ItemsTFC.BARLEY_GRAIN, ItemsTFC.OAT_GRAIN, ItemsTFC.RICE_GRAIN, ItemsTFC.RYE_GRAIN, ItemsTFC.WHEAT_GRAIN, ItemsTFC.MAIZE);
    //Values that has a visual effect on client
    private static final DataParameter<Boolean> GENDER = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> BIRTHDAY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.VARINT);
    private static final DataParameter<Float> FAMILIARITY = EntityDataManager.createKey(EntityAnimalTFC.class, DataSerializers.FLOAT);

    private long lastFed; //Last time(in days) this entity was fed
    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed

    private boolean fertilized; //Is this female fertilized? (in oviparous, the egg laying is fertilized, for mammals this is pregnancy)
    private long matingTime; //The last time(in ticks) this male tried fertilizing females

    public EntityAnimalTFC(World worldIn, Gender gender, int birthDay)
    {
        super(worldIn);
        this.setGender(gender);
        this.setBirthDay(birthDay);
        this.setFamiliarity(0);
        this.setGrowingAge(0); //We don't use this
        this.lastFed = -1;
        this.matingTime = -1;
        this.lastFDecay = CalendarTFC.INSTANCE.getTotalDays();
        this.fertilized = false;
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

    public boolean isFertilized() { return this.fertilized; }

    public void setFertilized(boolean value)
    {
        this.fertilized = value;
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
            if (this.getGender() == Gender.MALE && this.isReadyToMate())
            {
                this.matingTime = CalendarTFC.INSTANCE.getCalendarTime();
                if (findFemaleMate())
                {
                    this.setInLove(null);
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
            if (this.matingTime > -1 && this.matingTime > CalendarTFC.INSTANCE.getCalendarTime())
            {
                //Calendar went backwards by command! this need to update
                this.matingTime = CalendarTFC.INSTANCE.getCalendarTime();
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
        nbt.setBoolean("fertilized", this.fertilized);
        nbt.setLong("mating", matingTime);
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
        this.matingTime = nbt.getLong("mating");
        this.fertilized = nbt.getBoolean("fertilized");
        this.setFamiliarity(nbt.getFloat("familiarity"));

    }

    @Override
    public boolean isBreedingItem(ItemStack stack)
    {
        return DEFAULT_BREEDING_ITEMS.contains(stack.getItem());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
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
                else
                {
                    if (!this.world.isRemote)
                    {
                        //Show tooltips
                        if (this.isFertilized() && this instanceof EntityAnimalMammal)
                        {
                            player.sendMessage(new TextComponentTranslation(MOD_ID + ".tooltip.animal.pregnant"));
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityAnimalTFC other = (EntityAnimalTFC) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
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
     * Event, used by children of this class, to do things on fertilization of females
     */
    public void onFertilized(EntityAnimalTFC male)
    {
    }

    @Nullable
    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable other)
    {
        if (this.getGender() == Gender.FEMALE)
        {
            this.fertilized = true;
            this.resetInLove();
            this.onFertilized((EntityAnimalTFC) other);
        }
        return null;
    }

    /**
     * Find and charms a near female animal of this animal
     * Used by males to try mating with females
     *
     * @return true if found and charmed a female
     */
    protected boolean findFemaleMate()
    {
        List<EntityAnimalTFC> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getEntityBoundingBox().grow(8.0D));
        for (EntityAnimalTFC ent : list)
        {
            if (ent.getGender() == Gender.FEMALE && !ent.isInLove() && ent.isReadyToMate())
            {
                ent.setInLove(null);
                return true;
            }
        }
        return false;
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

    /**
     * Check if this animal is ready to mate
     *
     * @return true if ready
     */
    protected boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || !this.getIsFedToday())
            return false;
        return this.matingTime == -1 || this.matingTime + getCooldownMating() <= CalendarTFC.INSTANCE.getCalendarTime();
    }

    /**
     * The number of ticks this animal needs to rest before trying to mate again
     *
     * @return ticks needed to fully recover
     */
    protected long getCooldownMating()
    {
        return DEFAULT_TICKS_COOLDOWN_MATING;
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
