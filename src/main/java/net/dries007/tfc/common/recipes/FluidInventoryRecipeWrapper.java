package net.dries007.tfc.common.recipes;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class FluidInventoryRecipeWrapper extends RecipeWrapper
{
    protected FluidStack inputFluid;

    public FluidInventoryRecipeWrapper(ItemStackHandler inventory, FluidStack inputFluid)
    {
        super(inventory);
        this.inputFluid = inputFluid;
    }

    public FluidStack getInputFluid()
    {
        return inputFluid;
    }
}
