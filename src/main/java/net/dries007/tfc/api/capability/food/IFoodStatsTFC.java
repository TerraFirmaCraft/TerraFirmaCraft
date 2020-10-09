/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.TerraFirmaCraft;

public interface IFoodStatsTFC
{
    float MAX_PLAYER_THIRST = 100f;

    /**
     * Add food stats directly from an {@link ItemStack} without requiring it be an instance of {@link ItemFood}
     *
     * DO NOT call {@link net.minecraft.util.FoodStats#addStats(int, float)} directly as TFC will completely ignore it!
     *
     * @param stack to obtain the attached IFood capability
     */
    default void addStats(ItemStack stack)
    {
        IFood foodCap = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (foodCap != null)
        {
            addStats(foodCap);
        }
        else
        {
            TerraFirmaCraft.getLog().info("Player ate a weird food: {} / {} that was missing a food capability! This is likely the error of an addon!", stack.getItem(), stack);
        }
    }

    /**
     * Add food stats directly from a food capability instance
     *
     * Use this for when passing in a stack is undesirable.
     *
     * @param foodCap The food capability to add to the current state
     */
    void addStats(IFood foodCap);

    float getHealthModifier();

    float getThirst();

    void setThirst(float value);

    @Nonnull
    NutritionStats getNutrition();

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
    default void addThirst(float value)
    {
        setThirst(getThirst() + value);
    }

    /**
     * Resets cooldown to prevent arm swinging in client when it attempts to drink water
     * Client also needs to update cooldown after a sucessful drink attempt
     */
    void resetCooldown();
}
