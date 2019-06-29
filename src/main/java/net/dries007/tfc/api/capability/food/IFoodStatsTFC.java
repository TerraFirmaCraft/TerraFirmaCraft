/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public interface IFoodStatsTFC
{
    void addStats(@Nonnull ItemFood foodItem, @Nonnull ItemStack stack);

    void addStats(int hungerAmount, float saturationAmount);

    int getFoodLevel();

    boolean needFood();

    void addExhaustion(float exhaustion);

    float getHealthModifier();

    float getThirst();

    boolean attemptDrink(float value);

    /**
     * Sets the nutrient value directly. Used by command nutrients and for debug purposes
     *
     * @param nutrient the nutrient to set
     * @param value    the value to set to, in [0, 100]
     */
    void setNutrient(@Nonnull Nutrient nutrient, float value);

    float getNutrient(@Nonnull Nutrient nutrient);
}
