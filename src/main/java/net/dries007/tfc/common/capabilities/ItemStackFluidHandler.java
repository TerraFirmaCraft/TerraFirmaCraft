package net.dries007.tfc.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.common.items.VesselItem;

/**
 * A {@link IFluidHandler} capability provider implementation for item stacks.
 * Supports a single fluid, up to a constant capacity, with a whitelist of fluids to accept via a tag.
 */
public class ItemStackFluidHandler implements SimpleFluidHandler, IFluidHandlerItem, ICapabilityProvider
{
    private final LazyOptional<IFluidHandlerItem> capability;
    private final ItemStack stack;
    @Nullable private final Tag<Fluid> allowedFluids;
    private final int capacity;

    private boolean initialized; // If the internal capability objects have loaded their data.
    private FluidStack fluid;

    public ItemStackFluidHandler(ItemStack stack, int capacity)
    {
        this(stack, null, capacity);
    }

    public ItemStackFluidHandler(ItemStack stack, @Nullable Tag<Fluid> allowedFluids, int capacity)
    {
        this.capability = LazyOptional.of(() -> this);
        this.stack = stack;
        this.allowedFluids = allowedFluids;
        this.capacity = capacity;

        this.fluid = FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack getContainer()
    {
        return stack;
    }

    @Nonnull
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
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack)
    {
        return allowedFluids == null || allowedFluids.contains(stack.getFluid());
    }

    @Override
    public int fill(FluidStack fill, FluidAction action)
    {
        if ((fluid.isEmpty() && isFluidValid(0, fill)) || fluid.isFluidEqual(fill))
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

    @Nonnull
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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction direction)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
        {
            load();
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    /**
     * @see VesselItem.VesselCapability#load()
     */
    private void load()
    {
        if (!initialized)
        {
            initialized = true;
            fluid = FluidStack.loadFluidStackFromNBT(stack.getOrCreateTag().getCompound("fluid"));
        }
    }

    private void save()
    {
        stack.getOrCreateTag().put("fluid", fluid.writeToNBT(new CompoundTag()));
    }
}
