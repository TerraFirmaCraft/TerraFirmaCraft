/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.recipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.te.TEBarrel;

public class BarrelRecipe extends IForgeRegistryEntry.Impl<BarrelRecipe>
{
    public static BarrelRecipe get(ItemStack stack, FluidStack fluidStack)
    {
        return TFCRegistries.BARREL.getValuesCollection().stream().filter(x -> x.isValidInput(fluidStack, stack)).findFirst().orElse(null);
    }

    private final IIngredient<ItemStack> inputStack;
    private final IIngredient<FluidStack> inputFluid;
    private final FluidStack outputFluid;
    private final ItemStack outputStack;
    private final int duration;

    public BarrelRecipe(IIngredient<FluidStack> inputFluid, IIngredient<ItemStack> inputStack, FluidStack outputFluid, ItemStack outputStack, int duration)
    {
        this.inputStack = inputStack;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.outputStack = outputStack;
        this.duration = duration;
    }

    public boolean isValidInput(FluidStack inputFluid, ItemStack inputStack)
    {
        return this.inputFluid.test(inputFluid) && this.inputStack.test(inputStack);
    }

    public int getDuration()
    {
        return duration;
    }

    @Nullable
    public FluidStack getOutputFluid(FluidStack inputFluid, ItemStack inputStack)
    {
        if (outputFluid != null)
        {
            int multiplier = getMultiplier(inputFluid, inputStack);
            int outputAmount = Math.min(multiplier * outputFluid.amount, TEBarrel.TANK_CAPACITY);
            return new FluidStack(outputFluid.getFluid(), outputAmount);
        }
        return null;
    }

    @Nonnull
    public ItemStack getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        int multiplier = getMultiplier(inputFluid, inputStack);
        int outputCount = Math.min(multiplier * outputStack.getCount(), outputStack.getMaxStackSize());
        ItemStack output = outputStack.copy();
        output.setCount(outputCount);
        return output;
    }

    private int getMultiplier(FluidStack inputFluid, ItemStack inputStack)
    {
        if (isValidInput(inputFluid, inputStack))
        {
            return Math.min(inputFluid.amount / this.inputFluid.getAmount(), inputStack.getCount() / this.inputStack.getAmount());
        }
        return 0;
    }
}
