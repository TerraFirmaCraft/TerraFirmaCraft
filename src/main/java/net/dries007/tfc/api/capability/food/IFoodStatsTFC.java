/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import javax.annotation.Nonnull;

public interface IFoodStatsTFC
{
    float MAX_PLAYER_NUTRIENTS = 100f;
    float MAX_PLAYER_THIRST = 100f;
    int FOOD_HUNGER_AMOUNT = 4; // The amount of hunger restored by eating any food, regardless of type

    float getHealthModifier();

    float getThirst();

    /**
     * Used to drink from a water source. Has an internal cooldown
     * Attention: Simulation updates the cooldown, if you need to update the value after a simulation, use #addThirst
     *
     * @param value    the amount to drink = the value to increase thirst by
     * @param simulate determines if this is a simulated drink (eg: the thirst value is not updated, but still checks if this attempt would happen)
     * @return true if the player was able to drink (cooldown + not already full)
     */
    boolean attemptDrink(float value, boolean simulate);

    /**
     * Used to directly add thirst, i.e. from an external source like a water bottle
     */
    void addThirst(float value);

    float getNutrient(@Nonnull Nutrient nutrient);

    /**
     * Sets the nutrient value directly. Used by command nutrients and for debug purposes
     *
     * @param nutrient the nutrient to set
     * @param value    the value to set to, in [0, 100]
     */
    void setNutrient(@Nonnull Nutrient nutrient, float value);
}
