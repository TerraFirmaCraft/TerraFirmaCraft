/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import net.minecraft.item.ItemStack;

public final class Fuel
{
    private final ItemStack stack;
    private final int amount;
    private final float temperature;

    public Fuel(ItemStack stack, int amount, float temperature)
    {
        this.stack = stack;
        this.amount = amount;
        this.temperature = temperature;
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
}
