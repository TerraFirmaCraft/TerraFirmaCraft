/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.dries007.tfc.world.layer.framework.AreaContext;
import net.dries007.tfc.world.layer.framework.CenterTransformLayer;

import static net.dries007.tfc.world.layer.TFCLayers.*;

/**
 * Replaces the final plate tectonic marker biomes with the actual biomes
 * Generates various island + volcanic formations
 */
public enum ArchipelagoLayer implements CenterTransformLayer
{
    INSTANCE;

    @Override
    public int apply(AreaContext context, int value)
    {
        if (value == OCEAN_OCEAN_CONVERGING_MARKER)
        {
            // Ocean - Ocean Converging creates volcanic island chains on this marker
            final int r = context.random().nextInt(20);
            if (r <= 1)
            {
                return VOLCANIC_OCEANIC_MOUNTAINS;
            }
            else if (r == 2)
            {
                return OCEAN_REEF;
            }
            return OCEAN;
        }
        else if (value == OCEAN_OCEAN_DIVERGING_MARKER)
        {
            // Ocean - Ocean Diverging creates mid-ocean ridges, which become ocean biomes (shallow areas)
            // Random chance for small non-volcanic islands (plains)
            if (context.random().nextInt(30) == 0)
            {
                return PLAINS;
            }
            return OCEAN;
        }
        else if (value == DEEP_OCEAN)
        {
            // Deep Oceans have a chance for a volcanic hotspot
            if (context.random().nextInt(250) == 0)
            {
                return VOLCANIC_OCEANIC_MOUNTAINS;
            }
        }
        else if (value == OCEAN)
        {
            // All oceans are initially marked as reefs, as many other oceans will be added in this phase
            // We then go back and prune this via the marker, to not appear too close to shores
            return OCEAN_REEF_MARKER;
        }
        return value;
    }
}
