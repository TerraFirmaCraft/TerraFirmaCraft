/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.fuel;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.inventory.ingredient.IIngredient;

public class Fuel
{
    private final IIngredient<ItemStack> ingredient;
    private final int amount;
    private final float temperature;

    private final boolean isForgeValid, isBloomeryValid;

    public Fuel(IIngredient<ItemStack> ingredient, int amount, float temperature)
    {
        this(ingredient, amount, temperature, false, false);
    }

    public Fuel(IIngredient<ItemStack> ingredient, int amount, float temperature, boolean isForgeValid, boolean isBloomeryValid)
    {
        this.ingredient = ingredient;
        this.amount = amount;
        this.temperature = temperature;
        this.isForgeValid = isForgeValid;
        this.isBloomeryValid = isBloomeryValid;
    }

    /**
     * Check if at least one itemstack from both fuel obj match
     *
     * @param fuel the other fuel to compare
     * @return true if at least one itemstack is equal
     */
    public boolean matchesInput(Fuel fuel)
    {
        for (ItemStack stack : fuel.ingredient.getValidIngredients())
        {
            if (matchesInput(stack))
            {
                return true;
            }
        }
        return false;
    }

    public boolean matchesInput(ItemStack stack)
    {
        return ingredient.testIgnoreCount(stack);
    }

    public int getAmount()
    {
        return amount;
    }

    public float getTemperature()
    {
        return temperature;
    }

    public boolean isForgeFuel()
    {
        return isForgeValid;
    }

    public boolean isBloomeryFuel()
    {
        return isBloomeryValid;
    }
}
