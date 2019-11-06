/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public abstract class EntityAnimalTFC extends EntityAnimal implements IAnimalTFC
{
    private static final long MATING_COOLDOWN_DEFAULT_TICKS = ICalendar.TICKS_IN_HOUR * 2;

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
        this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
        this.fertilized = false;
    }

    @SuppressWarnings("unused")
    public EntityAnimalTFC(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public Gender getGender()
    {
        return Gender.fromBool(this.dataManager.get(GENDER));
    }

    @Override
    public void setGender(Gender gender)
    {
        this.dataManager.set(GENDER, gender.toBool());
    }

    @Override
    public int getBirthDay()
    {
        return this.dataManager.get(BIRTHDAY);
    }

    @Override
    public void setBirthDay(int value)
    {
        this.dataManager.set(BIRTHDAY, value);
    }

    @Override
    public float getFamiliarity()
    {
        return this.dataManager.get(FAMILIARITY);
    }

    @Override
    public void setFamiliarity(float value)
    {
        if (value < 0f) value = 0f;
        if (value > 1f) value = 1f;
        this.dataManager.set(FAMILIARITY, value);
    }

    @Override
    public boolean isFertilized() { return this.fertilized; }

    @Override
    public void setFertilized(boolean value)
    {
        this.fertilized = value;
    }

    @Override
    public boolean isReadyToMate()
    {
        if (this.getAge() != Age.ADULT || this.getFamiliarity() < 0.3f || this.isFertilized() || !this.isHungry())
            return false;
        return this.matingTime == -1 || this.matingTime + MATING_COOLDOWN_DEFAULT_TICKS <= CalendarTFC.PLAYER_TIME.getTicks();
    }

    @Override
    public boolean isHungry()
    {
        if (lastFed == -1) return true;
        return lastFed < CalendarTFC.PLAYER_TIME.getTotalDays();
    }

    @Override
    public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if (!this.world.isRemote)
        {
            // Is it time to decay familiarity?
            // If this entity was never fed(eg: new born, wild)
            // or wasn't fed yesterday(this is the starting of the second day)
            if (this.lastFDecay > -1 && this.lastFDecay + 1 < CalendarTFC.PLAYER_TIME.getTotalDays())
            {
                float familiarity = getFamiliarity();
                if (familiarity < 0.3f)
                {
                    familiarity -= 0.02 * (CalendarTFC.PLAYER_TIME.getTotalDays() - this.lastFDecay);
                    this.lastFDecay = CalendarTFC.PLAYER_TIME.getTotalDays();
                    this.setFamiliarity(familiarity);
                }
            }
            if (this.getGender() == Gender.MALE && this.isReadyToMate())
            {
                this.matingTime = CalendarTFC.PLAYER_TIME.getTicks();
                if (findFemaleMate())
                {
                    this.setInLove(null);
                }
            }
        }
    }

    @Override
    public boolean getCanSpawnHere()
    {
        return this.world.checkNoEntityCollision(getEntityBoundingBox())
            && this.world.getCollisionBoxes(this, getEntityBoundingBox()).isEmpty()
            && !this.world.containsAnyLiquid(getEntityBoundingBox());
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt)
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
    public boolean canMateWith(EntityAnimal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        EntityAnimalTFC other = (EntityAnimalTFC) otherAnimal;
        return this.getGender() != other.getGender() && this.isInLove() && other.isInLove();
    }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound nbt)
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
    protected void entityInit()
    {
        super.entityInit();
        getDataManager().register(GENDER, true);
        getDataManager().register(BIRTHDAY, 0);
        getDataManager().register(FAMILIARITY, 0f);
    }

    @Override
    public boolean isChild()
    {
        return this.getAge() == Age.CHILD;
    }

    @Override
    public boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack itemstack = player.getHeldItem(hand);

        if (!itemstack.isEmpty())
        {
            if (this.isFood(itemstack) && player.isSneaking())
            {
                if (this.isHungry())
                {
                    if (!this.world.isRemote)
                    {
                        lastFed = CalendarTFC.PLAYER_TIME.getTotalDays();
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

    @Nullable
    @Override
    public EntityAgeable createChild(@Nonnull EntityAgeable other)
    {
        if (this.getGender() == Gender.FEMALE && other instanceof IAnimalTFC)
        {
            this.fertilized = true;
            this.resetInLove();
            this.onFertilized((IAnimalTFC) other);
        }
        return null;
    }

    @Override
    public void setScaleForAge(boolean child)
    {
        double ageScale = 1 / (2.0D - getPercentToAdulthood());
        this.setScale((float) ageScale);
    }

    /**
     * Find and charms a near female animal of this animal
     * Used by males to try mating with females
     *
     * @return true if found and charmed a female
     */
    @SuppressWarnings("WeakerAccess")
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
}
