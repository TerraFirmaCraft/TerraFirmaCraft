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
    @Nullable
    public static BarrelRecipe get(ItemStack stack, FluidStack fluidStack)
    {
        return TFCRegistries.BARREL.getValuesCollection().stream().filter(x -> x.isValidInput(fluidStack, stack)).findFirst().orElse(null);
    }

    @Nullable
    public static BarrelRecipe getInstant(ItemStack stack, FluidStack fluidStack)
    {
        return TFCRegistries.BARREL.getValuesCollection().stream().filter(x -> x.isValidInput(fluidStack, stack) && x.getDuration() == 0).findFirst().orElse(null);
    }

    private final IIngredient<ItemStack> inputStack;
    private final IIngredient<FluidStack> inputFluid;
    private final FluidStack outputFluid;
    private final ItemStack outputStack;
    private final int duration;

    public BarrelRecipe(@Nonnull IIngredient<FluidStack> inputFluid, @Nonnull IIngredient<ItemStack> inputStack, @Nullable FluidStack outputFluid, @Nonnull ItemStack outputStack, int duration)
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
    public FluidStack getOutputFluid()
    {
        return outputFluid;
    }

    @Nonnull
    public ItemStack getOutputStack()
    {
        return outputStack;
    }

    @Nullable
    public FluidStack getOutputFluid(FluidStack inputFluid, ItemStack inputStack)
    {
        int multiplier = getMultiplier(inputFluid, inputStack);
        if (outputFluid != null)
        {
            // Ignore input and replace with output
            int outputAmount = Math.min(multiplier * outputFluid.amount, TEBarrel.TANK_CAPACITY);
            return new FluidStack(outputFluid.getFluid(), outputAmount);
        }
        else
        {
            // Try and keep as much of the original input as possible
            int retainAmount = inputFluid.amount - (multiplier * this.inputFluid.getAmount());
            if (retainAmount > 0)
            {
                return new FluidStack(inputFluid.getFluid(), inputFluid.amount - (multiplier * this.inputFluid.getAmount()));
            }
        }
        return null;
    }

    @Nonnull
    public ItemStack getOutputItem(FluidStack inputFluid, ItemStack inputStack)
    {
        int multiplier = getMultiplier(inputFluid, inputStack);
        if (!this.outputStack.isEmpty())
        {
            // Ignore input and replace with output
            int outputCount = Math.min(multiplier * outputStack.getCount(), outputStack.getMaxStackSize());
            ItemStack output = outputStack.copy();
            output.setCount(outputCount);
            return output;
        }
        else
        {
            // Try and keep as much of the original input as possible
            int retainCount = inputStack.getCount() - (multiplier * this.inputStack.getAmount());
            if (retainCount > 0)
            {
                inputStack.setCount(retainCount);
                return inputStack;
            }
        }
        return ItemStack.EMPTY;
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
