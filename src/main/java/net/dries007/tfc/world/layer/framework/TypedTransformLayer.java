/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * @see TransformLayer
 */
public interface TypedTransformLayer<A>
{
    default TypedAreaFactory<A> apply(long seed, TypedAreaFactory<A> prev)
    {
        return () -> {
            final AreaContext context = new AreaContext(seed);
            final TypedArea<A> prevArea = prev.get();
            return new TypedArea<>((x, z) -> {
                context.setSeed(x, z);
                return apply(context, prevArea, x, z);
            }, 1024);
        };
    }

    A apply(AreaContext context, TypedArea<A> prev, int x, int z);
}
