/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public interface IFoodStatsTFC
{
    void addStats(ItemFood foodItem, ItemStack stack);

    void addStats(int hungerAmount, float saturationAmount);

    int getFoodLevel();

    boolean needFood();

    void addExhaustion(float exhaustion);

    float getHealthModifier();

    boolean attemptDrink(float value);
}
