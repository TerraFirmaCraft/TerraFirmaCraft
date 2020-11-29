package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

/**
 * Replaces the final plate tectonic marker biomes with the actual biomes
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
            if (context.nextRandom(4) == 0)
            {
                return VOLCANIC_OCEANIC_MOUNTAINS;
            }
            return OCEAN;
        }
        else if (value == OCEAN_OCEAN_DIVERGING_MARKER)
        {
            // Ocean - Ocean Diverging can create underwater rifts - here just creates ocean (with rare non-volcanic islands)
            if (context.nextRandom(30) == 0)
            {
                return PLAINS;
            }
            return OCEAN;
        }
        else if (value == DEEP_OCEAN)
        {
            // Deep Oceans have the volcanic hotspot
            if (context.nextRandom(250) == 0)
            {
                return VOLCANIC_OCEANIC_MOUNTAINS;
            }
        }
        return value;
    }
}
