/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.fuel;

import net.minecraft.item.ItemStack;

public class Fuel
{
    private final ItemStack stack;
    private final int amount;
    private final float temperature;

    private final boolean isForgeValid;

    public Fuel(ItemStack stack, int amount, float temperature)
    {
        this(stack, amount, temperature, false);
    }

    public Fuel(ItemStack stack, int amount, float temperature, boolean isForgeValid)
    {
        this.stack = stack;
        this.amount = amount;
        this.temperature = temperature;
        this.isForgeValid = isForgeValid;
    }

    public boolean matchesInput(Fuel fuel)
    {
        return matchesInput(fuel.stack);
    }

    public boolean matchesInput(ItemStack stack)
    {
        return this.stack.isItemEqual(stack);
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
}
