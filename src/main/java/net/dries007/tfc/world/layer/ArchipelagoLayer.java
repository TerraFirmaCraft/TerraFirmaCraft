package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

/**
 * Replaces instances of {@link TFCLayerUtil#OCEAN_OCEAN_CONVERGING_MARKER} with smaller islands at a lower zoom level
 */
public enum ArchipelagoLayer implements IC0Transformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int value)
    {
        if (value == OCEAN_OCEAN_CONVERGING_MARKER)
        {
            if (context.nextRandom(8) == 0)
            {
                if (context.nextRandom(3) == 0)
                {
                    return FLOODED_MOUNTAINS;
                }
                return PLAINS;
            }
            return OCEAN;
        }
        return value;
    }
}
