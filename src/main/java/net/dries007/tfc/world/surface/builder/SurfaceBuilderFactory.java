/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

/**
 * A surface builder template.
 *
 * @see Invariant for surface builders that have no internal state
 */
public interface SurfaceBuilderFactory
{
    SurfaceBuilder apply(long seed);

    interface Invariant extends SurfaceBuilder, SurfaceBuilderFactory
    {
        @Override
        default SurfaceBuilder apply(long seed)
        {
            return this;
        }
    }
}
