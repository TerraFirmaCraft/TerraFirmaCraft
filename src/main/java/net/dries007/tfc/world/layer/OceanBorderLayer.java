/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.DEEP_OCEAN;
import static net.dries007.tfc.world.layer.TFCLayerUtil.OCEAN;

/**
 * Creates oceans on borders between land and deep ocean
 */
public enum OceanBorderLayer implements ICastleTransformer
{
    INSTANCE;

    @Override
    public int apply(INoiseRandom context, int north, int east, int south, int west, int center)
    {
        if (center == DEEP_OCEAN)
        {
            // Add ocean to land - deep ocean borders
            if (!TFCLayerUtil.isOceanOrMarker(north) || !TFCLayerUtil.isOceanOrMarker(east) || !TFCLayerUtil.isOceanOrMarker(south) || !TFCLayerUtil.isOceanOrMarker(west))
            {
                return OCEAN;
            }
        }
        else if (center == OCEAN)
        {
            // And in the reverse, in large sections of ocean, add deep ocean in fully ocean-locked area
            if (TFCLayerUtil.isOceanOrMarker(north) && TFCLayerUtil.isOceanOrMarker(east) && TFCLayerUtil.isOceanOrMarker(west) && TFCLayerUtil.isOceanOrMarker(south))
            {
                return DEEP_OCEAN;
            }
        }
        return center;
    }
}