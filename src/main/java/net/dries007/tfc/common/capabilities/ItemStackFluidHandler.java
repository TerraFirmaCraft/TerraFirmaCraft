/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.util.Helpers;

/**
 * A {@link IFluidHandler} capability provider implementation for item stacks.
 * Supports a single fluid, up to a constant capacity, with a whitelist of fluids to accept via a tag.
 */
public class ItemStackFluidHandler implements SimpleFluidHandler, IFluidHandlerItem
{
    private final ItemStack stack;
    private final Predicate<Fluid> allowedFluids;
    protected final Supplier<Integer> capacity;

    private boolean initialized; // If the internal capability objects have loaded their data.
    protected FluidStack fluid;

    public ItemStackFluidHandler(ItemStack stack, TagKey<Fluid> allowedFluids, Supplier<Integer> capacity)
    {
        this(stack, fluid -> Helpers.isFluid(fluid, allowedFluids), capacity);
    }

    public ItemStackFluidHandler(ItemStack stack, Predicate<Fluid> allowedFluids, Supplier<Integer> capacity)
    {
        this.stack = stack;
        this.allowedFluids = allowedFluids;
        this.capacity = capacity;

        this.fluid = FluidStack.EMPTY;

        load();
    }

    @NotNull
    @Override
    public ItemStack getContainer()
    {
        return stack;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank)
    {
        return fluid;
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return capacity.get();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
    {
        return allowedFluids.test(stack.getFluid());
    }

    @Override
    public int fill(FluidStack fill, FluidAction action)
    {
        if (isFluidValid(0, fill) && (fluid.isFluidEqual(fill) || fluid.isEmpty()))
        {
            final int filled = Math.min(capacity.get() - fluid.getAmount(), fill.getAmount());
            final int total = fluid.getAmount() + filled;
            if (action.execute())
            {
                fluid = fill.copy();
                fluid.setAmount(total);
                save();
            }
            return filled;
        }
        return 0;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (!fluid.isEmpty())
        {
            final int drained = Math.min(fluid.getAmount(), maxDrain);
            final FluidStack result = fluid.copy();
            result.setAmount(drained);
            if (action.execute())
            {
                fluid.shrink(drained);
                save();
            }
            return result;
        }
        return FluidStack.EMPTY;
    }

    protected void load()
    {
        if (!initialized)
        {
            initialized = true;
            fluid = stack.getTag() == null ? FluidStack.EMPTY : FluidStack.loadFluidStackFromNBT(stack.getTag().getCompound("fluid"));
        }
    }

    protected void save()
    {
        if (fluid.isEmpty())
        {
            stack.removeTagKey("fluid");
        }
        else
        {
            stack.addTagElement("fluid", fluid.writeToNBT(new CompoundTag()));
        }
    }
}
