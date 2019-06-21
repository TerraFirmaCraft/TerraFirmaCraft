/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import net.dries007.tfc.util.agriculture.Nutrient;

public interface IPlayerData
{
    float getNutrient(Nutrient nutrient);

    float[] getNutrients();

    void setNutrients(float[] nutrients);

    void setNutrient(Nutrient nutrient, float amount);

    void addNutrient(Nutrient nutrient, float amount);

    void update();

    float getMaxHealth();

    float getThirst();

    void setThirst(float value);

    /**
     * Drinks fluid(ie: water) to fill or drain(salt water) thirst bar.
     * @param value value to fill/drain(negative)
     */

    /**
     * Drinks fluid(ie: water) to fill or drain(salt water) thirst bar.
     * @param value value to fill/drain(negative)
     * @return true if the fluid was drank(ie: cooldown)
     */
    boolean drink(float value);

    float getExhaustion();

    void setExhaustion(float value);

    /**
     * Adds an exhaustion(consumes thirst bar faster) modifier
     * The exhaustion worn off over time
     * @param value
     */
    void addExhaustion(float value);
}
