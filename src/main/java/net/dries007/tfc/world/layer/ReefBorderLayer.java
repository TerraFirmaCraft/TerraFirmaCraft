package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

/**
 * Operates on the {@link TFCLayerUtil#OCEAN_REEF_MARKER} markers
 * Borders reef - land with ocean, and adds ocean to reef - deep ocean borders
 */
public enum ReefBorderLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int east, int south, int west, int center)
    {
        if (center == OCEAN_REEF_MARKER)
        {
            if (!TFCLayerUtil.isOceanOrMarker(north) || !TFCLayerUtil.isOceanOrMarker(east) || !TFCLayerUtil.isOceanOrMarker(south) || !TFCLayerUtil.isOceanOrMarker(west))
            {
                return OCEAN;
            }
            return OCEAN_REEF;
        }
        else if (TFCLayerUtil.isOceanOrMarker(center) && (north == OCEAN_REEF_MARKER || east == OCEAN_REEF_MARKER || south == OCEAN_REEF_MARKER || west == OCEAN_REEF_MARKER))
        {
            return OCEAN;
        }
        return center;
    }
}
