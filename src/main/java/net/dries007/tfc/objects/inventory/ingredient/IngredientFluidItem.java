/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.inventory.ingredient;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class IngredientFluidItem implements IIngredient<ItemStack>
{
    private final FluidStack stack;

    public IngredientFluidItem(@Nonnull Fluid fluid, int amount)
    {
        this(new FluidStack(fluid, amount));
    }

    public IngredientFluidItem(@Nonnull FluidStack stack)
    {
        this.stack = stack;
    }

    @Override
    public NonNullList<ItemStack> getValidIngredients()
    {
        return null;
    }

    @Override
    public boolean test(ItemStack input)
    {
        FluidStack result = FluidUtil.getFluidContained(input);
        return result != null && result.amount >= stack.amount && result.getFluid() == stack.getFluid();
    }

    @Override
    public ItemStack consume(ItemStack input)
    {
        IFluidHandler cap = input.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (cap != null)
        {
            cap.drain(stack.copy(), true);
        }
        return input;
    }

    @Override
    public int getAmount()
    {
        return stack.amount;
    }
}
