/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.player;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;

import net.dries007.tfc.util.agriculture.Nutrient;

public interface IPlayerData
{
    float getNutrient(@Nonnull Nutrient nutrient);

    float[] getNutrients();

    void setNutrients(float[] nutrients);

    void setNutrient(@Nonnull Nutrient nutrient, float amount);

    void addNutrient(@Nonnull Nutrient nutrient, float amount);

    /**
     * Called when a player loads in the world.
     * Updates nutrients assuming that no time had passed between player log out + log in
     */
    void updateTicksFastForward();

    /**
     * Called during player tick, used to calculate thirst
     *
     * @param player the player that is ticking
     */
    void onUpdate(@Nonnull EntityPlayer player);

    float getHealthModifier();

    float getThirst();

    void setThirst(float value);

    /**
     * Drinks fluid(ie: water) to fill or drain(salt water) thirst bar.
     *
     * @param value value to fill/drain(negative)
     * @return true if the fluid was drank(ie: cooldown)
     */
    boolean drink(float value);
}
