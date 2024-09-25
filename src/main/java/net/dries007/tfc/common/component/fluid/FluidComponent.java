/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.fluid;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * An immutable representation of a fluid container which holds a certain fluid, restricted by a given predicate and capacity.
 * @param content The content of the fluid stack. <strong>Never</strong> modify this content, it should be treated as immutable!
 */
public record FluidComponent(FluidStack content)
{
    public static final FluidComponent EMPTY = new FluidComponent(FluidStack.EMPTY);

    public static final Codec<FluidComponent> CODEC = FluidStack.OPTIONAL_CODEC.xmap(FluidComponent::new, FluidComponent::content);
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidComponent> STREAM_CODEC = FluidStack.OPTIONAL_STREAM_CODEC.map(FluidComponent::new, FluidComponent::content);

    public static FluidComponent of(Fluid fluid, int amount)
    {
        return new FluidComponent(new FluidStack(fluid, amount));
    }

    /**
     * @param content The content of the container
     * @param input The input attempting to fill into this container
     * @param info Info representing the container
     * @return The result component, plus an amount which indicates how much was filled
     */
    public static FillInfo fill(FluidStack content, FluidStack input, FluidContainerInfo info)
    {
        // If we cannot contain the input, then return unmodified, and with no fill done
        if (input.isEmpty() || !info.canContainFluid(input))
        {
            return new FillInfo(content, 0);
        }
        if (content.isEmpty())
        {
            // Content is empty, and we can contain the fluid, so calculate how much we can contain up to capacity
            // Return the total value of how much was filled
            final FluidStack newContent = input.copyWithAmount(Math.min(input.getAmount(), info.fluidCapacity()));
            return new FillInfo(newContent, newContent.getAmount());
        }
        // If both are non-empty, and the fluids are not identical, we cannot contain the result
        if (!FluidStack.isSameFluidSameComponents(content, input))
        {
            return new FillInfo(content, 0);
        }

        // Otherwise, we once again, calculate the maximum amount we can contain from the sum of both amounts
        // Return the total value minus the amount that was already present
        final FluidStack newContent = content.copyWithAmount(Math.min(content.getAmount() + input.getAmount(), info.fluidCapacity()));
        return new FillInfo(newContent, newContent.getAmount() - content.getAmount());
    }

    /**
     * @param content The content of the container
     * @param amount The maximum amount to drain
     * @return The result component, plus an amount which indicates how much was drained
     */
    public static DrainInfo drain(FluidStack content, int amount)
    {
        // If we are currently empty, we cannot drain any
        if (content.isEmpty())
        {
            return new DrainInfo(content, FluidStack.EMPTY);
        }
        // Otherwise, calculate the remainder after draining, and the amount that was actually drained
        final FluidStack newContent = amount > content.getAmount() ? FluidStack.EMPTY : content.copyWithAmount(content.getAmount() - amount);
        final FluidStack newAmount = amount > content.getAmount() ? content.copy() : content.copyWithAmount(amount);
        return new DrainInfo(newContent, newAmount);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof FluidComponent that && FluidStack.matches(content, that.content);
    }

    @Override
    public int hashCode()
    {
        return FluidStack.hashFluidAndComponents(content);
    }

    @Override
    public String toString()
    {
        return "Fluid[%s mB of %s]".formatted(content.getAmount(), content.getHoverName().getString());
    }

    public record FillInfo(FluidStack content, int filled) {}
    public record DrainInfo(FluidStack content, FluidStack drained) {}
}
