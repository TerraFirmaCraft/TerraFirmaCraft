/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.entities.animals;

/**
 * Interface to common methods in all animals
 */
public interface IAnimal
{
    /**
     * Gets the birth ticks of this animal.
     */
    long getBirthTicks();

    /**
     * Set the birth ticks of this animal.
     */
    void setBirthTicks(long value);

    /**
     * Get the age of this animal, based on birth ticks.
     */
    default Aging getAging()
    {
        if (getElderly() > 1)
        {
            return Aging.ELDER;
        }
        else
        {
            return getGrowth() < 1 ? Aging.CHILD : Aging.ADULT;
        }
    }

    /**
     * How well into adulthood this animal is. Float between 0-1.
     */
    float getGrowth();

    /**
     * How *elderly* this animal is, used as a threshold on when to despawn on the wild.
     * - 0-1 = not old yet
     * - 1 = just got old
     * - Each whole number should be an entire lifetime
     */
    float getElderly();

    /**
     * Wild animals will despawn when they get too old, allowing new animals to spawn
     */
    boolean isWild();

    enum Aging
    {
        CHILD, ADULT, ELDER
    }
}
