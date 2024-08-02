/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

/**
 * A builder implementing cached, passthrough, sided access to an underlying capability. This is built on the construction of
 * each new block entity that provides sided capabilities, and is easily queried as a capability provider by {@link BlockCapabilities}
 *
 * @see PartialFluidHandler
 * @see PartialItemHandler
 * @param <T> The underlying type (fluid, item, or heat handler) that is being queried
 */
public class SidedHandler<T>
{
    private static final int SIDES = Direction.values().length;

    private final T internal;
    private final @Nullable T[] sides;

    /**
     * Create a new instance, with the given {@code internal} handler, which is the default that will be returned by {@code null} queries.
     */
    @SuppressWarnings("unchecked")
    public SidedHandler(T internal)
    {
        this.internal = internal;
        this.sides = (T[]) new Object[SIDES];
    }

    @Nullable
    public T get(@Nullable Direction side)
    {
        return side == null ? internal : sides[side.ordinal()];
    }

    public SidedHandler<T> on(Function<T, ? extends T> wrap, Predicate<Direction> sides)
    {
        return on(wrap.apply(internal), sides);
    }

    public SidedHandler<T> on(T handler, Predicate<Direction> sides)
    {
        for (Direction side : Helpers.DIRECTIONS)
        {
            if (sides.test(side))
            {
                this.sides[side.ordinal()] = handler;
            }
        }
        return this;
    }

    public SidedHandler<T> on(Function<T, ? extends T> wrap, Direction... sides)
    {
        return on(wrap.apply(internal), sides);
    }

    public SidedHandler<T> on(T handler, Direction... sides)
    {
        for (Direction side : sides)
        {
            this.sides[side.ordinal()] = handler;
        }
        return this;
    }
}
