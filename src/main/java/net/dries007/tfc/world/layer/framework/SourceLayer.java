/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * The source layer of a unsealedStack of layers.
 * Computes an integer value from just the (x, z) coordinate.
 */
public interface SourceLayer
{
    default AreaFactory apply(long seed)
    {
        return () -> {
            final AreaContext context = new AreaContext(seed);
            return new Area((x, z) -> {
                context.setSeed(x, z);
                return apply(context, x, z);
            }, 1024);
        };
    }

    int apply(AreaContext context, int x, int z);
}
