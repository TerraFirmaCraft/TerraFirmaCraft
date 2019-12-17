/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes.barrel;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientFluidItem;
import net.dries007.tfc.util.Helpers;

public class BarrelRecipeFluidMixing extends BarrelRecipe
{
    public BarrelRecipeFluidMixing(@Nonnull IIngredient<FluidStack> inputFluid, @Nonnull IngredientFluidItem inputStack, @Nullable FluidStack outputFluid, int duration)
    {
        super(inputFluid, inputStack, outputFluid, ItemStack.EMPTY, duration);
    }

    @Override
    public boolean isValidInputInstant(ItemStack inputStack, @Nullable FluidStack inputFluid)
    {
        // Used on instant recipes, to verify that they only convert if there exists enough items to fully convert the fluid
        FluidStack inputStackFluid = FluidUtil.getFluidContained(inputStack);
        if (inputFluid != null && inputStackFluid != null)
        {
            return inputFluid.amount / this.inputFluid.getAmount() <= inputStackFluid.amount / this.inputStack.getAmount();
        }
        return false;
    }

    @Nullable
    @Override
    public FluidStack getOutputFluid(FluidStack inputFluid, ItemStack inputStack)
    {
        return super.getOutputFluid(inputFluid, inputStack);
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        return Helpers.listOf(inputStack.getItem().getContainerItem(inputStack));
    }

    @Override
    protected int getMultiplier(FluidStack inputFluid, ItemStack inputStack)
    {
        if (isValidInput(inputFluid, inputStack))
        {
            FluidStack inputStackFluid = FluidUtil.getFluidContained(inputStack);
            if (inputStackFluid != null)
            {
                return Math.min(inputFluid.amount / this.inputFluid.getAmount(), inputStackFluid.amount / this.inputStack.getAmount());
            }
        }
        return 0;
    }
}
