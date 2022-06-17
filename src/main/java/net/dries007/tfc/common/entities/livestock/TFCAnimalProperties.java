/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import java.util.Optional;
import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.entities.GenderedRenderAnimal;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public interface TFCAnimalProperties extends GenderedRenderAnimal
{
    default Entity getEntity()
    {
        return (Entity) this;
    }

    private SynchedEntityData entityData()
    {
        return getEntity().getEntityData();
    }

    private ICalendar getCalendar()
    {
        return Calendars.get(getEntity().level);
    }

    CommonAnimalData animalData();

    AnimalConfig animalConfig();

    default void registerCommonData()
    {
        entityData().define(animalData().gender(), true);
        entityData().define(animalData().birthday(), 0);
        entityData().define(animalData().familiarity(), 0f);
        entityData().define(animalData().uses(), 0);
        entityData().define(animalData().fertilized(), false);
    }

    default void saveCommonAnimalData(CompoundTag nbt)
    {
        nbt.putBoolean("gender", getGender().toBool());
        nbt.putInt("birth", getBirthDay());
        nbt.putBoolean("fertilized", isFertilized());
        nbt.putFloat("familiarity", getFamiliarity());
        nbt.putInt("uses", getUses());
    }

    default void readCommonAnimalData(CompoundTag nbt)
    {
        setGender(Gender.valueOf(nbt.getBoolean("gender")));
        setBirthDay(nbt.getInt("birth"));
        setFertilized(nbt.getBoolean("fertilized"));
        setFamiliarity(nbt.getFloat("familiarity"));
        setUses(nbt.getInt("uses"));
    }

    /**
     * Get this animal gender, female or male
     *
     * @return Gender of this animal
     */
    default Gender getGender()
    {
        return Gender.valueOf(entityData().get(animalData().gender()));
    }

    /**
     * Set this animal gender, used on spawn/birth
     *
     * @param gender the Gender to set to
     */
    default void setGender(Gender gender)
    {
        entityData().set(animalData().gender(), gender.toBool());
    }

    /**
     * Returns the birth day of this animal. Determines how old this animal is
     *
     * @return returns the day this animal has been birth
     */
    default int getBirthDay()
    {
        return entityData().get(animalData().birthday());
    }

    /**
     * Sets the birth day of this animal. Used to determine how old this animal is
     *
     * @param value the day this animal has been birth. Used when this animal spawns.
     */
    default void setBirthDay(int value)
    {
        entityData().set(animalData().birthday(), value);
    }

    /**
     * Returns the familiarity of this animal
     *
     * @return float value between 0-1.
     */
    default float getFamiliarity()
    {
        return entityData().get(animalData().familiarity());
    }

    /**
     * Set this animal familiarity
     *
     * @param value float value between 0-1.
     */
    default void setFamiliarity(float value)
    {
        entityData().set(animalData().familiarity(), Mth.clamp(value, 0f, 1f));
    }

    /**
     * Add a 'use' to the animal
     */
    default void addUses(int uses)
    {
        setUses(getUses() + uses);
    }

    default void setUses(int uses)
    {
        entityData().set(animalData().uses(), uses);
    }

    /**
     * Get the uses this animal has
     */
    default int getUses()
    {
        return entityData().get(animalData().uses());
    }

    /**
     * Returns true if this female is pregnant, or the next time it ovulates, eggs are fertilized.
     *
     * @return true if this female has been fertilized.
     */
    default boolean isFertilized()
    {
        return entityData().get(animalData().fertilized());
    }

    /**
     * Set if this female is fertilized
     *
     * @param value true on fertilization (mating)
     */
    default void setFertilized(boolean value)
    {
        entityData().set(animalData().fertilized(), value);
    }

    /**
     * Do things on fertilization of females (ie: save the male genes for some sort of genetic selection)
     */
    default void onFertilized(@Nonnull TFCAnimalProperties male)
    {
        setFertilized(true);
    }

    /**
     * //todo IMPLEMENT??? MIGHT NEED HACKS??? Used by model renderer to scale the size of the animal
     *
     * @return double value between 0(birthday) to 1(full grown adult)
     */
    default double getPercentToAdulthood()
    {
        long deltaDays = getCalendar().getTotalDays() - this.getBirthDay();
        long adulthoodDay = this.getDaysToAdulthood();
        return Math.max(0, Math.min(1, (double) deltaDays / adulthoodDay));
    }

    /**
     * Get this entity age, based on birth
     *
     * @return the Age enum of this entity
     */
    default Age getAgeType()
    {
        long deltaDays = getCalendar().getTotalDays() - this.getBirthDay();
        long adulthoodDay = this.getDaysToAdulthood();
        if (getUses() > getUsesToElderly())
        {
            return Age.OLD; // if enabled, only for familiarizable animals
        }
        else if (deltaDays > adulthoodDay)
        {
            return Age.ADULT;
        }
        else
        {
            return Age.CHILD;
        }
    }

    /**
     * What is the maximum familiarity obtainable for adults of this animal?
     *
     * @return 0 if not familiarizable at all, [0, 1] for a cap
     */
    default float getAdultFamiliarityCap()
    {
        return animalConfig().familiarityCap().get().floatValue();
    }

    /**
     * Get the number of days needed for this animal to be adult
     *
     * @return number of days
     */
    default int getDaysToAdulthood()
    {
        return animalConfig().adulthoodDays().get();
    }

    /**
     * Get the number of uses for this animal to become old
     *
     * @return number of uses, 0 to disable
     */
    default int getUsesToElderly()
    {
        return animalConfig().uses().get();
    }

    default boolean eatsRottenFood()
    {
        return animalConfig().eatsRottenFood().get();
    }


    /**
     * Default tag checked by isFood (edible items)
     */
    TagKey<Item> getFoodTag();


    void setMated();

    /**
     * Check if this animal is ready to mate
     *
     * @return true if ready
     */
    default boolean isReadyToMate()
    {
        return this.getAgeType() == Age.ADULT && !(this.getFamiliarity() < 0.3f) && !this.isFertilized() && !this.isHungry();
    }

    /**
     * Is this animal hungry?
     *
     * @return true if this animal can be fed by player
     */
    boolean isHungry();

    /**
     * Which animal type is this? Do this animal lay eggs or give birth to it's offspring?
     *
     * @return the enum Type of this animal.
     */
    Type getTFCAnimalType();

    /**
     * Some animals can give products (eg: Milk, Wool and Eggs)
     * This function returns if said animal is ready to be worked upon
     * (or if it is ready to lay eggs on it's own)
     *
     * ** Check for everything **
     * this function should return only true if the animal will give it's products upon work
     * (so TOP integration could show this animal is ready)
     *
     * @return true if it is ready for product production
     */
    default boolean isReadyForAnimalProduct()
    {
        return false;
    }

    /**
     * Weaker sub-check of isReadyForAnimalProduct that isn't concerned with familiarity
     */
    default boolean hasProduct()
    {
        return false;
    }

    /**
     * Set this animal on produce cooldown
     * This means that you just sheared a sheep, your chicken just laid eggs, or you just milked your cow
     */
    default void setProductsCooldown()
    {
    }

    /**
     * Returns the number of ticks remaining for this animal to finish its produce cooldown
     *
     * @return ticks remaining to finish cooldown
     */
    default long getProductsCooldown()
    {
        return 0;
    }


    @Override
    default boolean displayMaleCharacteristics()
    {
        return !((LivingEntity) getEntity()).isBaby() && getGender() == TFCAnimalProperties.Gender.MALE;
    }

    @Override
    default boolean displayFemaleCharacteristics()
    {
        return !((LivingEntity) getEntity()).isBaby() && getGender() == TFCAnimalProperties.Gender.FEMALE;
    }

    default boolean isFood(ItemStack stack)
    {
        if (!eatsRottenFood())
        {
            Optional<Boolean> rot = stack.getCapability(FoodCapability.CAPABILITY).map(IFood::isRotten);
            if (rot.isPresent() && rot.get())
            {
                return false;
            }
        }
        return Helpers.isItem(stack, getFoodTag());
    }

    enum Age
    {
        CHILD, ADULT, OLD
    }

    enum Gender
    {
        MALE, FEMALE;

        public static Gender valueOf(boolean value)
        {
            return value ? MALE : FEMALE;
        }

        public boolean toBool()
        {
            return this == MALE;
        }
    }

    enum Type
    {
        MAMMAL, OVIPAROUS
    }
}
