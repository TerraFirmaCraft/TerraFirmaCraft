/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.common.items.VesselItem;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link IFluidHandler} capability provider implementation for item stacks.
 * Supports a single fluid, up to a constant capacity, with a whitelist of fluids to accept via a tag.
 */
public class ItemStackFluidHandler implements SimpleFluidHandler, IFluidHandlerItem, ICapabilityProvider
{
    private final LazyOptional<IFluidHandlerItem> capability;
    private final ItemStack stack;
    private final Predicate<Fluid> allowedFluids;
    protected final int capacity;

    private boolean initialized; // If the internal capability objects have loaded their data.
    protected FluidStack fluid;

    public ItemStackFluidHandler(ItemStack stack, TagKey<Fluid> allowedFluids, int capacity)
    {
        this(stack, fluid -> Helpers.isFluid(fluid, allowedFluids), capacity);
    }

    public ItemStackFluidHandler(ItemStack stack, Predicate<Fluid> allowedFluids, int capacity)
    {
        this.capability = LazyOptional.of(() -> this);
        this.stack = stack;
        this.allowedFluids = allowedFluids;
        this.capacity = capacity;

        this.fluid = FluidStack.EMPTY;
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
        return capacity;
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
            final int filled = Math.min(capacity - fluid.getAmount(), fill.getAmount());
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

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction)
    {
        if (cap == Capabilities.FLUID || cap == Capabilities.FLUID_ITEM)
        {
            load();
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    /**
     * @see VesselItem.VesselCapability#load()
     */
    protected void load()
    {
        if (!initialized)
        {
            initialized = true;
            fluid = FluidStack.loadFluidStackFromNBT(stack.getOrCreateTag().getCompound("fluid"));
        }
    }

    protected void save()
    {
        stack.getOrCreateTag().put("fluid", fluid.writeToNBT(new CompoundTag()));
    }
}
