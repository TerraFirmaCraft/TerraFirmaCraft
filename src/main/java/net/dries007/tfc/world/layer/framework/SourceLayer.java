/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * The source layer of a stack of layers.
 * Computes an integer value from just the (x, z) coordinate.
 */
public interface SourceLayer
{
    default AreaFactory apply(AreaContext context)
    {
        return () -> context.createArea((x, z) -> {
            context.initSeed(x, z);
            return apply(context, x, z);
        });
    }

    int apply(AreaContext context, int x, int z);
}
