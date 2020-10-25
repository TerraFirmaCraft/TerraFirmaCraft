package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IBishopTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.RIVER_MARKER;

/**
 * Expands harsh river vertexes by filling in acute angles
 * This avoids sudden sections of very thin rivers which can lead to bottoming off / cut offs in noise generation later
 */
public enum RiverAcuteVertexLayer implements IBishopTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int southWest, int southEast, int northWest, int northEast, int center)
    {
        if (center != RIVER_MARKER)
        {
            int riverCount = 0;
            if (southWest == RIVER_MARKER)
            {
                riverCount++;
            }
            if (southEast == RIVER_MARKER)
            {
                riverCount++;
            }
            if (northWest == RIVER_MARKER)
            {
                riverCount++;
            }
            if (northEast == RIVER_MARKER)
            {
                riverCount++;
            }
            if (riverCount >= 3)
            {
                return RIVER_MARKER;
            }
        }
        return center;
    }
}
