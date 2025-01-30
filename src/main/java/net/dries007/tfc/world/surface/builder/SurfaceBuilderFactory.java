/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.Seed;

/**
 * A surface builder template.
 *
 * @see Invariant for surface builders that have no internal state
 */
public interface SurfaceBuilderFactory
{
    SurfaceBuilder apply(Seed seed);

    interface Invariant extends SurfaceBuilder, SurfaceBuilderFactory
    {
        @Override
        default SurfaceBuilder apply(Seed seed)
        {
            return this;
        }
    }
}
