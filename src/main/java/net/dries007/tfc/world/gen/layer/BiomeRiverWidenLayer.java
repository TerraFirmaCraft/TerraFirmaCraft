/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.gen.layer;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

@ParametersAreNonnullByDefault
public enum BiomeRiverWidenLayer implements ICastleTransformer
{
    MEDIUM
        {
            @Override
            protected boolean isRiverCompatible(int value)
            {
                return LOW.isRiverCompatible(value) || value == TFCLayerUtil.ROLLING_HILLS || value == TFCLayerUtil.LOW_CANYONS;
            }
        },
    LOW
        {
            @Override
            protected boolean isRiverCompatible(int value)
            {
                return value == TFCLayerUtil.PLAINS || value == TFCLayerUtil.LOWLANDS || value == TFCLayerUtil.HILLS || value == TFCLayerUtil.RIVER;
            }
        };

    @Override
    public int apply(INoiseRandom context, int north, int west, int south, int east, int center)
    {
        if (isRiverCompatible(center) && center != TFCLayerUtil.RIVER)
        {
            if (north == TFCLayerUtil.RIVER && (isRiverCompatible(west) && isRiverCompatible(east) && isRiverCompatible(south)))
            {
                return TFCLayerUtil.RIVER;
            }
            else if (east == TFCLayerUtil.RIVER && (isRiverCompatible(west) && isRiverCompatible(north) && isRiverCompatible(south)))
            {
                return TFCLayerUtil.RIVER;
            }
            else if (west == TFCLayerUtil.RIVER && (isRiverCompatible(north) && isRiverCompatible(east) && isRiverCompatible(south)))
            {
                return TFCLayerUtil.RIVER;
            }
            else if (south == TFCLayerUtil.RIVER && (isRiverCompatible(west) && isRiverCompatible(east) && isRiverCompatible(north)))
            {
                return TFCLayerUtil.RIVER;
            }
        }
        return center;
    }

    protected abstract boolean isRiverCompatible(int value);
}
