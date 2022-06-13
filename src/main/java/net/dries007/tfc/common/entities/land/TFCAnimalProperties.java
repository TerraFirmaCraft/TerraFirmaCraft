/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public interface TFCAnimalProperties
{
    default Entity getEntity()
    {
        return (Entity) this;
    }

    private ICalendar getCalendar()
    {
        return Calendars.get(getEntity().level);
    }

    /**
     * Get this animal gender, female or male
     *
     * @return Gender of this animal
     */
    Gender getGender();

    /**
     * Set this animal gender, used on spawn/birth
     *
     * @param gender the Gender to set to
     */
    void setGender(Gender gender);

    /**
     * Returns the birth day of this animal. Determines how old this animal is
     *
     * @return returns the day this animal has been birth
     */
    int getBirthDay();

    /**
     * Sets the birth day of this animal. Used to determine how old this animal is
     *
     * @param value the day this animal has been birth. Used when this animal spawns.
     */
    void setBirthDay(int value);

    /**
     * Returns the familiarity of this animal
     *
     * @return float value between 0-1.
     */
    float getFamiliarity();

    /**
     * Set this animal familiarity
     *
     * @param value float value between 0-1.
     */
    void setFamiliarity(float value);

    /**
     * Add a 'use' to the animal
     */
    default void addUses(int uses)
    {
        setUses(getUses() + uses);
    }

    void setUses(int uses);

    /**
     * Get the uses this animal has
     */
    int getUses();

    /**
     * Returns true if this female is pregnant, or the next time it ovulates, eggs are fertilized.
     *
     * @return true if this female has been fertilized.
     */
    boolean isFertilized();

    /**
     * Set if this female is fertilized
     *
     * @param value true on fertilization (mating)
     */
    void setFertilized(boolean value);

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
    float getAdultFamiliarityCap();

    /**
     * Get the number of days needed for this animal to be adult
     *
     * @return number of days
     */
    int getDaysToAdulthood();

    /**
     * Get the number of uses for this animal to become old
     *
     * @return number of uses, 0 to disable
     */
    int getUsesToElderly();

    /**
     * Default tag checked by isFood (edible items)
     */
    TagKey<Item> getFoodTag();

    boolean eatsRottenFood();

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

    default boolean displayMaleCharacteristics()
    {
        return !((LivingEntity) getEntity()).isBaby() && getGender() == TFCAnimalProperties.Gender.MALE;
    }

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
