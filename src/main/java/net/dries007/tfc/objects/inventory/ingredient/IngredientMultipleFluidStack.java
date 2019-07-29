/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class IngredientMultipleFluidStack implements IIngredient<FluidStack>
{
    private final int amount;
    private final Fluid[] fluids;

    IngredientMultipleFluidStack(int amount, Fluid... fluids)
    {
        this.amount = amount;
        this.fluids = fluids;
    }

    @Override
    public boolean test(FluidStack input)
    {
        return testIgnoreCount(input) && input.amount >= this.amount;
    }

    @Override
    public boolean testIgnoreCount(FluidStack input)
    {
        if (input != null && input.getFluid() != null)
        {
            for (Fluid fluid : fluids)
            {
                if (fluid == input.getFluid())
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public FluidStack consume(FluidStack input)
    {
        if (input.amount > this.amount)
        {
            return new FluidStack(input.getFluid(), input.amount - this.amount);
        }
        return null;
    }

    @Override
    public int getAmount()
    {
        return amount;
    }
}
