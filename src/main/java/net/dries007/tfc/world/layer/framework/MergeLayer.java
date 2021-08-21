/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.framework;

/**
 * A layer which merges two different layers, forming a layer tree.
 */
public interface MergeLayer
{
    default AreaFactory apply(long seed, AreaFactory first, AreaFactory second)
    {
        return () -> {
            final AreaContext context = new AreaContext(seed);
            final Area firstArea = first.get();
            final Area secondArea = second.get();
            return new Area((x, z) -> {
                context.setSeed(x, z);
                return apply(context, firstArea, secondArea, x, z);
            }, 1024);
        };
    }

    int apply(AreaContext context, Area first, Area second, int x, int z);
}
