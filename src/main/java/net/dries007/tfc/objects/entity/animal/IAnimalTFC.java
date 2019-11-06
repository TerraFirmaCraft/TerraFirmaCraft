/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;

import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;

public interface IAnimalTFC
{
    boolean isValidSpawnConditions(Biome biome, float temperature, float rainfall);

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
     * Event: Do things on fertilization of females
     */
    default void onFertilized(@Nonnull IAnimalTFC male)
    {
        setFertilized(true);
    }

    /**
     * Used by model renderer to scale the size of the animal
     *
     * @return double value between 0(birthday) to 1(full grown adult)
     */
    default double getPercentToAdulthood()
    {
        double value = (CalendarTFC.PLAYER_TIME.getTotalDays() - this.getBirthDay()) / (double) getDaysToAdulthood();
        if (value > 1) value = 1;
        if (value < 0) value = 0;
        return value;
    }

    /**
     * Get this entity age, based on birth
     *
     * @return the Age enum of this entity
     */
    default Age getAge()
    {
        return CalendarTFC.PLAYER_TIME.getTotalDays() >= this.getBirthDay() + getDaysToAdulthood() ? Age.ADULT : Age.CHILD;
    }

    /**
     * Get the number of days needed for this animal to be adult
     *
     * @return number of days
     */
    int getDaysToAdulthood();

    /**
     * Check if this animal is ready to mate
     *
     * @return true if ready
     */
    default boolean isReadyToMate()
    {
        return this.getAge() == Age.ADULT && !(this.getFamiliarity() < 0.3f) && !this.isFertilized() && !this.isHungry();
    }

    /**
     * Check if said item can feed this animal
     *
     * @param stack the itemstack to check
     * @return true if item is used to feed this animal (entice and increase familiarity)
     */
    default boolean isFood(@Nonnull ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "grain");
    }

    /**
     * Is this animal hungry?
     *
     * @return true if this animal can be fed by player
     */
    boolean isHungry();

    enum Age
    {
        CHILD, ADULT, OLD
    }

    enum Gender
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
