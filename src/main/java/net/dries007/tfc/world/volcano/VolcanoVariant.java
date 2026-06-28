/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.volcano;

import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.surface.SurfaceBuilderContext;

/**
 * Interface for different styles of a single volcano type. Used by stratovolcanoes to have a wider variety of shapes
 */
public interface VolcanoVariant
{
    /**
     * @return name of this variant, needed for feature placements
     */
    String name();

    /**
     * @return final surface height for the column
     */
    default double getHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return heightIn;
    }

    /**
     * @return max height of solid earth, not including glaciers or water added by surface builders
     */
    default double getLandHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return heightIn;
    }

    /**
     * @return max height of fluid added by surface builder, ex. Crater Lake variant
     */
    default double getFluidHeight(double heightIn, int x, int z, double maxDiam, double biomeScaleHeight, double biomeBaseHeight, Cellular2D.Cell cell)
    {
        return 0;
    }

    /**
     * @return {@code true} if the default surface builder for the biome should be cancelled. Otherwise, it will run after this and may override it.
     */
    boolean buildSurface(SurfaceBuilderContext context, int oceanFloorHeight, int preVolcanicHeight, CenteredFeatureNoiseSampler sampler);
}
