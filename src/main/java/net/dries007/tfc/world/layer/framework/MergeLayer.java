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
    default AreaFactory apply(AreaContext context, AreaFactory first, AreaFactory second)
    {
        return () -> {
            final Area firstArea = first.get();
            final Area secondArea = second.get();
            return context.createArea((x, z) -> {
                context.initSeed(x, z);
                return apply(context, firstArea, secondArea, x, z);
            });
        };
    }

    int apply(AreaContext context, Area first, Area second, int x, int z);
}
