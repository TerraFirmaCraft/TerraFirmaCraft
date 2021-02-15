/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

/**
 * Replaces the final plate tectonic marker biomes with the actual biomes
 * Generates various island + volcanic formations
 */
public enum ArchipelagoLayer implements IC0Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (value == OCEAN_OCEAN_CONVERGING_MARKER)
        {
            // Ocean - Ocean Converging creates volcanic island chains on this marker
            final int r = context.random(20);
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
            if (context.random(30) == 0)
            {
                return PLAINS;
            }
            return OCEAN;
        }
        else if (value == DEEP_OCEAN)
        {
            // Deep Oceans have a chance for a volcanic hotspot
            if (context.random(250) == 0)
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
