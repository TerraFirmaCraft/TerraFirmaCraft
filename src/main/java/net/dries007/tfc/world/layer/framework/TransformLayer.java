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
    default AreaFactory apply(long seed, AreaFactory prev)
    {
        return () -> {
            final AreaContext context = new AreaContext(seed);
            final Area prevArea = prev.get();
            return new Area((x, z) -> {
                context.setSeed(x, z);
                return apply(context, prevArea, x, z);
            }, 1024);
        };
    }

    int apply(AreaContext context, Area area, int x, int z);
}
