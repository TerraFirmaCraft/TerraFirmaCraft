/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.BrainAnimalBehavior;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Companion interface to {@link CommonAnimalData}, used to provide simple accessors for the underlying properties of the
 * animal data fields there.
 */
public interface CommonAnimalBehavior extends ConfigurableAnimalBehavior, BrainAnimalBehavior
{
    /**
     * @return The common animal data defined for this entity, which contains references to the synced entity data accessors.
     */
    CommonAnimalData animalData();

    /**
     * @return The synced entity data instance attached to this animal. Implemented by {@link Entity}.
     */
    SynchedEntityData getEntityData();

    /**
     * @return The current world. Implemented by {@link Entity}.
     */
    Level level();

    /**
     * @return {@code true} if this animal is a male.
     */
    default boolean isMale()
    {
        return getGender().isMale();
    }

    /**
     * @return {@code true} if this animal is a female.
     */
    default boolean isFemale()
    {
        return getGender().isFemale();
    }

    /**
     * @return The gender of this animal.
     */
    default Gender getGender()
    {
        return getEntityData().get(animalData().gender()) ? Gender.MALE : Gender.FEMALE;
    }

    /**
     * Set the gender of this animal.
     */
    default void setGender(Gender gender)
    {
        getEntityData().set(animalData().gender(), gender.isMale());
    }

    /**
     * @return The last known, recorded age of the animal.
     */
    default Age getLastAge()
    {
        return Age.valueOf(getEntityData().get(animalData().lastAge()));
    }

    /**
     * Set the age of this animal.
     */
    default void setLastAge(Age age)
    {
        getEntityData().set(animalData().lastAge(), (byte) age.ordinal());
    }

    /**
     * Increment the animal's uses, representing a proxy for how much the player has interacted with the animal.
     */
    default void addUses(int uses)
    {
        setUses(getUses() + uses);
    }

    /**
     * Set the number of uses the animal has had, representing a proxy for how much the player has interacted with the animal.
     */
    default void setUses(int uses)
    {
        getEntityData().set(animalData().uses(), uses);
    }

    /**
     * @return The number of uses this animal has had, representing a proxy for how much the player has interacted with the animal.
     */
    default int getUses()
    {
        return getEntityData().get(animalData().uses());
    }

    /**
     * @return {@code true} if this animal is both {@link Gender#FEMALE} and has been fertilized (pregnant,
     * ovulating, or eggs are fertilized).
     */
    default boolean isFertilized()
    {
        return getGender().isFemale() && getEntityData().get(animalData().fertilized());
    }

    /**
     * Sets the fertilization of this animal (pregnant, ovulating, or eggs are fertilized). Has no effect when called on
     * {@link Gender#MALE} animals.
     */
    default void setFertilized(boolean value)
    {
        if (getGender().isFemale()) getEntityData().set(animalData().fertilized(), value);
    }

    /**
     * @return The time that this animal was birthed.
     */
    default long getBirthTick()
    {
        return getEntityData().get(animalData().birthTick());
    }

    /**
     * Set this animal to be born now.
     */
    default void setBirthTickNow()
    {
        setBirthTick(calendar().getTicks());
    }

    /**
     * Set this animal to be born "a long time ago" (120 days), typically used to ensure that an animal is created as an adult.
     */
    default void setBirthTickToALongTimeAgo()
    {
        setBirthTick(calendar().getTicks() - 120 * ICalendar.TICKS_IN_DAY);
    }

    /**
     * Set the tick that this animal was born. For most wild spawned animals, this will be some arbitrary time in the past.
     */
    default void setBirthTick(long value)
    {
        getEntityData().set(animalData().birthTick(), value);
    }

    /**
     * @return The time at which this animal will transform into an "old" state, after its uses are exhausted.
     */
    default long getOldTick()
    {
        return getEntityData().get(animalData().oldTick());
    }

    /**
     * Set the time at which this animal will transform into an "old" state, after its uses are exhausted. This is set to be a random
     * duration after being used up, to simulate the passage of time, rather than have animals instantly transform upon being used.
     */
    default void setOldTick(long value)
    {
        getEntityData().set(animalData().oldTick(), value);
    }

    /**
     * Sets the animal as been fed yesterday, effectively making it instantly hungry. Used for actions like pregnancy, where
     * an animal becomes hungry immediately after.
     */
    default void setLastFedYesterday()
    {
        getEntityData().set(animalData().lastFedTick(), calendar().getTicks() - ICalendar.TICKS_IN_DAY);
    }

    /**
     * Set that this animal was just fed. This also resets the familiarity update counter, despite not modifying the animal's
     * familiarity itself, it will prevent the animal from decaying in familiarity.
     */
    default void setLastFedNow()
    {
        getEntityData().set(animalData().lastFedTick(), calendar().getTicks());
        getEntityData().set(animalData().lastFamiliarityTick(), calendar().getTicks());
    }

    /**
     * @return The day that this animal was last fed.
     */
    default long getLastFedTick()
    {
        return getEntityData().get(animalData().lastFedTick());
    }

    /**
     * @return The familiarity of this animal, which is a value in {@code [0, 1]} representing how familiar with player(s) it is.
     * This includes that familiarity may have possibly decayed since the last access.
     */
    default float getFamiliarity()
    {
        return getEntityData().get(animalData().familiarity());
    }

    /**
     * Set the animal's familiarity. The value will be clamped to the range {@code [0, 1]}. This also updates the last time that familiarity
     * for this animal was updated.
     */
    default void setFamiliarity(float value)
    {
        getEntityData().set(animalData().familiarity(), Mth.clamp(value, 0f, 1f));
        getEntityData().set(animalData().lastFamiliarityTick(), calendar().getTicks());
    }

    /**
     * @return The time that this animal was last familiarized. This is used to control familiarity decay - it will start decaying after this
     * reaches a long enough difference (one day),
     */
    default long getLastFamiliarityTick()
    {
        return getEntityData().get(animalData().lastFamiliarityTick());
    }

    /**
     * Set the animal as just having tried to mate now.
     */
    default void setLastMatedNow()
    {
        getEntityData().set(animalData().lastMateTick(), calendar().getTicks());
    }

    /**
     * @return The time that this animal last tried to mate.
     */
    default long getLastMateTick()
    {
        return getEntityData().get(animalData().lastMateTick());
    }

    /**
     * @return The correct calendar for the logical side the entity is on.
     */
    default ICalendar calendar()
    {
        return Calendars.get(level());
    }
}
