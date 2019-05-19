/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class IngredientFluidStack implements IIngredient<FluidStack>
{
    private final FluidStack inputFluid;

    public IngredientFluidStack(Fluid fluid, int amount)
    {
        this(new FluidStack(fluid, amount));
    }

    public IngredientFluidStack(FluidStack inputFluid)
    {
        this.inputFluid = inputFluid;
    }

    @Override
    public boolean test(FluidStack fluidStack)
    {
        if (fluidStack != null && fluidStack.getFluid() != null)
        {
            return fluidStack.getFluid() == this.inputFluid.getFluid() && fluidStack.amount >= this.inputFluid.amount;
        }
        return false;
    }

    @Override
    public FluidStack consume(FluidStack input)
    {
        if (input.amount > inputFluid.amount)
        {
            return new FluidStack(input.getFluid(), input.amount - inputFluid.amount);
        }
        return null;
    }

    @Override
    public int getAmount()
    {
        if (inputFluid != null)
        {
            return inputFluid.amount;
        }
        return 0;
    }
}
