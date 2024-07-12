/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Predicate;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

/**
 * A side based wrapper for an arbitrary type
 * For most use cases, the default {@link Builder} can be used.
 */
public interface SidedHandler<T>
{
    /**
     * Access the specific handler for a given side.
     */
    @Nullable
    T getSidedHandler(@Nullable Direction side);

    class Builder<T> implements SidedHandler<T>
    {
        private static final int SIDES = Direction.values().length;

        private final @Nullable T internal;
        private final @Nullable T[] sidedHandlers;

        @SuppressWarnings("unchecked")
        public Builder(@Nullable T internal)
        {
            this.internal = internal;
            this.sidedHandlers = (T[]) new Object[SIDES];
        }

        public Builder<T> on(T handler, Predicate<Direction> sides)
        {
            for (Direction side : Helpers.DIRECTIONS)
            {
                if (sides.test(side))
                {
                    sidedHandlers[side.ordinal()] = handler;
                }
            }
            return this;
        }

        public Builder<T> on(T handler, Direction... sides)
        {
            for (Direction side : sides)
            {
                sidedHandlers[side.ordinal()] = handler;
            }
            return this;
        }

        @Nullable
        @Override
        public T getSidedHandler(@Nullable Direction side)
        {
            return side == null ? internal : sidedHandlers[side.ordinal()];
        }
    }

    record Noop<T>(T internal) implements SidedHandler<T>
    {
        @Override
        public T getSidedHandler(@Nullable Direction side)
        {
            return internal;
        }
    }
}
