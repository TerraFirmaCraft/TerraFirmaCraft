/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class IngredientFluidStack implements IIngredient<FluidStack>
{
    private final FluidStack inputFluid;

    IngredientFluidStack(@Nonnull Fluid fluid, int amount)
    {
        this(new FluidStack(fluid, amount));
    }

    IngredientFluidStack(@Nonnull FluidStack inputFluid)
    {
        this.inputFluid = inputFluid;
    }

    @Override
    public NonNullList<FluidStack> getValidIngredients()
    {
        return NonNullList.withSize(1, inputFluid.copy());
    }

    @Override
    public boolean test(FluidStack fluidStack)
    {
        return testIgnoreCount(fluidStack) && fluidStack.amount >= this.inputFluid.amount;
    }

    @Override
    public boolean testIgnoreCount(FluidStack fluidStack)
    {
        return fluidStack != null && fluidStack.getFluid() != null && fluidStack.getFluid() == this.inputFluid.getFluid();
    }

    @Override
    @Nullable
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
