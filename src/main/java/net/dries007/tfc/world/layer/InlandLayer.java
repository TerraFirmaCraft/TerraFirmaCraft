package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

/**
 * This layer pipes the initial biome generation, and marks specific areas as inland, which are allowed to generate lakes
 * It prevents lakes from generating and replacing biomes at the edge of a biome-ocean border, or near one
 */
public enum InlandLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (isAquatic(north) || isAquatic(west) || isAquatic(south) || isAquatic(east))
        {
            return NULL_MARKER;
        }
        return INLAND_MARKER;
    }

    private boolean isAquatic(int value)
    {
        return value == DEEP_OCEAN || value == DEEP_OCEAN_RIDGE || value == OCEAN_OCEAN_CONVERGING_MARKER;
    }
}
