/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * A layer which transforms a single previous layer.
 * Most implementations will query local values of the previous layer.
 */
public interface TransformLayer
{
    default AreaFactory apply(AreaContext context, AreaFactory prev)
    {
        return () -> {
            final Area prevArea = prev.get();
            return context.createArea((x, z) -> {
                context.initSeed(x, z);
                return apply(context, prevArea, x, z);
            });
        };
    }

    int apply(AreaContext context, Area area, int x, int z);
}
