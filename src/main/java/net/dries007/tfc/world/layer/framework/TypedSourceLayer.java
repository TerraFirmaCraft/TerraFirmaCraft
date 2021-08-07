/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * @see SourceLayer
 */
public interface TypedSourceLayer<A>
{
    default TypedAreaFactory<A> apply(AreaContext context)
    {
        return () -> context.createTypedArea((x, z) -> {
            context.initSeed(x, z);
            return apply(context, x, z);
        });
    }

    A apply(AreaContext context, int x, int z);
}
