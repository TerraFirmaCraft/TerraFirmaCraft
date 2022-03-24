/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.util.Helpers;

/**
 * A side based wrapper for an arbitrary type
 * For most use cases, the default {@link Builder} can be used.
 */
public interface SidedHandler<T>
{
    /**
     * Access the specific handler for a given side.
     * The side is interpreted to be the same as in {@link net.minecraftforge.common.capabilities.ICapabilityProvider#getCapability(Capability, Direction)}
     */
    T getSidedHandler(@Nullable Direction side);

    class Builder<T> implements SidedHandler<LazyOptional<T>>
    {
        private static final int SIDES = Direction.values().length;

        private final LazyOptional<T> internal;
        private final LazyOptional<T>[] sidedHandlers;
        private final List<LazyOptional<T>> handlers;

        @SuppressWarnings("unchecked")
        public Builder(T internal)
        {
            this.internal = LazyOptional.of(() -> internal);
            this.sidedHandlers = (LazyOptional<T>[]) new LazyOptional[SIDES];
            this.handlers = new ArrayList<>();
            this.handlers.add(this.internal);
        }

        public void invalidate()
        {
            handlers.forEach(LazyOptional::invalidate);
        }

        public Builder<T> on(T handler, Predicate<Direction> sides)
        {
            final LazyOptional<T> optional = LazyOptional.of(() -> handler);
            for (Direction side : Helpers.DIRECTIONS)
            {
                if (sides.test(side))
                {
                    sidedHandlers[side.ordinal()] = optional;
                }
            }
            handlers.add(optional);
            return this;
        }

        public Builder<T> on(T handler, Direction... sides)
        {
            final LazyOptional<T> optional = LazyOptional.of(() -> handler);
            for (Direction side : sides)
            {
                sidedHandlers[side.ordinal()] = optional;
            }
            handlers.add(optional);
            return this;
        }

        @Override
        public LazyOptional<T> getSidedHandler(@Nullable Direction side)
        {
            if (side == null)
            {
                return internal;
            }
            final LazyOptional<T> sided = sidedHandlers[side.ordinal()];
            return sided == null ? LazyOptional.empty() : sided;
        }
    }

    class Noop<T> implements SidedHandler<LazyOptional<T>>
    {
        private final LazyOptional<T> internal;

        public Noop(T internal)
        {
            this.internal = LazyOptional.of(() -> internal);
        }

        public void invalidate()
        {
            internal.invalidate();
        }

        @Override
        public LazyOptional<T> getSidedHandler(@Nullable Direction side)
        {
            return internal;
        }
    }
}
