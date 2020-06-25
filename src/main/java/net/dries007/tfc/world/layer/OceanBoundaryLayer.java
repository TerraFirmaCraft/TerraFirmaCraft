package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum OceanBoundaryLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (center == TFCLayerUtil.DEEP_OCEAN)
        {
            if (north == TFCLayerUtil.OCEAN || west == TFCLayerUtil.OCEAN || south == TFCLayerUtil.OCEAN || east == TFCLayerUtil.OCEAN)
            {
                return TFCLayerUtil.OCEAN;
            }
        }
        return center;
    }
}
