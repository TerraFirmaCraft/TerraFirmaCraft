/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surface.builder;

import net.dries007.tfc.world.surface.SurfaceBuilderContext;

/**
 * A configured, per chunk generator instance of a surface builder
 * Responsible for building a single column, with all relevant info contained in the context.
 */
public interface SurfaceBuilder
{
    void buildSurface(SurfaceBuilderContext context, int startY, int endY);
}
