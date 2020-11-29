package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.INLAND_MARKER;
import static net.dries007.tfc.world.layer.TFCLayerUtil.NULL_MARKER;

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
        if (TFCLayerUtil.isOceanOrMarker(north) || TFCLayerUtil.isOceanOrMarker(west) || TFCLayerUtil.isOceanOrMarker(south) || TFCLayerUtil.isOceanOrMarker(east))
        {
            return NULL_MARKER;
        }
        return INLAND_MARKER;
    }
}
