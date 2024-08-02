/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import net.dries007.tfc.config.animals.AnimalConfig;

public interface ConfigurableAnimalBehavior
{
    /**
     * @return The common configurations options defined for this entity, which are common to all animals.
     */
    AnimalConfig animalConfig();

    /**
     * @return The maximum obtainable familiarity for wild-born animal adults. This does not apply to animals that were born
     * as the child of familiarized animals.
     */
    default float getAdultFamiliarityCap()
    {
        return animalConfig().familiarityCap().get().floatValue();
    }

    /**
     * @return The number of days required for this animal to fully become an adult.
     */
    default int getDaysToAdulthood()
    {
        return animalConfig().adulthoodDays().get();
    }

    /**
     * @return The number of uses required for this animal to become old
     */
    default int getUsesToElderly()
    {
        return animalConfig().uses().get();
    }

    /**
     * @return {@code true} if this animal will eat rotten food
     */
    default boolean eatsRottenFood()
    {
        return animalConfig().eatsRottenFood().get();
    }
}
